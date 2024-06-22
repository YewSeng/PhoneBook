<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>    
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.4.1/dist/css/bootstrap.min.css" integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh" crossorigin="anonymous">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/createContact.css">
<script src="${pageContext.request.contextPath}/js/contactFormValidation.js"></script>
<title>Create Contact Page</title>
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
        <h1>Create Contact</h1>
        <%-- Display error message if exists --%>
        <c:if test="${not empty errorMessage}">
            <div class="error-message alert alert-danger" id="error-message"><b>${errorMessage}</b></div>
        </c:if>
        <%-- Your form for creating a new contact --%>
        <form action="<c:url value='/api/v1/contacts/registerContact'/>" method="POST" onsubmit="return validateForm()">
            <%-- Input fields for contact details --%>
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
            <button type="submit" id="submit-button" class="btn btn-primary btn-center">Create Contact</button>
        </form>
    </div>
</body>
</html>