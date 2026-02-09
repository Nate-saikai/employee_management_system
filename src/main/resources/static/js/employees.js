let currentEditEmployeeId = null;
let employeesCache = [];

// Pagination variables
let currentPage = 0;
const pageSize = 10;
let totalPages = 0;

async function loadEmployeesPage(page = 0) {
    const app = document.getElementById("app");
    app.innerHTML = `<h2>Loading employees...</h2>`;

    try {
        // Fetch all employees initially
        const employees = await fetchEmployees(`/user/employees/all?page=${page}&size=${pageSize}`);
        if (employees === null) return;
        renderEmployees(employees);


    } catch (err) {
        app.innerHTML = `<p class="error">Error: ${err.message}</p>`;
    }
}

// Helper to fetch employees
async function fetchEmployees(url) {
    const app = document.getElementById("app");

    const response = await fetch(url, { credentials: "same-origin" });

    if (response.status === 401) {
        app.innerHTML = `<p class="error">Session Expired. Please log in again.</p>`;
        return null;
    }

    if (response.status === 403) {
        app.innerHTML = `<p class="error">You do not have permission to access this resource.</p>`;
        return null;
    }

    const data = await response.json();

    if (!response.ok) throw new Error(data.error || data);

    if (data.totalPages !== undefined) {
        totalPages = data.totalPages;
    } else {
        totalPages = (data.content && data.content.length > 0) ? 1 : 0;
    }
    return data.content || data;
}

async function loadDashboard() {
    try {
        const ageResponse = await fetch("/user/employees/aveAge", { credentials: "same-origin" });
        const salaryResponse = await fetch("/user/employees/aveSalary", { credentials: "same-origin" });

        if (ageResponse.ok) {
            const age = await ageResponse.text(); // backend returns plain number/string
            document.getElementById("avg-age").textContent = `Average Employee Age: ${age}`;
        }

        if (salaryResponse.ok) {
            const salary = await salaryResponse.text();
            document.getElementById("avg-salary").textContent = `Average Employee Salary: ${salary}`;
        }
    } catch (err) {
        console.error("Error loading dashboard:", err);
    }
}

function nextPage() {
    loadEmployeesPage(currentPage + 1);
}

function prevPage() {
    if (currentPage > 0) {
        loadEmployeesPage(currentPage - 1);
    }
}

function sanitizeInput(value) {
    return value.replace(/[^A-Za-z0-9- ]/g, "");
}

// Create a global toast function
function showToast(message, type = "error") {
    let container = document.getElementById("toast-container");
    if (!container) {
        container = document.createElement("div");
        container.id = "toast-container";
        document.body.appendChild(container);
    }

    // Clear previous toast (single mode)
    container.innerHTML = "";

    const toast = document.createElement("div");
    toast.textContent = message;
    toast.className = `toast ${type}`;
    container.appendChild(toast);

    // Auto-remove after 3 seconds
    setTimeout(() => toast.remove(), 3000);
}


