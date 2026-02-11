function loadRegisterPage() {
    const app = document.getElementById("app");

    app.innerHTML = `
        <section class="auth-card">
            <h2>Register Manager</h2>
            <form id="register-form">
                <label>Employee ID</label>
                <input type="text" name="employeeId" id="safeInput" pattern="[A-Za-z0-9-]+" title="Only letters, numbers, and dashes allowed" required />
                <label>Name</label>
                <input type="text" name="name" id="safeInput" pattern="[A-Za-z0-9-]+" title="Only letters and numbers allowed" required />
                <label>Date of Birth</label>
                <input type="date" id="dateField" name="dateOfBirth" required />
                <label>Password</label>
                <input type="password" name="password" required />
                <label>Salary</label>
                <input type="number" step="any" name="salary"/>
                <label>Department</label>
                <input type="text" name="departmentName"/>
                <button class="btn primary" type="submit">Register</button>
            </form>
        </section>
    `;

    // Listener for text input
    const input = document.getElementById("safeInput");
    input.addEventListener("input", () => {
        if (!input.checkValidity()) {
            input.reportValidity(); // shows the native bubble
        }
    });

    // Get today's date in YYYY-MM-DD format
    const today = new Date();
    const yyyy = today.getFullYear() - 21;
    const mm = String(today.getMonth() + 1).padStart(2, '0'); // Months are 0-based
    const dd = String(today.getDate()).padStart(2, '0');

    const maxDate = `${yyyy}-${mm}-${dd}`;

    // Set the max attribute of the date input
    const dateInput = document.getElementById('dateField');
    if (dateInput) {
        dateInput.setAttribute('max', maxDate);
    }

    document.getElementById("register-form")
        .addEventListener("submit", async function(event) {
            event.preventDefault();

            const form = event.target;
            const payload = {
                employeeId: form.employeeId.value,
                name: form.name.value,
                dateOfBirth: form.dateOfBirth.value,
                passwordHash: form.password.value,
                salary: parseFloat(form.salary.value),
                departmentName: form.departmentName.value
            };

            try {
                const response = await fetch("/user/register", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(payload),
                    credentials: "same-origin"
                });

                if (response.ok) {
                    showToast("Manager registered!", "success");
                    history.pushState({}, "", "/");
                    route();
                } else {
                    const data = await response.json();
                    showToast("Error: " + data.error);
                }
            } catch (err) {
                showToast("Error: " + err.message);
            }
        });
}
