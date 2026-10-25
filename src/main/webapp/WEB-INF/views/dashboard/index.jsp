<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Dashboard — CampusConnect"/>
</jsp:include>
<jsp:include page="/WEB-INF/views/common/navbar.jsp"/>

<div class="container py-4">
    <!-- User Welcome Header -->
    <div class="d-flex justify-content-between align-items-center flex-wrap gap-3 mb-4 pb-3 border-bottom">
        <div>
            <h2 class="fw-bold mb-1">Welcome back, ${sessionScope.currentUser.fullName}!</h2>
            <div class="d-flex align-items-center gap-2 text-muted small">
                <span class="badge badge-role-${sessionScope.currentUser.role}">${sessionScope.currentUser.role}</span>
                <span><i class="bi bi-building me-1"></i>${sessionScope.currentUser.department}</span>
                <span>&bull;</span>
                <span>Year ${sessionScope.currentUser.academicYear}</span>
                <span>&bull;</span>
                <span>ID: ${sessionScope.currentUser.registerNumber}</span>
            </div>
        </div>
        <div class="d-flex gap-2 align-items-center">
            <a href="${pageContext.request.contextPath}/credits" class="btn btn-outline-warning text-dark rounded-pill fw-bold px-3">
                <i class="bi bi-coin text-warning me-1"></i>${creditBalance} Credits
            </a>
            <a href="${pageContext.request.contextPath}/profile/edit" class="btn btn-outline-primary rounded-pill px-3">
                <i class="bi bi-pencil me-1"></i>Edit Profile
            </a>
        </div>
    </div>

    <jsp:include page="/WEB-INF/views/common/alerts.jsp"/>

    <!-- ==================== STUDENT DASHBOARD ==================== -->
    <c:if test="${sessionScope.currentUser.role == 'STUDENT'}">
        <div class="row g-4 mb-4">
            <!-- Upcoming Sessions -->
            <div class="col-lg-6">
                <div class="card card-cc h-100 p-4">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <h5 class="fw-bold mb-0"><i class="bi bi-calendar-check text-primary me-2"></i>Confirmed Mentorship Sessions</h5>
                        <a href="${pageContext.request.contextPath}/sessions" class="small fw-bold text-decoration-none">View All</a>
                    </div>
                    <c:choose>
                        <c:when test="${not empty upcomingSessions}">
                            <div class="list-group list-group-flush">
                                <c:forEach var="s" items="${upcomingSessions}">
                                    <div class="list-group-item px-0 py-2 d-flex justify-content-between align-items-center">
                                        <div>
                                            <div class="fw-bold text-dark">${s.skillName}</div>
                                            <div class="small text-muted">With Mentor: <strong>${s.mentorName}</strong></div>
                                            <div class="small text-secondary"><i class="bi bi-clock me-1"></i>${s.sessionDate} at ${s.startTime} - ${s.endTime}</div>
                                        </div>
                                        <a href="${pageContext.request.contextPath}/sessions/view?id=${s.id}" class="btn btn-outline-primary btn-sm rounded-pill">Details</a>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="text-center py-4 text-muted">
                                <i class="bi bi-calendar-x fs-1 d-block mb-2 text-secondary"></i>
                                <div>No upcoming confirmed sessions.</div>
                                <a href="${pageContext.request.contextPath}/mentors" class="btn btn-primary btn-sm rounded-pill mt-2">Find a Mentor</a>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <!-- Pending Booking Requests -->
            <div class="col-lg-6">
                <div class="card card-cc h-100 p-4">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <h5 class="fw-bold mb-0"><i class="bi bi-hourglass-split text-warning me-2"></i>Pending Booking Requests</h5>
                        <a href="${pageContext.request.contextPath}/sessions" class="small fw-bold text-decoration-none">View All</a>
                    </div>
                    <c:choose>
                        <c:when test="${not empty pendingSessions}">
                            <div class="list-group list-group-flush">
                                <c:forEach var="s" items="${pendingSessions}">
                                    <div class="list-group-item px-0 py-2 d-flex justify-content-between align-items-center">
                                        <div>
                                            <div class="fw-bold text-dark">${s.skillName}</div>
                                            <div class="small text-muted">Requested with: <strong>${s.mentorName}</strong></div>
                                            <div class="small text-secondary">${s.sessionDate} (${s.startTime} - ${s.endTime})</div>
                                        </div>
                                        <span class="badge badge-status-PENDING">Awaiting Mentor</span>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="text-center py-4 text-muted">
                                <i class="bi bi-check2-circle fs-1 d-block mb-2 text-success"></i>
                                <div>No pending requests at this time.</div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <!-- Recommended Mentors For You -->
        <c:if test="${not empty recommendedMentors}">
            <div class="card card-cc p-4 mb-4">
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <div>
                        <h5 class="fw-bold mb-0"><i class="bi bi-stars text-warning me-2"></i>Recommended Mentors For Your Learning Goals</h5>
                        <div class="small text-muted">Ranked using our weighted matching algorithm</div>
                    </div>
                    <a href="${pageContext.request.contextPath}/mentors" class="btn btn-outline-primary btn-sm rounded-pill">Browse All</a>
                </div>
                <div class="row g-3">
                    <c:forEach var="m" items="${recommendedMentors}">
                        <div class="col-md-6 col-lg-3">
                            <div class="card h-100 p-3 border shadow-none bg-light">
                                <div class="d-flex justify-content-between align-items-start mb-2">
                                    <div class="fw-bold">${m.mentor.fullName}</div>
                                    <span class="badge bg-primary rounded-pill"><fmt:formatNumber value="${m.totalScore * 100}" maxFractionDigits="0"/>% Match</span>
                                </div>
                                <div class="small text-muted mb-2">${m.mentor.department}</div>
                                <div class="star-rating small mb-2">
                                    <i class="bi bi-star-fill"></i> ${m.mentor.mentorRating} (${m.mentor.completedSessionsCount} sessions)
                                </div>
                                <div class="mt-auto d-flex gap-1">
                                    <a href="${pageContext.request.contextPath}/profile?id=${m.mentor.id}" class="btn btn-outline-secondary btn-sm rounded-pill flex-fill">Profile</a>
                                    <a href="${pageContext.request.contextPath}/sessions/book?mentorId=${m.mentor.id}" class="btn btn-primary btn-sm rounded-pill flex-fill">Book</a>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </c:if>
    </c:if>

    <!-- ==================== SENIOR / MENTOR DASHBOARD ==================== -->
    <c:if test="${sessionScope.currentUser.role == 'SENIOR'}">
        <div class="row g-4 mb-4">
            <!-- Pending Requests Requiring Decision -->
            <div class="col-lg-6">
                <div class="card card-cc h-100 p-4">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <h5 class="fw-bold mb-0"><i class="bi bi-bell-fill text-warning me-2"></i>Mentorship Requests Requiring Action</h5>
                        <span class="badge bg-warning text-dark">${not empty pendingRequests ? pendingRequests.size() : 0} Pending</span>
                    </div>
                    <c:choose>
                        <c:when test="${not empty pendingRequests}">
                            <div class="list-group list-group-flush">
                                <c:forEach var="s" items="${pendingRequests}">
                                    <div class="list-group-item px-0 py-2">
                                        <div class="d-flex justify-content-between align-items-start mb-1">
                                            <div>
                                                <div class="fw-bold">${s.learnerName}</div>
                                                <div class="small text-muted">Topic: <strong>${s.skillName}</strong></div>
                                                <div class="small text-secondary"><i class="bi bi-calendar me-1"></i>${s.sessionDate} at ${s.startTime} - ${s.endTime}</div>
                                            </div>
                                            <span class="credit-badge small">+${s.creditCost} Credits</span>
                                        </div>
                                        <c:if test="${not empty s.notes}">
                                            <div class="small text-muted bg-light p-2 rounded mb-2">"${s.notes}"</div>
                                        </c:if>
                                        <div class="d-flex gap-2">
                                            <form action="${pageContext.request.contextPath}/sessions/action" method="post" class="d-inline">
                                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                                <input type="hidden" name="sessionId" value="${s.id}">
                                                <input type="hidden" name="action" value="confirm">
                                                <button type="submit" class="btn btn-success btn-sm rounded-pill px-3">Confirm</button>
                                            </form>
                                            <form action="${pageContext.request.contextPath}/sessions/action" method="post" class="d-inline">
                                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                                <input type="hidden" name="sessionId" value="${s.id}">
                                                <input type="hidden" name="action" value="reject">
                                                <button type="submit" class="btn btn-outline-danger btn-sm rounded-pill px-3" data-confirm="Decline this session request?">Decline</button>
                                            </form>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="text-center py-4 text-muted">
                                <i class="bi bi-check-all fs-1 text-success d-block mb-2"></i>
                                <div>No pending session requests at this time.</div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <!-- Mentor Stats & Availability -->
            <div class="col-lg-6">
                <div class="card card-cc h-100 p-4">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <h5 class="fw-bold mb-0"><i class="bi bi-person-badge text-primary me-2"></i>Mentor Performance & Availability</h5>
                        <a href="${pageContext.request.contextPath}/profile/edit" class="btn btn-outline-primary btn-sm rounded-pill">Manage Slots</a>
                    </div>
                    <div class="row g-2 mb-3 text-center">
                        <div class="col-4">
                            <div class="p-2 border rounded bg-light">
                                <div class="fs-4 fw-bold text-primary">${sessionScope.currentUser.mentorRating}</div>
                                <div class="small text-muted">Average Rating</div>
                            </div>
                        </div>
                        <div class="col-4">
                            <div class="p-2 border rounded bg-light">
                                <div class="fs-4 fw-bold text-success">${sessionScope.currentUser.completedSessionsCount}</div>
                                <div class="small text-muted">Completed Sessions</div>
                            </div>
                        </div>
                        <div class="col-4">
                            <div class="p-2 border rounded bg-light">
                                <div class="fs-4 fw-bold text-info">${not empty endorsements ? endorsements.size() : 0}</div>
                                <div class="small text-muted">Endorsements</div>
                            </div>
                        </div>
                    </div>
                    <h6 class="fw-bold mb-2 small text-secondary">Active Weekly Slots:</h6>
                    <c:choose>
                        <c:when test="${not empty availabilities}">
                            <div class="d-flex flex-wrap gap-2">
                                <c:forEach var="slot" items="${availabilities}">
                                    <span class="badge bg-light text-dark border p-2">
                                        <i class="bi bi-clock me-1 text-primary"></i>${slot.dayOfWeek}: ${slot.startTime} - ${slot.endTime}
                                    </span>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="alert alert-warning py-2 small mb-0">
                                You have not added any availability slots yet. <a href="${pageContext.request.contextPath}/profile/edit">Add slots</a> to receive bookings.
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </c:if>

    <!-- ==================== ALUMNI DASHBOARD ==================== -->
    <c:if test="${sessionScope.currentUser.role == 'ALUMNI'}">
        <div class="card card-cc p-4 mb-4">
            <h5 class="fw-bold mb-3"><i class="bi bi-award-fill text-warning me-2"></i>Alumni Mentorship & Career Guidance Contributions</h5>
            <p class="text-muted small">
                As an alumni mentor, your industry experience helps students transition smoothly into tech careers, master real-world system design, and prepare for interviews.
            </p>
            <div class="d-flex gap-2 flex-wrap">
                <a href="${pageContext.request.contextPath}/resources/upload" class="btn btn-primary rounded-pill px-3">
                    <i class="bi bi-file-earmark-arrow-up me-1"></i>Share Career Roadmap / Notes
                </a>
                <a href="${pageContext.request.contextPath}/projects/new" class="btn btn-outline-primary rounded-pill px-3">
                    <i class="bi bi-plus-circle me-1"></i>Post Student Project
                </a>
                <a href="${pageContext.request.contextPath}/profile/edit" class="btn btn-outline-secondary rounded-pill px-3">
                    <i class="bi bi-calendar-plus me-1"></i>Update Availability
                </a>
            </div>
        </div>
    </c:if>

    <!-- ==================== ADMIN DASHBOARD ==================== -->
    <c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
        <div class="card card-cc p-4 mb-4 border-warning">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h5 class="fw-bold mb-0 text-dark"><i class="bi bi-shield-lock-fill text-danger me-2"></i>University Administrator Quick Panel</h5>
                <a href="${pageContext.request.contextPath}/admin" class="btn btn-danger btn-sm rounded-pill px-3">Open Full Admin Suite</a>
            </div>
            <div class="row g-3">
                <div class="col-md-3">
                    <div class="p-3 border rounded text-center bg-light">
                        <div class="fs-3 fw-bold text-primary">${platformStats.totalUsers}</div>
                        <div class="small text-muted">Total Users</div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="p-3 border rounded text-center bg-light">
                        <div class="fs-3 fw-bold text-success">${platformStats.completedSessions}</div>
                        <div class="small text-muted">Completed Sessions</div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="p-3 border rounded text-center bg-light">
                        <div class="fs-3 fw-bold text-info">${platformStats.totalResources}</div>
                        <div class="small text-muted">Resources Uploaded</div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="p-3 border rounded text-center bg-light">
                        <div class="fs-3 fw-bold text-danger">${platformStats.pendingReports}</div>
                        <div class="small text-muted">Pending Reports</div>
                    </div>
                </div>
            </div>
        </div>
    </c:if>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>