// Render the employee table with filters
function renderEmployees(employees) {
    employeesCache = employees.content || employees;
    const app = document.getElementById("app");

    let html = `
        <section class="dashboard">
            <h2>Dashboard</h2>
            <div class="dashboard-values">
                <div id="avg-age">Average Age: …</div>
                <div id="avg-salary">Average Salary: …</div>
            </div>
        </section>

        <section class="employees-list">
            <h2>Employees</h2>
            <div class="filters">
                <label>Filter by Age:
                    <input type="number" id="filter-age" min="18" placeholder="Exact age">
                    <button onclick="filterByAge()">Apply</button>
                </label>
                <label>Filter by Department:
                    <input type="text" id="filter-department" placeholder="Department name">
                    <button onclick="filterByDepartment()">Apply</button>
                </label>
                <button onclick="loadEmployeesPage()">Reset Filters</button>
                <button class="btn primary" onclick="openAddModal()">Add Employee</button>
            </div>

            <table>
                <thead>
                    <tr>
                        <th>Employee ID</th>
                        <th>Name</th>
                        <th>Department</th>
                        <th>Date of Birth</th>
                        <th>Salary</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
    `;

    loadDashboard();

    employees.forEach(emp => {
        html += `
            <tr>
                <td>${emp.employeeId}</td>
                <td>${emp.name}</td>
                <td>${emp.department ? emp.department.departmentName : 'N/A'}</td>
                <td>${emp.dateOfBirth}</td>
                <td>${emp.salaryAmount}</td>
                <td>
                    <button onclick="openEditModal('${emp.employeeId}')">Edit</button>
                    <button onclick="removeEmployee('${emp.employeeId}')">Remove</button>
                </td>
            </tr>
        `;
    });

    html += `
                </tbody>
            </table>
            <div class="pagination">
                <button onclick="prevPage()"
                    ${currentPage === 0 || !employeesCache || employeesCache.length === 0 ? "disabled" : ""}>
                    Previous
                </button>
                <span>Page ${totalPages === 0 ? 0 : currentPage + 1} of ${totalPages}</span>
                <button onclick="nextPage()"
                    ${currentPage >= totalPages - 1 || !employeesCache || employeesCache.length === 0 ? "disabled" : ""}>
                    Next
                </button>
            </div>
        </section>

        <!-- Update Modal -->
        <div id="edit-modal" class="modal" style="display:none;">
            <div class="modal-content">
                <span class="close" onclick="closeEditModal()">&times;</span>
                <h3>Edit Employee</h3>
                <form id="update-form">
                    <label>Name</label>
                    <input type="text" name="name" pattern="[A-Za-z0-9-]+" title="Only letters and numbers allowed" required />
                    <label>Date of Birth</label>
                    <input type="date" name="dateOfBirth" required />
                    <label>Salary</label>
                    <input type="number" step="any" name="salary"/>
                    <label>Department</label>
                    <input type="text" name="departmentName"/>
                    <button class="btn primary" type="submit">Update</button>
                </form>
            </div>
        </div>

        <!-- Add Modal -->
        <div id="add-modal" class="modal" style="display:none;">
            <div class="modal-content">
                <span class="close" onclick="closeAddModal()">&times;</span>
                <h3>Add Employee</h3>
                <form id="add-form">
                    <label>Employee ID</label>
                    <input type="text" name="employeeId" pattern="[A-Za-z0-9]+" required />
                    <label>Name</label>
                    <input type="text" name="name" pattern="[A-Za-z0-9]+" required />
                    <label>Date of Birth</label>
                    <input type="date" name="dateOfBirth" id="dateField" required />
                    <label>Salary</label>
                    <input type="number" step="any" min=0 name="salary"/>
                    <label>Department</label>
                    <input type="text" name="departmentName"/>
                    <button class="btn primary" type="submit">Add</button>
                </form>
            </div>
        </div>
    `;

    app.innerHTML = html;

    if (!employeesCache|| employeesCache.length === 0) {
       app.innerHTML = html += `<p class="error">No Employees Found!</p>`;
    }

    /* ------------------------------TEXT VALIDATION-----------------------------------*/
    const inputs = document.querySelectorAll("input[type=text]");
    inputs.forEach(input => {
        input.addEventListener("input", () => {
            const original = input.value; const sanitized = sanitizeInput(original);
            if (original !== sanitized) {
                input.value = sanitized;
                showToast(`Invalid character removed from ${input.name}. Only letters, numbers, and hyphens allowed.`);
            }
        });
    });

    /*------------------------------DATE RESTRICTION------------------------------------*/
    // Get today's date in YYYY-MM-DD format
    const today = new Date();
    const yyyy = today.getFullYear() - 21;
    const mm = String(today.getMonth() + 1).padStart(2, '0'); // Months are 0-based
    const dd = String(today.getDate()).padStart(2, '0');

    const maxDate = `${yyyy}-${mm}-${dd}`;

    // Set the max attribute of the date input
    const dateInputs = document.querySelectorAll("input[type=date]");
    dateInputs.forEach(dateInput => {
        dateInput.setAttribute('max', maxDate);
    });

    // Modal Update Listener
    document.getElementById("update-form").onsubmit = function(e) {
        e.preventDefault();
        updateEmployee();
    };

    // Modal Update Listener
    document.getElementById("add-form").onsubmit = function(e) {
        e.preventDefault();
        console.log("add button clicked");
        addEmployee();
    };

}


