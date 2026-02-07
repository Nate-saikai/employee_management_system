async function loadEmployeesPage() {
    const app = document.getElementById("app");
    app.innerHTML = `<h2>Loading employees...</h2>`;

    try {
        // Fetch all employees initially
        const employees = await fetchEmployees("/user/employees/all?page=0&size=10");
        renderEmployees(employees);


    } catch (err) {
        app.innerHTML = `<p class="error">Error: ${err.message}</p>`;
    }
}

// Helper to fetch employees
async function fetchEmployees(url) {
    const response = await fetch(url, { credentials: "same-origin" });

    const app = document.getElementById("app");

    if (response.status === 401) {
        app.innerHTML = `<p class="error">Session Expired. Please log in again.</p>`;
        return null;
    }

    if (response.status === 403) {
        app.innerHTML = `<p class="error">You do not have permission to access this resource.</p>`;
        return null;
    }

    if (!response.ok) throw new Error("Failed to fetch employees");
    const data = await response.json();
    return data.content || data;
}

// Render the employee table with filters
function renderEmployees(employees) {
    const app = document.getElementById("app");

    // Create filter UI
    let html = `
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
            </div>

            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Department</th>
                        <th>Date of Birth</th> <!-- was previously age -->
                        <th>Salary</th>
                    </tr>
                </thead>
                <tbody>
    `;

    employees.forEach(emp => {
        const dob = new Date(emp.dateOfBirth);
        const age = new Date().getFullYear() - dob.getFullYear(); // useful if age need be presented
        html += `
            <tr onclick="navigateToEmployee(${emp.personId})" style="cursor:pointer">
                <td>${emp.employeeId}</td>
                <td>${emp.name}</td>
                <td>${emp.department ? emp.department.departmentName : 'N/A'}</td>
                <td>${emp.dateOfBirth}</td>
                <td>${emp.salaryAmount}</td>
            </tr>
        `;
    });

    html += `
                </tbody>
            </table>
        </section>
    `;

    app.innerHTML = html;
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
        renderEmployees(employees);
    } catch (err) {
        alert("Error: " + err.message);
    }
}
