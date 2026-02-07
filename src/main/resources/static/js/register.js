function loadRegisterPage() {
    const app = document.getElementById("app");

    app.innerHTML = `
        <section class="auth-card">
            <h2>Register Employee</h2>
            <form id="register-form">
                <label>Employee ID</label>
                <input type="number" name="employeeId" required />
                <label>Name</label>
                <input type="text" name="name" required />
                <label>Date of Birth</label>
                <input type="date" name="dateOfBirth" required />
                <label>Password</label>
                <input type="password" name="password" required />
                <label>Salary</label>
                <input type="number" name="salary" required />
                <label>Department</label>
                <input type="text" name="departmentName" required />
                <button class="btn primary" type="submit">Register</button>
            </form>
        </section>
    `;

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
                    alert("Employee registered!");
                    history.pushState({}, "", "/");
                    route();
                } else {
                    const text = await response.text();
                    alert("Error: " + text);
                }
            } catch (err) {
                alert("Error: " + err.message);
            }
        });
}
