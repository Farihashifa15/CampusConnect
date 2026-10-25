<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Session #${sessionDetail.id} Details — CampusConnect"/>
</jsp:include>
<jsp:include page="/WEB-INF/views/common/navbar.jsp"/>

<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4 pb-2 border-bottom">
        <div>
            <div class="d-flex align-items-center gap-2">
                <h2 class="fw-bold mb-0">Mentorship Session #${sessionDetail.id}</h2>
                <span class="badge badge-status-${sessionDetail.status} fs-6">${sessionDetail.status}</span>
            </div>
            <div class="text-muted small">Topic: <strong>${sessionDetail.skillName}</strong></div>
        </div>
        <a href="${pageContext.request.contextPath}/sessions" class="btn btn-outline-secondary rounded-pill px-3">
            <i class="bi bi-arrow-left me-1"></i>Back to Sessions
        </a>
    </div>

    <jsp:include page="/WEB-INF/views/common/alerts.jsp"/>

    <div class="row g-4 justify-content-center">
        <div class="col-lg-8">
            <div class="card card-cc p-4 mb-4">
                <h5 class="fw-bold mb-3"><i class="bi bi-info-circle text-primary me-2"></i>Session Details</h5>

                <div class="row g-3 mb-4">
                    <div class="col-md-6">
                        <div class="p-3 bg-light rounded border">
                            <div class="small text-muted mb-1">Mentor:</div>
                            <div class="fw-bold fs-5">${sessionDetail.mentorName}</div>
                            <a href="${pageContext.request.contextPath}/profile?id=${sessionDetail.mentorId}" class="small text-decoration-none">View Profile</a>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="p-3 bg-light rounded border">
                            <div class="small text-muted mb-1">Student Learner:</div>
                            <div class="fw-bold fs-5">${sessionDetail.learnerName}</div>
                            <a href="${pageContext.request.contextPath}/profile?id=${sessionDetail.learnerId}" class="small text-decoration-none">View Profile</a>
                        </div>
                    </div>
                </div>

                <div class="row g-3 mb-4">
                    <div class="col-md-4">
                        <div class="small text-muted mb-1"><i class="bi bi-calendar me-1"></i>Session Date</div>
                        <div class="fw-semibold text-dark">${sessionDetail.sessionDate}</div>
                    </div>
                    <div class="col-md-4">
                        <div class="small text-muted mb-1"><i class="bi bi-clock me-1"></i>Interval</div>
                        <div class="fw-semibold text-dark">${sessionDetail.startTime} - ${sessionDetail.endTime}</div>
                    </div>
                    <div class="col-md-4">
                        <div class="small text-muted mb-1"><i class="bi bi-coin me-1"></i>Fee / Reward</div>
                        <div><span class="credit-badge">${sessionDetail.creditCost} Skill Credits</span></div>
                    </div>
                </div>

                <div class="mb-4">
                    <div class="small text-muted mb-1">Session Notes & Agenda:</div>
                    <div class="p-3 bg-light rounded border text-dark">
                        ${not empty sessionDetail.notes ? sessionDetail.notes : 'No specific agenda provided.'}
                    </div>
                </div>

                <!-- Session Actions -->
                <div class="d-flex flex-wrap gap-2 pt-3 border-top">
                    <!-- Mentor Confirm / Reject -->
                    <c:if test="${isMentor && sessionDetail.status == 'PENDING'}">
                        <form action="${pageContext.request.contextPath}/sessions/action" method="post" class="d-inline">
                            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                            <input type="hidden" name="sessionId" value="${sessionDetail.id}">
                            <input type="hidden" name="action" value="confirm">
                            <button type="submit" class="btn btn-success rounded-pill px-4 fw-bold">
                                <i class="bi bi-check2-circle me-1"></i>Confirm Session
                            </button>
                        </form>
                        <form action="${pageContext.request.contextPath}/sessions/action" method="post" class="d-inline">
                            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                            <input type="hidden" name="sessionId" value="${sessionDetail.id}">
                            <input type="hidden" name="action" value="reject">
                            <button type="submit" class="btn btn-outline-danger rounded-pill px-4" data-confirm="Decline this session request? Escrowed credits will be refunded.">
                                <i class="bi bi-x-circle me-1"></i>Decline
                            </button>
                        </form>
                    </c:if>

                    <!-- Mentor Complete -->
                    <c:if test="${isMentor && sessionDetail.status == 'CONFIRMED'}">
                        <form action="${pageContext.request.contextPath}/sessions/action" method="post" class="d-inline">
                            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                            <input type="hidden" name="sessionId" value="${sessionDetail.id}">
                            <input type="hidden" name="action" value="complete">
                            <button type="submit" class="btn btn-primary rounded-pill px-4 fw-bold shadow-sm" data-confirm="Mark this session as COMPLETED? You will receive ${sessionDetail.creditCost} Skill Credits.">
                                <i class="bi bi-trophy me-1"></i>Mark Session as Completed
                            </button>
                        </form>
                    </c:if>

                    <!-- Cancel Session (Learner or Mentor) -->
                    <c:if test="${(isLearner || isMentor) && (sessionDetail.status == 'PENDING' || sessionDetail.status == 'CONFIRMED')}">
                        <form action="${pageContext.request.contextPath}/sessions/action" method="post" class="d-inline ms-auto">
                            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                            <input type="hidden" name="sessionId" value="${sessionDetail.id}">
                            <input type="hidden" name="action" value="cancel">
                            <button type="submit" class="btn btn-outline-secondary rounded-pill px-3" data-confirm="Cancel this scheduled session? Escrowed credits will be refunded.">
                                <i class="bi bi-slash-circle me-1"></i>Cancel Session
                            </button>
                        </form>
                    </c:if>

                    <!-- Learner Leave Feedback -->
                    <c:if test="${isLearner && sessionDetail.status == 'COMPLETED'}">
                        <a href="${pageContext.request.contextPath}/feedback/add?sessionId=${sessionDetail.id}" class="btn btn-warning text-dark rounded-pill px-4 fw-bold shadow-sm">
                            <i class="bi bi-star-fill me-1"></i>Rate & Endorse Mentor
                        </a>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>