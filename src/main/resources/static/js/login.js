async function loadLoginPage() {
    const app = document.getElementById("app");

    const params = new URLSearchParams(window.location.search);
    const error = params.has("error");

    app.innerHTML = `
        <section class="auth-card">
            <h2>Login</h2>
            ${error ? `<p class="error">Invalid credentials</p>` : ""}
            <form method="post" th:action="@{/login}">
                <label>Employee ID</label>
                <input type="text" name="username" required />
                <label>Password</label>
                <input type="password" name="password" required />
                <button class="btn primary" type="submit">Login</button>
            </form>
        </section>
    `;
}
