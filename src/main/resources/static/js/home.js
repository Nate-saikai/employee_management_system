async function loadHomePage() {
    const app = document.getElementById("app");

    app.innerHTML = `
        <section class="home">
            <h1>Employee Management System</h1>
            <p class="subtitle">
                Manage employees, departments, and records securely.
            </p>

            <div class="home-actions">
                <a href="/login" class="btn primary" onclick="navigate(event)">
                    Login
                </a>
                <a href="/register" class="btn secondary" onclick="navigate(event)">
                    Register Manager
                </a>
            </div>
        </section>
    `;
}
