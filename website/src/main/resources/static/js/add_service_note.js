function openAddNoteModal() {
    document.getElementById('addNoteModal').style.display = 'block';
}

function closeAddNoteModal() {
    document.getElementById('addNoteModal').style.display = 'none';
}

function submitNewNote(event) {
    event.preventDefault();

    const form = event.target;
    const note = {
        noteId: form.serviceNoteId.value,
        ticketId: form.serviceTicketId.value,
        technicianId: form.technicianId.value,
        message: form.noteMessage.value
    }

    fetch(`/api/service-ticket/notes`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(note)
    })
        .then(response => {
            if (response.ok) {
                closeAddNoteModal()
                location.reload();
            }
            else {
                alert('Failed to add note.');
            }
        })
        .catch(() => alert('An error occurred while adding the note.'));

}