<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Change Password — CampusConnect"/>
</jsp:include>
<jsp:include page="/WEB-INF/views/common/navbar.jsp"/>

<div class="container py-5 my-auto">
    <div class="row justify-content-center">
        <div class="col-md-6 col-lg-5">
            <div class="card card-cc p-4 p-md-5 shadow-sm">
                <div class="text-center mb-4">
                    <div class="fs-1 text-primary mb-2"><i class="bi bi-shield-lock-fill"></i></div>
                    <h3 class="fw-bold">Change Password</h3>
                    <p class="text-muted small">Update your account security credentials</p>
                </div>

                <jsp:include page="/WEB-INF/views/common/alerts.jsp"/>

                <form action="${pageContext.request.contextPath}/auth/password" method="post">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                    <div class="mb-3">
                        <label for="oldPassword" class="form-label fw-semibold">Current Password</label>
                        <input type="password" class="form-control" id="oldPassword" name="oldPassword" required>
                    </div>

                    <div class="mb-3">
                        <label for="newPassword" class="form-label fw-semibold">New Password</label>
                        <input type="password" class="form-control" id="newPassword" name="newPassword" minlength="6" required>
                    </div>

                    <div class="mb-4">
                        <label for="confirmPassword" class="form-label fw-semibold">Confirm New Password</label>
                        <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" minlength="6" required>
                    </div>

                    <button type="submit" class="btn btn-primary w-100 py-2 rounded-pill fw-bold mb-3 shadow-sm">
                        Update Password
                    </button>
                    <div class="text-center">
                        <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-outline-secondary btn-sm rounded-pill px-3">Return to Dashboard</a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>