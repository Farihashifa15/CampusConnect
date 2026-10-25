<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Administrator Control Center — CampusConnect"/>
</jsp:include>
<jsp:include page="/WEB-INF/views/common/navbar.jsp"/>

<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4 pb-2 border-bottom">
        <div>
            <div class="d-flex align-items-center gap-2">
                <h2 class="fw-bold mb-0">Administrator Control Center</h2>
                <span class="badge bg-danger">ADMIN ONLY</span>
            </div>
            <div class="text-muted small">Platform metrics, user role governance, content moderation, and audit logs</div>
        </div>
        <div class="d-flex gap-2">
            <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-outline-primary btn-sm rounded-pill px-3">
                <i class="bi bi-people me-1"></i>Manage Users
            </a>
            <a href="${pageContext.request.contextPath}/admin/reports" class="btn btn-outline-warning text-dark btn-sm rounded-pill px-3">
                <i class="bi bi-flag me-1"></i>Reports (${not empty pendingReports ? pendingReports.size() : 0})
            </a>
            <a href="${pageContext.request.contextPath}/admin/audit" class="btn btn-outline-secondary btn-sm rounded-pill px-3">
                <i class="bi bi-journal-text me-1"></i>Audit Logs
            </a>
        </div>
    </div>

    <jsp:include page="/WEB-INF/views/common/alerts.jsp"/>

    <!-- Key Platform Metrics -->
    <div class="row g-3 mb-4">
        <div class="col-6 col-md-3">
            <div class="stat-box shadow-sm">
                <div class="number text-primary">${stats.totalUsers}</div>
                <div class="label">Total Users</div>
                <div class="small text-muted mt-1">${stats.studentCount} Students &bull; ${stats.seniorCount} Seniors &bull; ${stats.alumniCount} Alumni</div>
            </div>
        </div>
        <div class="col-6 col-md-3">
            <div class="stat-box shadow-sm">
                <div class="number text-success">${stats.activeMentors}</div>
                <div class="label">Mentors Offering Skills</div>
                <div class="small text-muted mt-1">${stats.completedSessions} Completed Sessions</div>
            </div>
        </div>
        <div class="col-6 col-md-3">
            <div class="stat-box shadow-sm">
                <div class="number text-info">${stats.totalResources}</div>
                <div class="label">Published Resources</div>
                <div class="small text-muted mt-1">${stats.totalQuestions} Doubt Questions</div>
            </div>
        </div>
        <div class="col-6 col-md-3">
            <div class="stat-box shadow-sm">
                <div class="number ${stats.pendingReports > 0 ? 'text-danger' : 'text-secondary'}">${stats.pendingReports}</div>
                <div class="label">Pending Reports</div>
                <div class="small text-muted mt-1">${stats.openProjects} Open Projects</div>
            </div>
        </div>
    </div>

    <div class="row g-4">
        <!-- Pending Reports Column -->
        <div class="col-lg-6">
            <div class="card card-cc p-4 h-100">
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <h5 class="fw-bold mb-0"><i class="bi bi-flag-fill text-danger me-2"></i>Content Needing Moderation</h5>
                    <a href="${pageContext.request.contextPath}/admin/reports" class="small fw-bold text-decoration-none">View All</a>
                </div>
                <c:choose>
                    <c:when test="${not empty pendingReports}">
                        <div class="list-group list-group-flush">
                            <c:forEach var="r" items="${pendingReports}">
                                <div class="list-group-item px-0 py-2">
                                    <div class="d-flex justify-content-between">
                                        <span class="badge bg-secondary-subtle text-secondary border small">${r.contentType} #${r.contentId}</span>
                                        <span class="small text-muted">${r.createdAt}</span>
                                    </div>
                                    <div class="small text-dark mt-1"><strong>Reason:</strong> ${r.reason}</div>
                                    <div class="small text-muted">Reported by: ${r.reporterName}</div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="text-center py-4 text-muted small">
                            <i class="bi bi-shield-check text-success fs-1 d-block mb-1"></i>
                            No pending moderation reports.
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- Recent System Activity Audit Logs -->
        <div class="col-lg-6">
            <div class="card card-cc p-4 h-100">
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <h5 class="fw-bold mb-0"><i class="bi bi-journal-text text-primary me-2"></i>Recent Administrative Audit Stream</h5>
                    <a href="${pageContext.request.contextPath}/admin/audit" class="small fw-bold text-decoration-none">Full Log</a>
                </div>
                <c:choose>
                    <c:when test="${not empty recentLogs}">
                        <div class="list-group list-group-flush small">
                            <c:forEach var="l" items="${recentLogs}">
                                <div class="list-group-item px-0 py-2">
                                    <div class="d-flex justify-content-between">
                                        <span class="fw-bold text-primary">${l.action}</span>
                                        <span class="text-muted">${l.createdAt}</span>
                                    </div>
                                    <div class="text-muted">${l.details}</div>
                                    <div class="text-secondary" style="font-size: 0.75rem;">By ${l.adminName} &bull; IP: ${l.ipAddress}</div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="text-center py-4 text-muted small">
                            No administrative events logged yet.
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>