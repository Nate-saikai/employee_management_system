async function loadEmployeeDetailsPage(id) {
    const app = document.getElementById("app");
    app.innerHTML = `<h2>Loading employee...</h2>`;

    try {
        const response = await fetch(`/user/employee?id=${id}`, {
            credentials: "same-origin"
        });

        if (!response.ok) {
            throw new Error("Employee not found");
        }

        const emp = await response.json();
        renderEmployeeDetails(emp);

    } catch (err) {
        app.innerHTML = `<p class="error">Error: ${err.message}</p>`;
    }
}

function renderEmployeeDetails(emp) {
    const dob = new Date(emp.dateOfBirth);
    const age = new Date().getFullYear() - dob.getFullYear();

    const app = document.getElementById("app");

    app.innerHTML = `
        <section class="employee-details">
            <h2>${emp.name} (${emp.employeeId})</h2>
            <ul>
                <li><strong>Department:</strong> <span id="emp-department">${emp.department ? emp.department.departmentName : "N/A"}</span></li>
                <li><strong>Date of Birth:</strong> <span id="emp-dob">${dob.toISOString().split('T')[0]}</span></li>
                <li><strong>Age:</strong> ${age}</li>
                <li><strong>Salary:</strong> $<span id="emp-salary">${emp.salaryAmount}</span></li>
            </ul>

            <button class="btn primary" onclick="showEditForm(${emp.personId})">Edit</button>
            <button class="btn secondary" onclick="deleteEmployee(${emp.personId})">Delete</button>
            <button class="btn secondary" onclick="history.back()">Back</button>

            <div id="edit-form-container" style="margin-top:2rem;"></div>
        </section>
    `;
}

// Show edit form
function showEditForm(id) {
    const container = document.getElementById("edit-form-container");

    const dob = document.getElementById("emp-dob").textContent;
    const salary = document.getElementById("emp-salary").textContent;
    const department = document.getElementById("emp-department").textContent;

    container.innerHTML = `
        <h3>Edit Employee</h3>
        <form id="edit-employee-form">
            <label>Name:</label>
            <input type="text" name="name" value="${document.querySelector("h2").textContent.split(" (")[0]}" required />
            
            <label>Date of Birth:</label>
            <input type="date" name="dateOfBirth" value="${dob}" required />
            
            <label>Salary:</label>
            <input type="number" name="salary" value="${salary}" required />

            <label>Department:</label>
            <input type="text" name="departmentName" value="${department}" required />

            <button class="btn primary" type="submit">Save Changes</button>
        </form>
    `;

    document.getElementById("edit-employee-form")
        .addEventListener("submit", async function(event) {
            event.preventDefault();

            const form = event.target;
            const payload = {
                name: form.name.value.trim(),
                dateOfBirth: form.dateOfBirth.value,
                salary: parseFloat(form.salary.value),
                departmentName: form.departmentName.value.trim()
            };

            try {
                const response = await fetch(`/user/employee/update/${id}`, {
                    method: "PUT",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(payload),
                    credentials: "same-origin"
                });

                const text = await response.text();

                if (response.ok) {
                    alert("Employee updated successfully");
                    loadEmployeeDetailsPage(id);
                } else {
                    alert("Error: " + text);
                }
            } catch (err) {
                alert("Error: " + err.message);
            }
        });
}

// Delete employee
async function deleteEmployee(id) {
    if (!confirm("Are you sure you want to delete this employee?")) return;

    try {
        const response = await fetch(`/user/employee/delete/${id}`, {
            method: "DELETE",
            credentials: "same-origin"
        });

        if (response.ok) {
            alert("Employee deleted successfully");
            history.pushState({}, "", "/employees");
            route();
        } else {
            const text = await response.text();
            alert("Error: " + text);
        }

    } catch (err) {
        alert("Error: " + err.message);
    }
}
