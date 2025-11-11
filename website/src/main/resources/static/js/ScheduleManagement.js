// Schedule Management JavaScript

let currentEmployeeId = null;
let currentDay = null;
let currentWeekType = 'current';
let isEditMode = false;

function openScheduleModal(weekType, cell) {
    const modal = document.getElementById('scheduleModal');
    const backdrop = document.getElementById('scheduleModalBackdrop');
    const modalTitle = document.getElementById('modalTitle');
    const deleteBtn = document.getElementById('deleteBtn');
    const form = document.getElementById('scheduleForm');
    const weekTypeInput = document.getElementById('weekType');

    // Set the week type
    currentWeekType = weekType;
    weekTypeInput.value = weekType;

    // Reset form
    form.reset();
    weekTypeInput.value = weekType; // Reset clears it, so set again

    const weekLabel = weekType === 'current' ? 'This Week' : 'Next Week';

    // Check if opening from a cell click (edit mode) or from button (add mode)
    if (cell && typeof cell === 'object') {
        isEditMode = true;
        currentEmployeeId = cell.getAttribute('data-employee-id');
        currentDay = cell.getAttribute('data-day');
        const employeeName = cell.getAttribute('data-employee-name');
        const shiftData = cell.getAttribute('data-shift');

        modalTitle.textContent = `Edit Schedule - ${employeeName} (${weekLabel})`;

        // Pre-fill the form
        document.getElementById('employeeSelect').value = currentEmployeeId;
        document.getElementById('employeeSelect').disabled = true;
        document.getElementById('daySelect').value = currentDay;
        document.getElementById('daySelect').disabled = true;

        // Parse and set the shift times if available
        if (shiftData && shiftData !== 'null') {
            try {
                const shift = JSON.parse(shiftData);
                document.getElementById('startTimeInput').value = shift.startTime;
                document.getElementById('endTimeInput').value = shift.endTime;
                deleteBtn.style.display = 'block';
            } catch (e) {
                console.error('Error parsing shift data:', e);
                deleteBtn.style.display = 'none';
            }
        } else {
            deleteBtn.style.display = 'none';
        }
    } else {
        isEditMode = false;
        currentEmployeeId = null;
        currentDay = null;

        modalTitle.textContent = `Add Schedule Entry (${weekLabel})`;
        document.getElementById('employeeSelect').disabled = false;
        document.getElementById('daySelect').disabled = false;
        deleteBtn.style.display = 'none';
    }

    modal.classList.add('active');
    backdrop.classList.add('active');
    document.body.style.overflow = 'hidden';
}

function closeScheduleModal() {
    const modal = document.getElementById('scheduleModal');
    const backdrop = document.getElementById('scheduleModalBackdrop');

    modal.classList.remove('active');
    backdrop.classList.remove('active');
    document.body.style.overflow = 'auto';

    // Reset form and state
    document.getElementById('scheduleForm').reset();
    document.getElementById('employeeSelect').disabled = false;
    document.getElementById('daySelect').disabled = false;
    currentEmployeeId = null;
    currentDay = null;
    currentWeekType = 'current';
    isEditMode = false;
}

