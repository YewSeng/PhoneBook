<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Contact Home Page</title>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.4.1/dist/css/bootstrap.min.css" integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh" crossorigin="anonymous">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/contactHome.css">
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
	    <div class="container-fluid">
	        <h1 class="mt-4">Contact Dashboard</h1>
	        <div class="row">
	            <div class="col-md-6 mb-4"> <!-- Use col-md-6 to make cards appear side by side on medium screens and larger -->
	                <div class="card text-center">
	                    <div class="card-body">
	                        <h5 class="card-title">Create A Contact</h5>
	                        <a href="<c:url value='/api/v1/contacts/createContact' />" class="btn btn-primary">Create a Contact</a>
	                    </div>
	                    <div class="card-footer text-muted">
	                        Last updated eons ago
	                    </div>
	                </div>
	            </div>
	            <div class="col-md-6 mb-4"> <!-- Use col-md-6 to make cards appear side by side on medium screens and larger -->
	                <div class="card text-center">
	                    <div class="card-body">
	                        <h5 class="card-title">View All Contacts</h5>
	                        <a href="<c:url value='/api/v1/contacts/getAllContacts' />" class="btn btn-secondary">View Contacts</a>
	                    </div>
	                    <div class="card-footer text-muted">
	                        Last updated eons ago
	                    </div>
	                </div>
	            </div>
	        </div>
	    </div>
	</div>
</body>
</html>
