function openNewTicketModal() {
    document.getElementById("newTicketModal").style.display = "block";
}

function closeNewTicketModal() {
    document.getElementById("newTicketModal").style.display = "none";
}

function submitNewTicket(event) {
    event.preventDefault();

    const form = event.target;
    const data = {
        customerEmail: form.customerEmail.value,
        serviceId:form.serviceId.value,
        deviceId: form.deviceId.value,
        deviceName: form.deviceName.value,
        serviceDescription:form.description.value
    };

    fetch("/api/tickets", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(data)
    })
        .then(response => {
            if (response.ok) {
                alert("Ticket created successfully!");
                closeNewTicketModal();
                form.reset();
                location.reload();
            } else {
                alert("Failed to create ticket. Please try again.");
            }
        })
        .catch(() => alert("An error occurred while submitting the ticket."));
}
