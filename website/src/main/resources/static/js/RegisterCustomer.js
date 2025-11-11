function openNewCustomerModal() {
    document.getElementById("newCustomerModel").style.display = "block";
}

function closeNewCustomerModal() {
    document.getElementById("newCustomerModel").style.display = "none";
}

function submitNewCustomerRegistration(event) {
    event.preventDefault();

    const form = event.target;
    const employeeId = document.getElementById("employeeId").value;

    const data = {
        customerEmail: form.customerEmail.value,
        firstName: form.firstName.value,
        lastName: form.lastName.value,
        contactNumber: form.contactNumber.value,
        registeredBy: employeeId,
    };

    fetch("/api/customer", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(data)
    })
        .then(response => {
            if (response.ok) {
                alert("Customer registered successfully!");
                closeNewCustomerModal();
                form.reset();
                location.reload();
            } else {
                alert("Failed to register customer. Please try again.");
            }
        })
        .catch(() => alert("An error occurred while submitting the request."));
}

let selectedCustomer = null;
let customerDevices = [];

function openCustomerSearchModal() {
    document.getElementById("customerSearchModal").style.display = "block";
}

function closeCustomerSearchModal() {
    document.getElementById("customerSearchModal").style.display = "none";
    document.getElementById("searchCustomerForm").reset();
    document.getElementById("customerSearchResult").style.display = "none";
}

function searchCustomer(event) {
    event.preventDefault();

    const email = document.getElementById("searchEmail").value;
    const resultDiv = document.getElementById("customerSearchResult");

    fetch(`/api/customer/search?email=${email}`)
        .then(response => {
            if (response.ok) {
                return response.json();
            } else if (response.status === 404) {
                throw new Error("NOT_FOUND");
            } else {
                throw new Error("ERROR");
            }
        })
        .then(customer => {
            selectedCustomer = customer;
            loadCustomerDevices(customer.email);
            resultDiv.innerHTML = `
                <div class="customer-found">
                    <h4>Customer Found</h4>
                    <p><strong>Name:</strong> ${customer.firstName} ${customer.lastName}</p>
                    <p><strong>Email:</strong> ${customer.email}</p>
                    <p><strong>Contact:</strong> ${customer.contactNumber}</p>
                    <button onclick="proceedToTicketCreation()" class="submit-btn">Create Ticket for This Customer</button>
                </div>
            `;
            resultDiv.style.display = "block";
        })
        .catch(error => {
            if (error.message === "NOT_FOUND") {
                resultDiv.innerHTML = `
                    <div class="customer-not-found">
                        <p>No customer found with email: <strong>${email}</strong></p>
                        <p>Please verify the email or register the customer first.</p>
                    </div>
                `;
            } else {
                resultDiv.innerHTML = `
                    <div class="customer-error">
                        <p>Error searching for customer. Please try again.</p>
                    </div>
                `;
            }
            resultDiv.style.display = "block";
            selectedCustomer = null;
        });
}

function proceedToTicketCreation() {
    if (!selectedCustomer) {
        alert("No customer selected!");
        return;
    }
    fetch(`/api/customer/device?email=${selectedCustomer.email}`)
        .then(response => response.json())
        .then(devices => {
            customerDevices = devices || [];
            closeCustomerSearchModal();

            // If no devices found, prompt to register a device
            if (customerDevices.length === 0) {
                if (confirm(`No devices found for ${selectedCustomer.firstName} ${selectedCustomer.lastName}.\n\nWould you like to register a device before creating the ticket?`)) {
                    openDeviceRegistrationForTicket();
                } else {
                    openNewTicketModal();
                }
            } else {
                openNewTicketModal();
            }
        })
        .catch(error => {
            console.error("Error fetching devices:", error);
            customerDevices = [];
            closeCustomerSearchModal();
            openNewTicketModal();
        });
}

function openDeviceRegistrationForTicket() {
    const emailInput = document.getElementById("deviceCustomerEmail");
    if (emailInput && selectedCustomer) {
        emailInput.value = selectedCustomer.email;
        emailInput.readOnly = true;
    }
    document.getElementById("newDeviceModal").style.display = "block";
}

function openNewTicketModal() {
    if (!selectedCustomer) {
        openCustomerSearchModal();
        return;
    }

    // Pre-fill customer email in the ticket form
    const emailInput = document.getElementById("customerEmail");
    if (emailInput && selectedCustomer) {
        emailInput.value = selectedCustomer.email;
        emailInput.readOnly = true;
    }

    document.getElementById("newTicketModal").style.display = "block";
    loadCustomerDevices(selectedCustomer.email);
}

function closeNewTicketModal() {
    document.getElementById("newTicketModal").style.display = "none";
    // Reset customer selection when closing
    selectedCustomer = null;
    const emailInput = document.getElementById("customerEmail");
    if (emailInput) {
        emailInput.readOnly = false;
    }
}

function submitNewTicket(event) {
    event.preventDefault();

    const form = event.target;

    const data = {
        customerEmail: form.customerEmail.value,
        deviceName: form.deviceName.value,
        deviceId: form.deviceId.value,
        serviceId: form.serviceId.value,
        serviceDescription: form.description.value,
    };

    fetch("/api/service-ticket/tickets", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(data)
    })
        .then(response => {
            if (response.ok) {
                alert("Service ticket created successfully!");
                closeNewTicketModal();
                form.reset();
                location.reload();
            } else {
                alert("Failed to create ticket. Please try again.");
            }
        })
        .catch(() => alert("An error occurred while submitting the request."));
}

function loadCustomerDevices(email) {
    fetch(`/api/customer/device?email=${email}`)
        .then(response => {
            if (response.ok) {
                return response.json();
            }
            throw new Error("Failed to load devices");
        })
        .then(devices => {
            updateDeviceDropdown(devices);
        })
        .catch(error => {
            console.error("Error loading devices:", error);
            updateDeviceDropdown([]);
        });
}

function updateDeviceDropdown(devices) {
    const deviceSelect = document.getElementById("deviceId");
    if (!deviceSelect) return;

    // Clear existing options
    deviceSelect.innerHTML = '';

    if (devices.length === 0) {
        deviceSelect.innerHTML = '<option value="">Unregistered Device</option>';
    } else {
        deviceSelect.innerHTML = '<option value="">New Device</option>';
        devices.forEach(device => {
            const option = document.createElement('option');
            option.value = device.deviceId;
            option.textContent = device.deviceName;
            deviceSelect.appendChild(option);
        });
    }
}