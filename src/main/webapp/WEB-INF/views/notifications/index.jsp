<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Notifications — CampusConnect"/>
</jsp:include>
<jsp:include page="/WEB-INF/views/common/navbar.jsp"/>

<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4 pb-2 border-bottom">
        <div>
            <h2 class="fw-bold mb-1">Notifications Center</h2>
            <div class="text-muted small">Stay updated on your mentorship bookings, doubt replies, and credit earnings</div>
        </div>
        <c:if test="${not empty notifications}">
            <form action="${pageContext.request.contextPath}/notifications/read" method="post" class="m-0">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                <button type="submit" class="btn btn-outline-secondary btn-sm rounded-pill px-3">
                    <i class="bi bi-check-all me-1"></i>Mark All as Read
                </button>
            </form>
        </c:if>
    </div>

    <jsp:include page="/WEB-INF/views/common/alerts.jsp"/>

    <div class="row justify-content-center">
        <div class="col-lg-8">
            <div class="card card-cc p-4">
                <c:choose>
                    <c:when test="${not empty notifications}">
                        <div class="list-group list-group-flush">
                            <c:forEach var="n" items="${notifications}">
                                <div class="list-group-item px-0 py-3 d-flex justify-content-between align-items-start ${!n.read ? 'bg-light rounded p-3 mb-2' : ''}">
                                    <div class="d-flex align-items-start gap-3">
                                        <div class="fs-4 ${!n.read ? 'text-primary' : 'text-secondary'}">
                                            <c:choose>
                                                <c:when test="${n.type == 'BOOKING'}"><i class="bi bi-calendar-event"></i></c:when>
                                                <c:when test="${n.type == 'SESSION'}"><i class="bi bi-person-video3"></i></c:when>
                                                <c:when test="${n.type == 'CREDIT'}"><i class="bi bi-coin text-warning"></i></c:when>
                                                <c:when test="${n.type == 'APPLICATION'}"><i class="bi bi-kanban text-info"></i></c:when>
                                                <c:when test="${n.type == 'ANSWER'}"><i class="bi bi-chat-dots text-success"></i></c:when>
                                                <c:otherwise><i class="bi bi-bell"></i></c:otherwise>
                                            </c:choose>
                                        </div>
                                        <div>
                                            <div class="fw-bold ${!n.read ? 'text-dark' : 'text-muted'}">${n.title}</div>
                                            <div class="small text-secondary mb-1">${n.message}</div>
                                            <div class="small text-muted" style="font-size: 0.75rem;">${n.createdAt}</div>
                                        </div>
                                    </div>
                                    <div class="d-flex gap-2 align-items-center">
                                        <c:if test="${not empty n.link}">
                                            <a href="${pageContext.request.contextPath}${n.link}" class="btn btn-outline-primary btn-sm rounded-pill px-3">
                                                View
                                            </a>
                                        </c:if>
                                        <c:if test="${!n.read}">
                                            <form action="${pageContext.request.contextPath}/notifications/read" method="post" class="d-inline m-0">
                                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                                <input type="hidden" name="id" value="${n.id}">
                                                <button type="submit" class="btn btn-link text-secondary p-0" title="Mark as read">
                                                    <i class="bi bi-check2"></i>
                                                </button>
                                            </form>
                                        </c:if>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="text-center py-5 text-muted">
                            <i class="bi bi-bell-slash display-4 text-secondary mb-2 d-block"></i>
                            <h5 class="fw-bold">No Notifications</h5>
                            <p class="small text-muted">You are all caught up! Important application updates will appear here.</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>