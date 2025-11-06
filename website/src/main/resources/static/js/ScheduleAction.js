function punchIn() {
    const employeeId = document.getElementById("employeeId").value;

    fetch("/api/schedule/punch-in?employeeId=" + employeeId, {
        method: "POST",
        headers: {"Content-Type": "application/json"},
    })
        .then(response => {
            if (response.ok) {
                alert("Thanks for logging in! ☕");
            }
            else if (response.status === 409) { // Conflict status code
                alert("You are already logged in. Please log out first.");
            }
            else {
                alert("Failed to punch in. Please try again.");
            }
        })
        .catch(() => alert("An error occurred while submitting the request."));
}

function punchOut() {
    const employeeId = document.getElementById("employeeId").value;

    fetch("/api/schedule/punch-out?employeeId=" + employeeId, {
        method: "POST",
        headers: {"Content-Type": "application/json"},
    })
        .then(response => {
            if (response.ok) {
                alert("Enjoy the remainder of your day, thanks for working! ☕");
            }
            else if (response.status === 409) { // Conflict status code
                alert("You're not logged in. Please log in first.");
            }
            else {
                alert("Failed to punch out. Did you log in first? Please try again.");
            }
        })
        .catch(() => alert("An error occurred while submitting the request."));
}