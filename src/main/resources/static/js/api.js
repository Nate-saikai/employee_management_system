// api.js

const API_BASE = "/user";

async function apiFetch(url, options = {}) {
    const response = await fetch(API_BASE + url, {
        credentials: "same-origin", // important for JSESSION
        headers: {
            "Content-Type": "application/json",
            ...(options.headers || {})
        },
        ...options
    });

    if (!response.ok) {
        const error = await response.text();
        throw new Error(error || "Request failed");
    }

    return response.json();
}
