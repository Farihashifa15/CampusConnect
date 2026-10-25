<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Content Moderation — CampusConnect Admin"/>
</jsp:include>
<jsp:include page="/WEB-INF/views/common/navbar.jsp"/>

<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4 pb-2 border-bottom">
        <div>
            <h2 class="fw-bold mb-1">Content Moderation & Reports</h2>
            <div class="text-muted small">Investigate reported doubt questions, answers, projects, and resources</div>
        </div>
        <a href="${pageContext.request.contextPath}/admin" class="btn btn-outline-secondary rounded-pill px-3">
            <i class="bi bi-arrow-left me-1"></i>Admin Dashboard
        </a>
    </div>

    <jsp:include page="/WEB-INF/views/common/alerts.jsp"/>

    <!-- Filter Pills -->
    <div class="d-flex gap-2 mb-4">
        <a href="${pageContext.request.contextPath}/admin/reports?status=PENDING" class="btn ${empty selectedStatus || selectedStatus == 'PENDING' ? 'btn-primary' : 'btn-outline-secondary'} btn-sm rounded-pill px-3">
            Pending Reports
        </a>
        <a href="${pageContext.request.contextPath}/admin/reports?status=RESOLVED" class="btn ${selectedStatus == 'RESOLVED' ? 'btn-primary' : 'btn-outline-secondary'} btn-sm rounded-pill px-3">
            Resolved
        </a>
        <a href="${pageContext.request.contextPath}/admin/reports?status=DISMISSED" class="btn ${selectedStatus == 'DISMISSED' ? 'btn-primary' : 'btn-outline-secondary'} btn-sm rounded-pill px-3">
            Dismissed
        </a>
        <a href="${pageContext.request.contextPath}/admin/reports?status=ALL" class="btn ${selectedStatus == 'ALL' ? 'btn-primary' : 'btn-outline-secondary'} btn-sm rounded-pill px-3">
            All Reports
        </a>
    </div>

    <div class="card card-cc p-4">
        <c:choose>
            <c:when test="${not empty reports}">
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0">
                        <thead class="table-light">
                            <tr>
                                <th>Report ID</th>
                                <th>Content Target</th>
                                <th>Reason Reported</th>
                                <th>Reporter</th>
                                <th>Status</th>
                                <th>Timestamp</th>
                                <th class="text-end">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="r" items="${reports}">
                                <tr>
                                    <td>#${r.id}</td>
                                    <td>
                                        <span class="badge bg-secondary-subtle text-secondary border">${r.contentType} #${r.contentId}</span>
                                    </td>
                                    <td class="text-dark fw-semibold">${r.reason}</td>
                                    <td>${r.reporterName}</td>
                                    <td>
                                        <span class="badge ${r.status == 'PENDING' ? 'bg-warning text-dark' : (r.status == 'RESOLVED' ? 'bg-success' : 'bg-secondary')}">
                                            ${r.status}
                                        </span>
                                    </td>
                                    <td class="small text-muted">${r.createdAt}</td>
                                    <td class="text-end">
                                        <c:if test="${r.status == 'PENDING'}">
                                            <form action="${pageContext.request.contextPath}/admin/report/resolve" method="post" class="d-inline m-0">
                                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                                <input type="hidden" name="reportId" value="${r.id}">
                                                <input type="hidden" name="action" value="resolve">
                                                <button type="submit" class="btn btn-success btn-sm rounded-pill px-3 me-1">Resolve</button>
                                            </form>
                                            <form action="${pageContext.request.contextPath}/admin/report/resolve" method="post" class="d-inline m-0">
                                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                                <input type="hidden" name="reportId" value="${r.id}">
                                                <input type="hidden" name="action" value="dismiss">
                                                <button type="submit" class="btn btn-outline-secondary btn-sm rounded-pill px-3">Dismiss</button>
                                            </form>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:when>
            <c:otherwise>
                <div class="text-center py-5 text-muted">
                    <i class="bi bi-shield-check display-4 text-success mb-2 d-block"></i>
                    <h5 class="fw-bold">No Reports Found</h5>
                    <p class="small text-muted">There are no reports matching this filter.</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>