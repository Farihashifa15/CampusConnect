<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="University Registration — CampusConnect"/>
</jsp:include>
<jsp:include page="/WEB-INF/views/common/navbar.jsp"/>

<div class="container py-5 my-auto">
    <div class="row justify-content-center">
        <div class="col-md-8 col-lg-6">
            <div class="card card-cc p-4 p-md-5 shadow-sm">
                <div class="text-center mb-4">
                    <div class="fs-1 text-primary mb-2"><i class="bi bi-person-badge-fill"></i></div>
                    <h3 class="fw-bold">Create University Account</h3>
                    <p class="text-muted small">Join your campus peer mentorship & skill collaboration platform</p>
                </div>

                <jsp:include page="/WEB-INF/views/common/alerts.jsp"/>

                <form action="${pageContext.request.contextPath}/auth/register" method="post">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                    <div class="row g-3 mb-3">
                        <div class="col-md-12">
                            <label for="fullName" class="form-label fw-semibold">Full Legal Name</label>
                            <input type="text" class="form-control" id="fullName" name="fullName" value="${fullName}" placeholder="e.g. Alex Rivera" required>
                        </div>
                    </div>

                    <div class="row g-3 mb-3">
                        <div class="col-md-6">
                            <label for="registerNumber" class="form-label fw-semibold">Register Number / Student ID</label>
                            <input type="text" class="form-control" id="registerNumber" name="registerNumber" value="${registerNumber}" placeholder="e.g. REG-2024-042" required>
                        </div>
                        <div class="col-md-6">
                            <label for="email" class="form-label fw-semibold">University Email</label>
                            <input type="email" class="form-control" id="email" name="email" value="${email}" placeholder="username${allowedDomain}" required>
                            <div class="form-text small">Must end with <span class="fw-bold text-dark">${allowedDomain}</span></div>
                        </div>
                    </div>

                    <div class="row g-3 mb-3">
                        <div class="col-md-6">
                            <label for="department" class="form-label fw-semibold">Academic Department</label>
                            <input type="text" class="form-control" id="department" name="department" value="${department}" placeholder="e.g. Computer Science" required>
                        </div>
                        <div class="col-md-3">
                            <label for="academicYear" class="form-label fw-semibold">Year of Study</label>
                            <select class="form-select" id="academicYear" name="academicYear">
                                <option value="1" ${academicYear == 1 ? 'selected' : ''}>Year 1</option>
                                <option value="2" ${academicYear == 2 ? 'selected' : ''}>Year 2</option>
                                <option value="3" ${academicYear == 3 ? 'selected' : ''}>Year 3</option>
                                <option value="4" ${academicYear == 4 ? 'selected' : ''}>Year 4</option>
                                <option value="5" ${academicYear == 5 ? 'selected' : ''}>Grad / Year 5</option>
                            </select>
                        </div>
                        <div class="col-md-3">
                            <label for="role" class="form-label fw-semibold">Member Role</label>
                            <select class="form-select" id="role" name="role">
                                <option value="STUDENT" ${role == 'STUDENT' ? 'selected' : ''}>Student</option>
                                <option value="SENIOR" ${role == 'SENIOR' ? 'selected' : ''}>Senior (Mentor)</option>
                                <option value="ALUMNI" ${role == 'ALUMNI' ? 'selected' : ''}>Alumni</option>
                            </select>
                        </div>
                    </div>

                    <div class="mb-4">
                        <label for="password" class="form-label fw-semibold">Password</label>
                        <input type="password" class="form-control" id="password" name="password" placeholder="At least 6 characters" minlength="6" required>
                    </div>

                    <div class="alert alert-info py-2 small mb-4">
                        <i class="bi bi-gift-fill text-warning me-1"></i> New registrations receive <strong>50 Skill Credits</strong> upon account creation.
                    </div>

                    <button type="submit" class="btn btn-primary w-100 py-2 rounded-pill fw-bold mb-3 shadow-sm">
                        Complete Registration
                    </button>

                    <div class="text-center small text-muted">
                        Already have an account?
                        <a href="${pageContext.request.contextPath}/auth/login" class="fw-bold text-decoration-none">Sign in here</a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>