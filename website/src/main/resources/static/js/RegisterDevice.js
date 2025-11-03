function openNewDeviceModal() {
    document.getElementById("newDeviceModal").style.display = "block";
}

function closeNewDeviceModal() {
    document.getElementById("newDeviceModal").style.display = "none";
}

function submitNewDeviceRegistration(event) {
    event.preventDefault();

    const form = event.target;
    const data = {
        customerEmail: form.customerEmail.value,
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
                location.reload();
            } else {
                alert("Failed to register device. Please try again.");
            }
        })
        .catch(() => alert("An error occurred while submitting the request."));
}
