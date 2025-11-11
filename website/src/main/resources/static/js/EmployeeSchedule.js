// Employee Schedule JavaScript

let currentDay = null;
let isEditMode = false;

function openShiftModal(cell) {
    const modal = document.getElementById('shiftModal');
    const backdrop = document.getElementById('shiftModalBackdrop');
    const modalTitle = document.getElementById('modalTitle');
    const deleteBtn = document.getElementById('deleteBtn');
    const form = document.getElementById('shiftForm');
    const dayDisplay = document.getElementById('dayDisplay');
    const selectedDayInput = document.getElementById('selectedDay');

    // Reset form
    form.reset();

    // Check if opening from a cell click (edit mode) or from button (add mode)
    if (cell && typeof cell === 'object') {
        isEditMode = true;
        currentDay = cell.getAttribute('data-day');
        const shiftData = cell.getAttribute('data-shift');

        // Format day name for display
        const dayName = formatDayName(currentDay);
        modalTitle.textContent = `Edit Shift - ${dayName}`;
        dayDisplay.value = dayName;
        selectedDayInput.value = currentDay;

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
        // Add mode - show day selection (for now, default to Monday)
        isEditMode = false;
        currentDay = 'MONDAY';

        modalTitle.textContent = 'Add Shift';
        dayDisplay.value = 'Select a day from the schedule';
        selectedDayInput.value = '';
        deleteBtn.style.display = 'none';
    }

    modal.classList.add('active');
    backdrop.classList.add('active');
    document.body.style.overflow = 'hidden';
}

function closeShiftModal() {
    const modal = document.getElementById('shiftModal');
    const backdrop = document.getElementById('shiftModalBackdrop');

    modal.classList.remove('active');
    backdrop.classList.remove('active');
    document.body.style.overflow = 'auto';

    // Reset form and state
    document.getElementById('shiftForm').reset();
    currentDay = null;
    isEditMode = false;
}

function saveShift(event) {
    event.preventDefault();

    const employeeId = document.getElementById('employeeId').value;
    const day = document.getElementById('selectedDay').value || currentDay;
    const startTime = document.getElementById('startTimeInput').value;
    const endTime = document.getElementById('endTimeInput').value;

    if (!day || !startTime || !endTime) {
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

    console.log('Saving shift:', { employeeId, ...shiftData });

    // Make API call to save the schedule (current week)
    fetch(`/api/schedules/shift?employeeId=${employeeId}&weekType=current`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(shiftData)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to save shift');
            }
            return response.json();
        })
        .then(data => {
            console.log('Success:', data);
            updateShiftCell(day, startTime, endTime);
            closeShiftModal();
            showNotification('Shift saved successfully!', 'success');
        })
        .catch((error) => {
            console.error('Error:', error);
            alert('Failed to save shift. Please try again.');
        });
}

function deleteShift() {
    if (!confirm('Are you sure you want to delete this shift?')) {
        return;
    }

    const employeeId = document.getElementById('employeeId').value;
    const day = document.getElementById('selectedDay').value || currentDay;

    console.log('Deleting shift for:', employeeId, day);

    // Make API call to delete the schedule
    fetch(`/api/schedules/shift?employeeId=${employeeId}&day=${day}&weekType=current`, {
        method: 'DELETE',
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Delete failed');
            }
            updateShiftCell(day, null, null);
            closeShiftModal();
            showNotification('Shift deleted successfully!', 'success');
        })
        .catch((error) => {
            console.error('Error:', error);
            alert('Failed to delete shift. Please try again.');
        });
}

function updateShiftCell(day, startTime, endTime) {
    // Find the cell in the table and update it
    const cell = document.querySelector(`td[data-day="${day}"]`);

    if (cell) {
        const timeSpan = cell.querySelector('.shift-time');
        if (timeSpan) {
            if (startTime && endTime) {
                timeSpan.textContent = `${startTime} - ${endTime}`;
                // Update data attribute for future edits
                const shiftData = { day, startTime, endTime };
                cell.setAttribute('data-shift', JSON.stringify(shiftData));
            } else {
                timeSpan.textContent = 'Off';
                cell.setAttribute('data-shift', 'null');
            }
        }
    }
}

function formatDayName(day) {
    const days = {
        'MONDAY': 'Monday',
        'TUESDAY': 'Tuesday',
        'WEDNESDAY': 'Wednesday',
        'THURSDAY': 'Thursday',
        'FRIDAY': 'Friday',
        'SATURDAY': 'Saturday',
        'SUNDAY': 'Sunday'
    };
    return days[day] || day;
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
        closeShiftModal();
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