<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.4.1/dist/css/bootstrap.min.css" integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh" crossorigin="anonymous">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.4.1/dist/js/bootstrap.min.js" integrity="sha384-wfSDF2E50Y2D1uUdj0O3uMBJnjuUD4Ih7YwaYd1iqfktj0Uod8GCExl3Og8ifwB6" crossorigin="anonymous"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/editContact.css">
    <script src="${pageContext.request.contextPath}/js/contactFormValidation.js"></script>    
<title>Edit Contact Page</title>
</head>
<body>
    <nav class="navbar navbar-dark bg-dark">
        <ul class="navbar-nav mr-auto mt-2 mt-lg-0 d-flex flex-row">
            <li class="nav-item active mx-2"> <!-- Added mx-2 class for margin -->
                <a class="nav-link" href="/">Home<span class="sr-only">(current)</span></a>
            </li>
            <li class="nav-item active mx-2"> <!-- Added mx-2 class for margin -->
                <a class="nav-link" href="<c:url value='/api/v1/contacts/createContact' />">Create Contact</a>
            </li>
            <li class="nav-item active mx-2"> <!-- Added mx-2 class for margin -->
                <a class="nav-link" href="<c:url value='/api/v1/contacts/getAllContacts' />">View All Contacts</a>
            </li>
        </ul>
    </nav>
    <div class="center-container">
        <h1>Edit Contact</h1>
        <h4>Contact Id: <span class="edit-data">${contact.contactId}</span></h4>
        <div class="row">
            <!-- Previous Contact Details -->
            <div class="col-md-5 previous-data-container">
                <div class="contact-details-container">
                    <h2>Previous Contact Details</h2>
                    <table class="table table-bordered">
                        <tbody>
                            <tr>
                                <th scope="row">Contact Id</th>
                                <td>${contact.contactId}</td>
                            </tr>
                            <tr>
                                <th scope="row">Name</th>
                                <td>${contact.name}</td>
                            </tr>
                            <tr>
                                <th scope="row">Phone Number</th>
                                <td>${contact.phoneNumber}</td>
                            </tr>
                            <tr>
                                <th scope="row">Address</th>
                                <td>${contact.address}</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
            <!-- Arrow and Text -->
            <div class="col-md-2 arrow-container">
                <div class="arrow">
                    <div class="arrow-text">Update</div>
                </div>
            </div>
            <!-- Form for Editing Contact Details -->
            <div class="col-md-5 new-data-container">
                <div class="contact-details-container">
                    <h2>Edit Contact Details</h2>
                    <c:if test="${not empty errorMessage}">
                        <div class="error-message alert alert-danger" id="error-message"><b>${errorMessage}</b></div>
                    </c:if>
                    <form action="<c:url value='/api/v1/contacts/updateContact/${contact.contactId}' />" method="POST" onsubmit="return validateForm()">
                        <!-- Hidden input field to specify the HTTP method as PUT -->
                        <input type="hidden" name="_method" value="PUT">
                        <!-- Input fields for contact details -->
			            <div class="form-group">
			                 <label for="name"><b>Name:</b></label>
			                <input type="text" class="form-control" id="name" name="name" value="${name}" required>
					        <c:if test="${not empty nameError}">
					            <div class="fieldsError" id="name-error"><b>${nameError}</b></div>
					        </c:if>
			            </div>
			            <div class="form-group">
			                <label for="phoneNumber"><b>Phone Number:</b></label>
			                <input type="text" class="form-control" id="phoneNumber" name="phoneNumber" value="${phoneNumber}" pattern="\d{8}" required>   
					        <c:if test="${not empty phoneNumberError}">
					            <div class="fieldsError" id="phoneNumber-error"><b>${phoneNumberError}</b></div>
					        </c:if>
			            </div>
						<div class="form-group">
						    <label for="address"><b>Address:</b></label>
						    <textarea class="form-control" id="address" name="address" rows="3" required>${address}</textarea>
						    <c:if test="${not empty addressError}">
						        <div class="fieldsError" id="address-error"><b>${addressError}</b></div>
						    </c:if>
						</div>
                        <button type="submit" id="submit-button" class="btn btn-primary btn-center">Update</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
