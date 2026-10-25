<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<nav class="navbar navbar-expand-lg navbar-dark bg-cc-nav sticky-top">
    <div class="container">
        <a class="navbar-brand navbar-brand-cc d-flex align-items-center gap-2" href="${pageContext.request.contextPath}/">
            <i class="bi bi-mortarboard-fill"></i>
            <span>CampusConnect</span>
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#ccNavbar" aria-controls="ccNavbar" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="ccNavbar">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <c:if test="${not empty sessionScope.currentUser}">
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/dashboard">
                            <i class="bi bi-speedometer2 me-1"></i>Dashboard
                        </a>
                    </li>
                </c:if>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/mentors">
                        <i class="bi bi-people-fill me-1"></i>Find Mentors
                    </a>
                </li>
                <c:if test="${not empty sessionScope.currentUser}">
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/sessions">
                            <i class="bi bi-calendar-event me-1"></i>My Sessions
                        </a>
                    </li>
                </c:if>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/doubts">
                        <i class="bi bi-question-circle me-1"></i>Doubt Board
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/resources">
                        <i class="bi bi-folder-symlink me-1"></i>Resources
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/projects">
                        <i class="bi bi-kanban me-1"></i>Projects
                    </a>
                </li>
                <c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle text-warning fw-bold" href="#" id="adminDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                            <i class="bi bi-shield-lock-fill me-1"></i>Admin Panel
                        </a>
                        <ul class="dropdown-menu dropdown-menu-dark" aria-labelledby="adminDropdown">
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/admin"><i class="bi bi-bar-chart-line me-2"></i>Dashboard</a></li>
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/admin/users"><i class="bi bi-people me-2"></i>User Management</a></li>
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/admin/reports"><i class="bi bi-flag me-2"></i>Moderation Reports</a></li>
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/admin/audit"><i class="bi bi-journal-text me-2"></i>Audit Logs</a></li>
                        </ul>
                    </li>
                </c:if>
            </ul>

            <ul class="navbar-nav ms-auto align-items-center gap-2">
                <c:choose>
                    <c:when test="${not empty sessionScope.currentUser}">
                        <!-- Skill Credits Badge -->
                        <li class="nav-item">
                            <a href="${pageContext.request.contextPath}/credits" class="credit-badge text-decoration-none d-flex align-items-center gap-1" data-bs-toggle="tooltip" title="Skill Credits Balance">
                                <i class="bi bi-coin"></i>
                                <span>${sessionScope.currentUser.creditBalance} Credits</span>
                            </a>
                        </li>

                        <!-- Notifications Link -->
                        <li class="nav-item">
                            <a class="nav-link position-relative" href="${pageContext.request.contextPath}/notifications" title="Notifications">
                                <i class="bi bi-bell-fill fs-5"></i>
                            </a>
                        </li>

                        <!-- User Profile Dropdown -->
                        <li class="nav-item dropdown">
                            <a class="nav-link dropdown-toggle d-flex align-items-center gap-2" href="#" id="userDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                                <span class="badge badge-role-${sessionScope.currentUser.role}">${sessionScope.currentUser.role}</span>
                                <span>${sessionScope.currentUser.fullName}</span>
                            </a>
                            <ul class="dropdown-menu dropdown-menu-end shadow" aria-labelledby="userDropdown">
                                <li>
                                    <div class="px-3 py-1">
                                        <div class="fw-bold">${sessionScope.currentUser.fullName}</div>
                                        <div class="small text-muted">${sessionScope.currentUser.email}</div>
                                    </div>
                                </li>
                                <li><hr class="dropdown-divider"></li>
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/profile"><i class="bi bi-person me-2"></i>My Profile</a></li>
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/profile/edit"><i class="bi bi-pencil-square me-2"></i>Edit Profile & Skills</a></li>
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/credits"><i class="bi bi-wallet2 me-2"></i>Credit Ledger</a></li>
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/auth/password"><i class="bi bi-key me-2"></i>Change Password</a></li>
                                <li><hr class="dropdown-divider"></li>
                                <li>
                                    <form action="${pageContext.request.contextPath}/auth/logout" method="post" class="m-0">
                                        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                        <button type="submit" class="dropdown-item text-danger"><i class="bi bi-box-arrow-right me-2"></i>Sign Out</button>
                                    </form>
                                </li>
                            </ul>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/auth/login">Sign In</a>
                        </li>
                        <li class="nav-item">
                            <a class="btn btn-primary btn-sm rounded-pill px-3 fw-bold" href="${pageContext.request.contextPath}/auth/register">Get Started</a>
                        </li>
                    </c:otherwise>
                </c:choose>
            </ul>
        </div>
    </div>
</nav>