<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>         
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.4.1/dist/css/bootstrap.min.css" integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh" crossorigin="anonymous">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/error.css">
<title>General Error Page</title>
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
	    <h1>An error occurred</h1>
	    <p>${errorMessage}</p>
	    
	    <!-- Button to return to contactHome.jsp -->
	    <a href="<c:url value='/contactHome.jsp' />" class="btn btn-primary">Return to Contact Home</a>
	</div>
</body>
</html>