// Filter by age
async function filterByAge() {
    const age = document.getElementById("filter-age").value;
    if (!age) return alert("Enter an age to filter.");

    try {
        const employees = await fetchEmployees(`/user/employees/all/age?age=${age}&page=0&size=10`);

        if (employees === null) {
            return null;
        }

        renderEmployees(employees);
    } catch (err) {
        alert("Error: " + err.message);
    }
}

// Filter by department
async function filterByDepartment() {
    const dept = document.getElementById("filter-department").value.trim();
    if (!dept) return alert("Enter a department to filter.");

    try {
        const employees = await fetchEmployees(`/user/employees/all/department?department=${encodeURIComponent(dept)}&page=0&size=10`);
    } catch (err) {
        showToast("Error: " + err.message);
    }

    renderEmployees(employees);
}

async function addEmployee() {
    try {
        const form = document.getElementById("add-form");

        const payload = {
            employeeId: form.employeeId.value,
            name: form.name.value,
            dateOfBirth: form.dateOfBirth.value,
            salary: form.salary.value === "" ? null : parseFloat(form.salary.value),
            departmentName: form.departmentName.value
        }

        const response = await fetch(`/user/employee/add`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload),
            credentials: "same-origin"
        });

        if (!response.ok) {
            showToast("Failed to add employee!");
        }

        closeAddModal();

        loadEmployeesPage();

    } catch (err) {
        alert("Error adding employees: " + err.message);
    }
}

async function updateEmployee() {
    try {
        const form = document.getElementById("update-form");

        // Convert to JSON
        const payload = {
            name: form.name.value,
            dateOfBirth: form.dateOfBirth.value,
            salary: form.salary.value === "" ? null : parseFloat(form.salary.value),
            departmentName: form.departmentName.value
        };

        const response = await fetch(`/user/employee/update?employeeId=${currentEditEmployeeId}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload),
            credentials: "same-origin"
        });

        if (!response.ok) {
            showToast(response.error);
        }

        closeEditModal();

        loadEmployeesPage();

    } catch (err) {
        alert("Error updating employee: " + err.message);
    }
}


async function removeEmployee(employeeId) {
    const response = await fetch(`/user/employee/delete?employeeId=${employeeId}`, {
        method: "DELETE",
        credentials: "same-origin"
    });

    if (!response.ok) {
        showToast(response.error);
    }

    const data = await response.json();

    showToast("Deleted: Employee Code: " + data.employeeId + ", " + data.name);

    await loadEmployeesPage();
}

function openEditModal(employeeId) {
    const modal = document.getElementById("edit-modal");
    modal.style.display = "flex";

    currentEditEmployeeId = employeeId;

    const emp = employeesCache.find(e => e.employeeId === employeeId);

    if (!emp) {
        console.warn("Employee not in cache");
        return;
    }

    const form = document.getElementById("update-form");
    form.elements["name"].value = emp.name;
    form.elements["dateOfBirth"].value = emp.dateOfBirth;
    form.elements["salary"].value = parseFloat(emp.salary);
    form.elements["departmentName"].value = emp.department ? emp.department.departmentName : "";

    console.log("Open edit modal for employee code:", employeeId);
}

function closeEditModal() {
    const modal = document.getElementById("edit-modal");
    modal.style.display = "none";
}

function openAddModal() {
    const modal = document.getElementById("add-modal");
    modal.style.display = "flex";
}

function closeAddModal() {
    const modal = document.getElementById("add-modal");
    modal.style.display = "none";
}


