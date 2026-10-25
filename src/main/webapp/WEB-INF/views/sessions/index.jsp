<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="My Mentorship Sessions — CampusConnect"/>
</jsp:include>
<jsp:include page="/WEB-INF/views/common/navbar.jsp"/>

<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center flex-wrap gap-2 mb-4 pb-2 border-bottom">
        <div>
            <h2 class="fw-bold mb-1">Mentorship Sessions</h2>
            <div class="text-muted small">Manage your upcoming, pending, and completed mentorship meetings</div>
        </div>
        <div class="d-flex gap-2">
            <a href="${pageContext.request.contextPath}/mentors" class="btn btn-primary rounded-pill px-3 shadow-sm">
                <i class="bi bi-calendar-plus me-1"></i>Book New Session
            </a>
        </div>
    </div>

    <jsp:include page="/WEB-INF/views/common/alerts.jsp"/>

    <!-- Tabs: As Learner vs As Mentor -->
    <ul class="nav nav-pills mb-4 gap-2" id="sessionTabs" role="tablist">
        <li class="nav-item" role="presentation">
            <button class="nav-link active rounded-pill px-4" id="learner-tab" data-bs-toggle="tab" data-bs-target="#learner" type="button" role="tab">
                <i class="bi bi-book me-1"></i>As Learner (${not empty asLearnerSessions ? asLearnerSessions.size() : 0})
            </button>
        </li>
        <li class="nav-item" role="presentation">
            <button class="nav-link rounded-pill px-4" id="mentor-tab" data-bs-toggle="tab" data-bs-target="#mentor" type="button" role="tab">
                <i class="bi bi-person-video3 me-1"></i>As Mentor (${not empty asMentorSessions ? asMentorSessions.size() : 0})
            </button>
        </li>
    </ul>

    <div class="tab-content" id="sessionTabsContent">
        <!-- Learner Tab -->
        <div class="tab-pane fade show active" id="learner" role="tabpanel">
            <div class="card card-cc p-4">
                <c:choose>
                    <c:when test="${not empty asLearnerSessions}">
                        <div class="table-responsive">
                            <table class="table table-hover align-middle mb-0">
                                <thead class="table-light">
                                    <tr>
                                        <th>Skill / Topic</th>
                                        <th>Mentor</th>
                                        <th>Scheduled Date & Time</th>
                                        <th>Escrowed Cost</th>
                                        <th>Status</th>
                                        <th class="text-end">Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="s" items="${asLearnerSessions}">
                                        <tr>
                                            <td class="fw-bold text-dark">${s.skillName}</td>
                                            <td>
                                                <a href="${pageContext.request.contextPath}/profile?id=${s.mentorId}" class="text-decoration-none fw-semibold">
                                                    ${s.mentorName}
                                                </a>
                                            </td>
                                            <td>
                                                <div><i class="bi bi-calendar me-1 text-secondary"></i>${s.sessionDate}</div>
                                                <div class="small text-muted"><i class="bi bi-clock me-1 text-secondary"></i>${s.startTime} - ${s.endTime}</div>
                                            </td>
                                            <td><span class="credit-badge small">${s.creditCost} Credits</span></td>
                                            <td><span class="badge badge-status-${s.status}">${s.status}</span></td>
                                            <td class="text-end">
                                                <a href="${pageContext.request.contextPath}/sessions/view?id=${s.id}" class="btn btn-outline-primary btn-sm rounded-pill px-3">
                                                    View Details
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="text-center py-5 text-muted">
                            <i class="bi bi-calendar-x display-4 text-secondary mb-2 d-block"></i>
                            <h5 class="fw-bold">No Sessions Booked</h5>
                            <p class="small text-muted">You haven't requested any mentorship sessions yet.</p>
                            <a href="${pageContext.request.contextPath}/mentors" class="btn btn-primary btn-sm rounded-pill px-3">Find a Mentor</a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- Mentor Tab -->
        <div class="tab-pane fade" id="mentor" role="tabpanel">
            <div class="card card-cc p-4">
                <c:choose>
                    <c:when test="${not empty asMentorSessions}">
                        <div class="table-responsive">
                            <table class="table table-hover align-middle mb-0">
                                <thead class="table-light">
                                    <tr>
                                        <th>Skill / Topic</th>
                                        <th>Learner</th>
                                        <th>Scheduled Date & Time</th>
                                        <th>Reward</th>
                                        <th>Status</th>
                                        <th class="text-end">Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="s" items="${asMentorSessions}">
                                        <tr>
                                            <td class="fw-bold text-dark">${s.skillName}</td>
                                            <td>
                                                <a href="${pageContext.request.contextPath}/profile?id=${s.learnerId}" class="text-decoration-none fw-semibold">
                                                    ${s.learnerName}
                                                </a>
                                            </td>
                                            <td>
                                                <div><i class="bi bi-calendar me-1 text-secondary"></i>${s.sessionDate}</div>
                                                <div class="small text-muted"><i class="bi bi-clock me-1 text-secondary"></i>${s.startTime} - ${s.endTime}</div>
                                            </td>
                                            <td><span class="credit-badge small">+${s.creditCost} Credits</span></td>
                                            <td><span class="badge badge-status-${s.status}">${s.status}</span></td>
                                            <td class="text-end">
                                                <a href="${pageContext.request.contextPath}/sessions/view?id=${s.id}" class="btn btn-outline-primary btn-sm rounded-pill px-3">
                                                    Manage
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="text-center py-5 text-muted">
                            <i class="bi bi-person-video3 display-4 text-secondary mb-2 d-block"></i>
                            <h5 class="fw-bold">No Mentoring Sessions Scheduled</h5>
                            <p class="small text-muted">You have no upcoming mentorship appointments from students.</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>