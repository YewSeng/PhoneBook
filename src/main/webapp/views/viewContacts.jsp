<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>    
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.4.1/dist/css/bootstrap.min.css" integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh" crossorigin="anonymous">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/viewContacts.css">
<title>View All Contacts Page</title>
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
        <h1>View Contacts Page</h1>
        <!-- Success or Error Message -->
        <c:if test="${not empty successMessage}">
            <div class="alert alert-success"><b>${successMessage}</b></div>
        </c:if>
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger"><b>${errorMessage}</b></div>
        </c:if>
        <c:if test="${not empty createdContact}">
            <div class="alert alert-success"><b>Created Contact Details:</b> 
                <br><b>Contact ID:</b> ${createdContact.contactId}, <b>Name:</b> ${createdContact.name}, <b>Phone Number:</b> ${createdContact.phoneNumber}, <b>Address:</b> ${createdContact.address}
            </div>
        </c:if>
        <c:if test="${not empty updatedContact}">
            <div class="alert alert-success"><b>Updated Contact Details:</b> 
                <br><b>Contact ID:</b> ${updatedContact.contactId}, <b>Name:</b> ${updatedContact.name}, <b>Phone Number:</b> ${updatedContact.phoneNumber}, <b>Address:</b> ${updatedContact.address}
            </div>
        </c:if>
        <c:if test="${not empty deletedContact}">
            <div class="alert alert-danger"><b>Deleted Contact Details:</b> 
                <br><b>ContactID:</b> ${deletedContact.contactId}, <b>Name:</b> ${deletedContact.name}, <b>Phone Number:</b> ${deletedContact.phoneNumber}, <b>Address:</b> ${deletedContact.address}
            </div>
        </c:if>
        <!-- Add form for filter -->
        <form action="${pageContext.request.contextPath}/api/v1/contacts/filterContacts" method="GET" class="mb-3">
            <div class="row">
            	<div class="col-md-3">
                    <label for="searchUUID" class="form-label"><b>UUID:</b></label>
                    <input type="text" name="contactId" id="searchUUID" class="form-control" placeholder="Enter UUID">
                </div>
                <div class="col-md-3">
                    <label for="searchName" class="form-label"><b>Name:</b></label>
                    <input type="text" name="name" id="searchName" class="form-control" placeholder="Enter name">
                </div>
                <div class="col-md-3">
                    <label for="searchPhoneNumber" class="form-label"><b>Phone Number:</b></label>
                    <input type="text" name="phoneNumber" id="searchPhoneNumber" class="form-control" placeholder="Enter phone number">
                </div>
                <div class="col-md-3">
                    <label for="searchAddress" class="form-label"><b>Address:</b></label>
                    <input type="text" name="address" id="searchAddress" class="form-control" placeholder="Enter address">
                </div>
            </div>
            <button type="submit" id="filter-button" class="btn btn-primary btn-center">${empty param.searchTerm ? 'Search' : 'Click To Return to Default Search'}</button>
        </form>
        <table class="table table-hover table-dark">
            <thead>
                <tr>
                    <th scope="col">Contact ID</th>
                    <th scope="col">Name</th>
                    <th scope="col">Phone Number</th>
                    <th scope="col">Address</th>
                    <th scope="col">Update Contact</th>
                    <th scope="col">Delete Contact</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${contacts}" var="contact" varStatus="loop">
                    <c:set var="index" value="${loop.index}" />
                    <c:set var="highlightClass" value="" />

                    <!-- Check if the current contact is the created contact -->
                    <c:if test="${not empty createdContact and contact.contactId eq createdContact.contactId}">
                        <c:set var="highlightClass" value="highlight-created" />
                    </c:if>

                    <!-- Check if the current contact is the updated contact -->
                    <c:if test="${not empty updatedContact and contact.contactId eq updatedContact.contactId}">
                        <c:set var="highlightClass" value="highlight-updated" />
                    </c:if>

                    <!-- Check if the current contact is the deleted contact -->
                    <c:if test="${not empty deletedContact and contact.contactId eq deletedContact.contactId}">
                        <c:set var="highlightClass" value="highlight-deleted" />
                    </c:if>

                    <!-- Table row with highlight class -->
                    <tr class="${highlightClass}">
                        <td><c:out value="${contact.contactId}" /></td>
                        <td><c:out value="${contact.name}" /></td>
                        <td><c:out value="${contact.phoneNumber}" /></td>
                        <td><c:out value="${contact.address}" /></td>
                        <td>
                            <!-- Update Contact button -->
                            <c:url var="updateContactUrl" value="/api/v1/contacts/editContact/${contact.contactId}" />
                            <form action="${updateContactUrl}">
                                <button type="submit" id="update-button" class="btn btn-warning btn-center">Update</button>
                            </form>
                        </td>
                        <td>
                            <!-- Delete Contact button -->
                            <c:url var="deleteContactUrl" value="/api/v1/contacts/deleteContact/${contact.contactId}" />
							<form action="${pageContext.request.contextPath}/api/v1/contacts/deleteContact/${contact.contactId}" method="POST">
							    <input type="hidden" name="_method" value="DELETE" />
							    <button type="submit" id="delete-button" class="btn btn-danger btn-center">Delete</button>
							</form>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
		<!-- Pagination controls -->
		<div>
		    <c:if test="${hasPreviousPage}">
		        <form action="<c:choose>
		                          <c:when test="${not empty param.searchType}">
		                              ${pageContext.request.contextPath}/api/v1/contacts/filterContacts
		                          </c:when>
		                          <c:otherwise>
		                              ${pageContext.request.contextPath}/api/v1/contacts/getAllContacts
		                          </c:otherwise>
		                      </c:choose>"
		              method="GET" class="pagination-form">
		            <input type="hidden" name="searchType" value="${param.searchType}" />
		            <input type="hidden" name="searchTerm" value="${param.searchTerm}" />
		            <input type="hidden" name="page" value="${currentPage - 1}" /> <!-- Adjust page number for backend -->
		            <input type="hidden" name="size" value="${pageSize}" />
		            <button type="submit" id="previous-button" class="btn btn-success">Previous</button>
		        </form>
		    </c:if>
		    <c:choose>
		        <c:when test="${totalPages > 0}">
		            <span class="mx-3">
		                <b>Page ${currentPage} of ${totalPages}</b>
		            </span>
		        </c:when>
		        <c:otherwise>
		            <span class="mx-3">
		                <b>Page 1 of 1</b>
		            </span>
		        </c:otherwise>
		    </c:choose>
		    <c:if test="${hasNextPage}">
		        <form action="<c:choose>
		                          <c:when test="${not empty param.searchType}">
		                              ${pageContext.request.contextPath}/api/v1/contacts/filterContacts
		                          </c:when>
		                          <c:otherwise>
		                              ${pageContext.request.contextPath}/api/v1/contacts/getAllContacts
		                          </c:otherwise>
		                      </c:choose>"
		              method="GET" class="pagination-form">
		            <input type="hidden" name="searchType" value="${param.searchType}" />
		            <input type="hidden" name="searchTerm" value="${param.searchTerm}" />
		            <input type="hidden" name="page" value="${currentPage + 1}" /> <!-- Adjust page number for backend -->
		            <input type="hidden" name="size" value="${pageSize}" />
		            <button type="submit" id="next-button" class="btn btn-success">Next</button>
		        </form>
		    </c:if>
		</div>
	</div>
</body>
</html>