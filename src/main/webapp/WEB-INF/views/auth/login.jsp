<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Sign In — CampusConnect"/>
</jsp:include>
<jsp:include page="/WEB-INF/views/common/navbar.jsp"/>

<div class="container py-5 my-auto">
    <div class="row justify-content-center">
        <div class="col-md-6 col-lg-5">
            <div class="card card-cc p-4 p-md-5 shadow-sm">
                <div class="text-center mb-4">
                    <div class="fs-1 text-primary mb-2"><i class="bi bi-mortarboard-fill"></i></div>
                    <h3 class="fw-bold">Sign In to CampusConnect</h3>
                    <p class="text-muted small">Access your university mentorship dashboard and sessions</p>
                </div>

                <jsp:include page="/WEB-INF/views/common/alerts.jsp"/>

                <form action="${pageContext.request.contextPath}/auth/login" method="post">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                    <div class="mb-3">
                        <label for="email" class="form-label fw-semibold">University Email</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="bi bi-envelope"></i></span>
                            <input type="email" class="form-control" id="email" name="email" value="${email}" placeholder="username${allowedDomain}" required autofocus>
                        </div>
                        <div class="form-text small">Must end with <span class="fw-bold text-dark">${allowedDomain}</span></div>
                    </div>

                    <div class="mb-4">
                        <label for="password" class="form-label fw-semibold">Password</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="bi bi-lock"></i></span>
                            <input type="password" class="form-control" id="password" name="password" placeholder="••••••••" required>
                        </div>
                    </div>

                    <button type="submit" class="btn btn-primary w-100 py-2 rounded-pill fw-bold mb-3 shadow-sm">
                        Sign In
                    </button>

                    <div class="text-center small text-muted">
                        Don't have a university account yet?
                        <a href="${pageContext.request.contextPath}/auth/register" class="fw-bold text-decoration-none">Register here</a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>