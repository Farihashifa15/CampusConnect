<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="System Audit Logs — CampusConnect Admin"/>
</jsp:include>
<jsp:include page="/WEB-INF/views/common/navbar.jsp"/>

<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4 pb-2 border-bottom">
        <div>
            <h2 class="fw-bold mb-1">Administrative Audit Logs</h2>
            <div class="text-muted small">Immutable security audit trail of privileged administrative actions</div>
        </div>
        <a href="${pageContext.request.contextPath}/admin" class="btn btn-outline-secondary rounded-pill px-3">
            <i class="bi bi-arrow-left me-1"></i>Admin Dashboard
        </a>
    </div>

    <jsp:include page="/WEB-INF/views/common/alerts.jsp"/>

    <div class="card card-cc p-4 mb-4">
        <div class="d-flex justify-content-between align-items-center mb-3">
            <div class="input-group" style="max-width: 350px;">
                <span class="input-group-text"><i class="bi bi-search"></i></span>
                <input type="text" class="form-control form-control-sm" id="tableSearchInput" placeholder="Quick filter log entries...">
            </div>
            <div class="small text-muted">Showing last <strong>${auditLogs.size()}</strong> actions</div>
        </div>

        <c:choose>
            <c:when test="${not empty auditLogs}">
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0 filterable-table">
                        <thead class="table-light">
                            <tr>
                                <th>Timestamp</th>
                                <th>Action</th>
                                <th>Target</th>
                                <th>Admin User</th>
                                <th>Details / Justification</th>
                                <th>IP Address</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="l" items="${auditLogs}">
                                <tr>
                                    <td class="small text-muted">${l.createdAt}</td>
                                    <td>
                                        <span class="badge bg-primary-subtle text-primary border border-primary-subtle font-monospace">
                                            ${l.action}
                                        </span>
                                    </td>
                                    <td>
                                        <span class="badge bg-light text-secondary border">
                                            ${l.targetType} <c:if test="${not empty l.targetId}">#${l.targetId}</c:if>
                                        </span>
                                    </td>
                                    <td class="fw-semibold text-dark">${l.adminName}</td>
                                    <td class="small text-dark">${l.details}</td>
                                    <td class="font-monospace small text-muted">${l.ipAddress}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:when>
            <c:otherwise>
                <div class="text-center py-5 text-muted">
                    <i class="bi bi-journal-x display-4 text-secondary mb-2 d-block"></i>
                    <h5 class="fw-bold">No Audit Logs</h5>
                    <p class="small text-muted">No administrative events have been recorded yet.</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>