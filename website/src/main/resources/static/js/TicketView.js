document.addEventListener("DOMContentLoaded", () => {
    const ticketId = document.getElementById("ticketId").value;
    document.getElementById("saveChangesBtn").addEventListener("click", () => {
        const body = {
            ticketId: ticketId,
            status: document.getElementById("status").value,
            title: document.getElementById("title").value,
            description: document.getElementById("description").value,
            customerId: document.getElementById("customerId").value,
            customerDeviceId: document.getElementById("customerDeviceId").value,
            employeeId: document.getElementById("employeeId").value,
            serviceId: document.getElementById("serviceId").value,
            notes: [],
        };

        fetch("/api/ticket/edit", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(body)
        })
            .then(res => {
                if (!res.ok) throw new Error("Failed to save changes");
                return res.json();
            })
            .then(data => {
                alert("Ticket updated successfully!");
                location.reload();
                console.log("Updated Ticket:", data);
            })
            .catch(err => {
                console.error(err);
                alert("Error saving changes.");
            });
    });
});
