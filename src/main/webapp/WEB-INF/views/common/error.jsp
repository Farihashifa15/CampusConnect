<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Error — CampusConnect"/>
</jsp:include>
<jsp:include page="/WEB-INF/views/common/navbar.jsp"/>

<div class="container py-5 my-5 text-center">
    <div class="card card-cc p-5 mx-auto shadow-sm" style="max-width: 600px;">
        <i class="bi bi-exclamation-octagon text-danger display-1 mb-3"></i>
        <h2 class="fw-bold mb-3">Something Went Wrong</h2>
        <p class="text-muted mb-4">
            ${not empty errorMessage ? errorMessage : 'The requested page could not be accessed or encountered an unexpected situation.'}
        </p>
        <div>
            <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary rounded-pill px-4 me-2">Go to Dashboard</a>
            <a href="${pageContext.request.contextPath}/" class="btn btn-outline-secondary rounded-pill px-4">Home</a>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>