function saveSchedule(event) {
    event.preventDefault();

    const employeeId = document.getElementById('employeeSelect').value;
    const day = document.getElementById('daySelect').value;
    const startTime = document.getElementById('startTimeInput').value;
    const endTime = document.getElementById('endTimeInput').value;
    const weekType = document.getElementById('weekType').value;

    if (!employeeId || !day || !startTime || !endTime) {
        alert('Please fill in all fields');
        return;
    }

    // Validate that end time is after start time
    if (startTime >= endTime) {
        alert('End time must be after start time');
        return;
    }

    // Prepare the data matching the Shift model
    const shiftData = {
        day: day,
        startTime: startTime,
        endTime: endTime
    };

    console.log('Saving schedule:', { employeeId, weekType, ...shiftData });

    // Make API call to save the schedule
    fetch(`/api/schedules/shift?employeeId=${employeeId}&weekType=${weekType}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(shiftData)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to save schedule');
            }
            return response.json();
        })
        .then(data => {
            console.log('Success:', data);
            updateTableCell(employeeId, day, startTime, endTime, weekType);
            closeScheduleModal();
            showNotification('Schedule saved successfully!', 'success');
        })
        .catch((error) => {
            console.error('Error:', error);
            alert('Failed to save schedule. Please try again.');
        });
}

function deleteSchedule() {
    if (!confirm('Are you sure you want to delete this schedule entry?')) {
        return;
    }

    const employeeId = document.getElementById('employeeSelect').value;
    const day = document.getElementById('daySelect').value;
    const weekType = document.getElementById('weekType').value;

    console.log('Deleting schedule for:', employeeId, day, weekType);

    // Make API call to delete the schedule
    fetch(`/api/schedules/shift?employeeId=${employeeId}&day=${day}&weekType=${weekType}`, {
        method: 'DELETE',
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Delete failed');
            }
            updateTableCell(employeeId, day, null, null, weekType);
            closeScheduleModal();
            showNotification('Schedule deleted successfully!', 'success');
        })
        .catch((error) => {
            console.error('Error:', error);
            alert('Failed to delete schedule. Please try again.');
        });
}

function updateTableCell(employeeId, day, startTime, endTime, weekType) {
    // Find the cell in the table and update it
    const cell = document.querySelector(
        `td[data-employee-id="${employeeId}"][data-day="${day}"][data-week="${weekType}"]`
    );

    if (cell) {
        const timeSpan = cell.querySelector('.schedule-time');
        if (timeSpan) {
            if (startTime && endTime) {
                timeSpan.textContent = `${startTime}-${endTime}`;
                // Update data attribute for future edits
                const shiftData = { day, startTime, endTime };
                cell.setAttribute('data-shift', JSON.stringify(shiftData));
            } else {
                timeSpan.textContent = '--';
                cell.setAttribute('data-shift', 'null');
            }
        }
    }
}

function emailNextWeekSchedule() {
    // Show loading message
    const confirmSend = confirm('This will send the next week\'s schedule to all employees via email. Continue?');

    if (!confirmSend) {
        return;
    }

    console.log('Sending next week schedule emails...');

    // TODO: Make API call to trigger email sending
    // fetch('/api/schedules/email-next-week', {
    //     method: 'POST',
    // })
    // .then(response => {
    //     if (!response.ok) {
    //         throw new Error('Failed to send emails');
    //     }
    //     showNotification('Schedule emails sent successfully!', 'success');
    // })
    // .catch((error) => {
    //     console.error('Error:', error);
    //     alert('Failed to send schedule emails. Please try again.');
    // });

    // Placeholder notification
    showNotification('Email functionality coming soon! Schedule would be sent to all employees.', 'success');
}

function showNotification(message, type) {
    // Simple notification system
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.textContent = message;
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 1rem 1.5rem;
        background: ${type === 'success' ? '#4caf50' : '#f44336'};
        color: white;
        border-radius: 8px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.2);
        z-index: 10000;
        animation: slideInRight 0.3s ease-out;
    `;

    document.body.appendChild(notification);

    setTimeout(() => {
        notification.style.animation = 'slideOutRight 0.3s ease-out';
        setTimeout(() => {
            document.body.removeChild(notification);
        }, 300);
    }, 3000);
}

// Close modal on Escape key
document.addEventListener('keydown', function(event) {
    if (event.key === 'Escape') {
        closeScheduleModal();
    }
});

// Add CSS animations for notifications
const style = document.createElement('style');
style.textContent = `
    @keyframes slideInRight {
        from {
            transform: translateX(400px);
            opacity: 0;
        }
        to {
            transform: translateX(0);
            opacity: 1;
        }
    }
    
    @keyframes slideOutRight {
        from {
            transform: translateX(0);
            opacity: 1;
        }
        to {
            transform: translateX(400px);
            opacity: 0;
        }
    }
`;
document.head.appendChild(style);