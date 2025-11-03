function saveServiceTicket(event) {
    event.preventDefault();

    const noteElements = document.querySelectorAll('#notesList li');

    const notes = Array.from(noteElements).map(li => ({
        noteId: li.dataset.noteId || null,
        technicianId: li.dataset.technicianId || null,
        message: li.querySelector('.note-message').textContent.trim(),
    }));


    const ticket = {
        ticketId: document.getElementById("ticketId").value,
        status: document.getElementById("status").value,
        title: document.getElementById("title").value,
        description: document.getElementById("description").value,
        customerId: document.getElementById("customerId").value,
        customerDeviceId: document.getElementById("customerDeviceId").value,
        employeeId: document.getElementById("assignedEmployeeId").value,
        serviceId: document.getElementById("serviceId").value,
        createdAt: document.getElementById("createdDate").value,
        updatedAt: document.getElementById("updatedDate").value,
        notes: notes,
    }

    fetch(`/api/service-ticket`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(ticket)
    })
        .then(response => {
            if (response.ok) {
                location.reload();
                alert('Ticket saved successfully.');
            }
            else {
                alert('Failed to save ticket.');
            }
        })
        .catch(() => alert('An error occurred while saving the ticket.'));

}

function closeServiceTicket(event) {
    event.preventDefault();
    const ticketId = document.getElementById("ticketId").value;

    fetch(`/api/service-ticket/close?ticketId=${ticketId}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' }
    })
        .then(response => {
            if (response.ok) {
                alert('Ticket closed successfully.');
            }
            else {
                alert('Failed to close ticket.');
            }
        })
        .catch(() => alert('An error occurred while closing the ticket.'));
}

function claimServiceTicket(event) {
    event.preventDefault();
    const employeeId = document.getElementById("employeeId").value;
    const ticketId = document.getElementById("ticketId").value;

    fetch(`/api/service-ticket/assign?employeeId=${employeeId}&ticketId=${ticketId}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' }
    })
        .then(response => {
            if (response.ok) {
                alert('Ticket assigned successfully.');
            }
            else {
                alert('Failed to assign ticket.');
            }
        })
        .catch(() => alert('An error occurred while assigning the ticket.'));
}