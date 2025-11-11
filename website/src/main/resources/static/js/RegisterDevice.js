function openNewDeviceModal() {
    // Reset readonly state when opening normally
    const emailInput = document.getElementById("deviceCustomerEmail");
    if (emailInput) {
        emailInput.readOnly = false;
    }
    document.getElementById("newDeviceModal").style.display = "block";
}

function closeNewDeviceModal() {
    document.getElementById("newDeviceModal").style.display = "none";

    // Reset readonly state
    const emailInput = document.getElementById("deviceCustomerEmail");
    if (emailInput) {
        emailInput.readOnly = false;
    }
}

function submitNewDeviceRegistration(event) {
    event.preventDefault();

    let custEmail = document.getElementById("customerEmail").value;
    const emailInput = document.getElementById("deviceCustomerEmail");
    if (emailInput) {
        emailInput.readOnly = false;
        custEmail = emailInput.value;
    }
    const form = event.target;
    const data = {
        customerEmail: custEmail,
        deviceName: form.deviceName.value,
        deviceType: form.deviceType.value,
        deviceInfo: form.deviceInfo.value,
    };

    fetch("/api/device", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(data)
    })
        .then(response => {
            if (response.ok) {
                alert("Device registered successfully!");
                closeNewDeviceModal();
                form.reset();
                // Check if we're in ticket creation context
                if (typeof selectedCustomer !== 'undefined' && selectedCustomer) {
                    openNewTicketModal();
                } else {
                    location.reload();
                }
            }
            else {
                alert("Failed to create ticket. Please try again.");
            }
        })
        .catch(() => alert("An error occurred while submitting the request."));
}
