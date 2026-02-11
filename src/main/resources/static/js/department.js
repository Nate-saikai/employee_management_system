const API_BASE = "/admin/departments";

let currentEditDepartmentId = null;
let departmentsCache = [];

// Pagination variables
let currentDeptPage = 0;
const deptPageSize = 5;
let deptTotalPages = 0;

async function loadDepartmentsPage(page = 0) {
    const app = document.getElementById("app");
    app.innerHTML = `<h2>Loading departments...</h2>`;

    try {
        const departments = await fetchDepartments(`${API_BASE}/all?page=${page}&size=${deptPageSize}`);
        if (departments === null) return;
        renderDepartments(departments);
    } catch (err) {
        app.innerHTML = `<p class="error">Error: ${err.message}</p>`;
    }
}

async function fetchDepartments(url) {
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
        deptTotalPages = data.totalPages;
    } else {
        deptTotalPages = (data.content && data.content.length > 0) ? 1 : 0;
    }
    return data.content || data;
}

function nextDeptPage() {
    loadDepartmentsPage(currentDeptPage + 1);
    currentDeptPage += 1;
}

function prevDeptPage() {
    if (currentDeptPage > 0) {
        loadDepartmentsPage(currentDeptPage - 1);
        currentDeptPage -= 1;
    }
}

function showDeptToast(message, type = "error") {
    let container = document.getElementById("toast-container");
    if (!container) {
        container = document.createElement("div");
        container.id = "toast-container";
        document.body.appendChild(container);
    }

    container.innerHTML = "";

    const toast = document.createElement("div");
    toast.textContent = message;
    toast.className = `toast ${type}`;
    container.appendChild(toast);

    setTimeout(() => toast.remove(), 3000);
}

function renderDepartments(departments) {
    departmentsCache = departments.content || departments;
    const app = document.getElementById("app");

    let html = `
        <section class="departments-list">
            <a href="/employees" class="btn secondary" onclick="navigate(event)">Back to Employees</a>
            <h2>Departments</h2>
            <div class="filters">
                <label>Search Department:
                    <input type="text" id="search-department" placeholder="Department name">
                    <button onclick="searchDepartment()">Search</button>
                </label>
                <button onclick="loadDepartmentsPage()">Reset</button>
                <button class="btn primary" onclick="openAddDeptModal()">Add Department</button>
            </div>

            <table>
                <thead>
                    <tr>
                        <th>Department ID</th>
                        <th>Name</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
    `;

    departments.forEach(dept => {
        html += `
            <tr>
                <td>${dept.departmentId}</td>
                <td>${dept.departmentName}</td>
                <td>
                    <button onclick="openEditDeptModal('${dept.departmentId}')">Edit</button>
                    <button onclick="removeDepartment('${dept.departmentId}')">Remove</button>
                </td>
            </tr>
        `;
    });

    html += `
                </tbody>
            </table>
            <div class="pagination">
                <button onclick="prevDeptPage()"
                    ${currentDeptPage === 0 || !departmentsCache || departmentsCache.length === 0 ? "disabled" : ""}>
                    Previous
                </button>
                <span>Page ${deptTotalPages === 0 ? 0 : currentDeptPage + 1} of ${deptTotalPages}</span>
                <button onclick="nextDeptPage()"
                    ${currentDeptPage >= deptTotalPages - 1 || !departmentsCache || departmentsCache.length === 0 ? "disabled" : ""}>
                    Next
                </button>
            </div>
        </section>

        <!-- Edit Modal -->
        <div id="edit-dept-modal" class="modal" style="display:none;">
            <div class="modal-content">
                <span class="close" onclick="closeEditDeptModal()">&times;</span>
                <h3>Edit Department</h3>
                <form id="update-dept-form">
                    <label>Name</label>
                    <input type="text" name="name" required />
                    <button class="btn primary" type="submit">Update</button>
                </form>
            </div>
        </div>

        <!-- Add Modal -->
        <div id="add-dept-modal" class="modal" style="display:none;">
            <div class="modal-content">
                <span class="close" onclick="closeAddDeptModal()">&times;</span>
                <h3>Add Department</h3>
                <form id="add-dept-form">
                    <label>Name</label>
                    <input type="text" name="name" required />
                    <button class="btn primary" type="submit">Add</button>
                </form>
            </div>
        </div>
    `;

    app.innerHTML = html;

    if (!departmentsCache || departmentsCache.length === 0) {
        app.innerHTML += `<p class="error">No Departments Found!</p>`;
    }

    document.getElementById("update-dept-form").onsubmit = function(e) {
        e.preventDefault();
        updateDepartment();
    };

    document.getElementById("add-dept-form").onsubmit = function(e) {
        e.preventDefault();
        addDepartment();
    };
}

async function searchDepartment() {
    const dept = document.getElementById("search-department").value.trim();
    if (!dept) return showDeptToast("Enter a department to search.");

    try {
        const departments = await fetchDepartments(`${API_BASE}/search?name=${encodeURIComponent(dept)}&page=0&size=10`);
        if (departments === null) return null;
        renderDepartments(departments);
    } catch (err) {
        showDeptToast("Error: " + err.message);
    }
}

async function addDepartment() {
    try {
        const form = document.getElementById("add-dept-form");
        const payload = { departmentName: form.name.value };

        const response = await fetch(`${API_BASE}/add`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload),
            credentials: "same-origin"
        });

        const data = await response.json();
        if (!response.ok) throw new Error(data.error || data);

        closeAddDeptModal();
        currentDeptPage = 0;
        loadDepartmentsPage();
    } catch (err) {
        showDeptToast("Error adding department: " + err.message);
    }
}

async function updateDepartment() {
    try {
        const form = document.getElementById("update-dept-form");
        const payload = { departmentName: form.name.value };

        const response = await fetch(`${API_BASE}/update?departmentId=${currentEditDepartmentId}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload),
            credentials: "same-origin"
        });

        const data = await response.json();

        if (!response.ok) {
            showDeptToast("Update Error: " + data.error);
            return;
        }

        closeEditDeptModal();
        currentDeptPage = 0;
        loadDepartmentsPage();
    } catch (err) {
        showDeptToast("Error updating department: " + err.message);
    }
}

async function removeDepartment(departmentId) {
    const response = await fetch(`${API_BASE}/delete?departmentId=${departmentId}`, {
        method: "DELETE",
        credentials: "same-origin"
    });

    const data = await response.json();

    if (!response.ok) {
        showDeptToast("Error: " + data.error);
        return;
    }

    showDeptToast("Deleted: " + data.body.departmentName);
    currentDeptPage = 0;
    await loadDepartmentsPage();
}

function openEditDeptModal(departmentId) {
    const modal = document.getElementById("edit-dept-modal");
    modal.style.display = "flex";

    currentEditDepartmentId = departmentId;

    const dept = departmentsCache.find(d => d.departmentId === departmentId);
    if (!dept) return;

    const form = document.getElementById("update-dept-form");
    form.elements["name"].value = dept.name;
}

function closeEditDeptModal() {
    document.getElementById("edit-dept-modal").style.display = "none";
}

function openAddDeptModal() {
    document.getElementById("add-dept-modal").style.display = "flex";
}

function closeAddDeptModal() {
    document.getElementById("add-dept-modal").style.display = "none";
}
