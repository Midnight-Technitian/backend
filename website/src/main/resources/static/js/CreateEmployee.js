function openNewEmployeeModal() {
    document.getElementById("newEmployeeModal").style.display = "block";
}

function closeNewEmployeeModal() {
    document.getElementById("newEmployeeModal").style.display = "none";
}

function submitNewEmployee(event) {
    event.preventDefault();

    const form = event.target;
    const data = {
        employeeId: "",
        email: form.email.value,
        firstName: form.firstName.value,
        lastName: form.lastName.value,
        contactNumber: form.contactNumber.value,
        positionTitle: form.positionTitle.value,
        profileImageUrl: "",
        createdBy: form.createdBy.value,
        updatedBy: form.createdBy.value,
        status: "ACTIVE",
        employeeStartDate: "",
        employeeEndDate: "",
        lastLoginAt: "",
        createdAt: "",
        updatedAt: ""
    };

    fetch("/api/employee", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(data)
    })
        .then(response => {
            if (response.ok) {
                alert("Employee created successfully!");
                closeNewEmployeeModal();
                form.reset();
                location.reload();
            } else {
                alert("Failed to create employee. Please try again.");
            }
        })
        .catch(() => alert("An error occurred while submitting the employee."));
}
