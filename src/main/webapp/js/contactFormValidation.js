function validateForm() {
    var name = document.getElementById("name").value;
    var phoneNumber = document.getElementById("phoneNumber").value;
    var address = document.getElementById("address").value;

    // Regular expressions for validation
    var nameRegex = /^[a-zA-Z ]{4,}$/;
    var phoneNumberRegex = /^[89]\d{7}$/;
    var minAddressLength = 10;

    // Error messages from backend
    var EMPTY_NAME_ERROR = "Name cannot be empty";
    var INVALID_NAME_ERROR = "Name must contain only letters and spaces and be at least 4 characters long";
    var EMPTY_PHONE_NUMBER_ERROR = "Phone Number cannot be empty";
    var INVALID_PHONE_NUMBER_ERROR = "Phone Number must start with 8 or 9 and have 7 consecutive digits";
    var EMPTY_ADDRESS_ERROR = "Address cannot be empty";
    var INVALID_ADDRESS_ERROR = "Address must be at least 10 characters long";

    // Variables to store error messages
    var errorMessage = "";
    var nameError = "";
    var phoneNumberError = "";
    var addressError = "";

    if (name.trim() === "") {
        nameError = EMPTY_NAME_ERROR;
        errorMessage += nameError + "\n";
    } else if (!nameRegex.test(name)) {
        nameError = INVALID_NAME_ERROR;
        errorMessage += nameError + "\n";
    }

    if (phoneNumber.trim() === "") {
        phoneNumberError = EMPTY_PHONE_NUMBER_ERROR;
        errorMessage += phoneNumberError + "\n";
    } else if (!phoneNumberRegex.test(phoneNumber)) {
        phoneNumberError = INVALID_PHONE_NUMBER_ERROR;
        errorMessage += phoneNumberError + "\n";
    }

    if (address.trim() === "") {
        addressError = EMPTY_ADDRESS_ERROR;
        errorMessage += addressError + "\n";
    } else if (address.trim().length < minAddressLength) {
        addressError = INVALID_ADDRESS_ERROR;
        errorMessage += addressError + "\n";
    }

    // Display error messages in divs
    document.getElementById("name-error").innerText = nameError;
    document.getElementById("phoneNumber-error").innerText = phoneNumberError;
    document.getElementById("address-error").innerText = addressError;

    // Display error message in main error div if there are errors, or hide it if no errors
    var errorDiv = document.getElementById("error-message");
    if (errorMessage.trim() !== "") {
        errorDiv.innerText = errorMessage;
        errorDiv.style.display = "block"; // Show the error message div
    } else {
        errorDiv.style.display = "none"; // Hide the error message div
    }

    return errorMessage === ""; // Return true if no error messages, otherwise return false
}