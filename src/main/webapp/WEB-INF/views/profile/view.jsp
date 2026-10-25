<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="${profileUser.fullName} — CampusConnect Profile"/>
</jsp:include>
<jsp:include page="/WEB-INF/views/common/navbar.jsp"/>

<div class="container py-4">
    <jsp:include page="/WEB-INF/views/common/alerts.jsp"/>

    <div class="row g-4">
        <!-- Profile Sidebar -->
        <div class="col-lg-4">
            <div class="card card-cc p-4 text-center mb-4">
                <div class="bg-primary text-white rounded-circle d-inline-flex align-items-center justify-content-center mx-auto mb-3 shadow-sm" style="width: 80px; height: 80px; font-size: 2rem; font-weight: bold;">
                    ${profileUser.fullName.substring(0, 1)}
                </div>
                <h4 class="fw-bold mb-1">${profileUser.fullName}</h4>
                <div class="mb-2">
                    <span class="badge badge-role-${profileUser.role}">${profileUser.role}</span>
                </div>
                <div class="small text-muted mb-3">
                    <div><i class="bi bi-building me-1"></i>${profileUser.department}</div>
                    <div>Year ${profileUser.academicYear} &bull; ID: ${profileUser.registerNumber}</div>
                </div>

                <!-- Rating & Sessions Stats -->
                <div class="row g-2 p-2 bg-light rounded border mb-3 text-center">
                    <div class="col-6">
                        <div class="fw-bold fs-5 text-warning">
                            <i class="bi bi-star-fill"></i> ${profileUser.mentorRating}
                        </div>
                        <div class="small text-muted" style="font-size: 0.75rem;">Mentor Rating</div>
                    </div>
                    <div class="col-6">
                        <div class="fw-bold fs-5 text-primary">${profileUser.completedSessionsCount}</div>
                        <div class="small text-muted" style="font-size: 0.75rem;">Completed Sessions</div>
                    </div>
                </div>

                <div class="d-grid gap-2">
                    <c:choose>
                        <c:when test="${isOwnProfile}">
                            <a href="${pageContext.request.contextPath}/profile/edit" class="btn btn-outline-primary rounded-pill">
                                <i class="bi bi-pencil-square me-1"></i>Edit Profile & Skills
                            </a>
                        </c:when>
                        <c:otherwise>
                            <c:if test="${profileUser.role == 'SENIOR' || profileUser.role == 'ALUMNI' || profileUser.role == 'STUDENT'}">
                                <a href="${pageContext.request.contextPath}/sessions/book?mentorId=${profileUser.id}" class="btn btn-primary rounded-pill shadow-sm">
                                    <i class="bi bi-calendar-plus me-1"></i>Book Mentorship Session
                                </a>
                            </c:if>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <!-- Mentor Availability Slots -->
            <div class="card card-cc p-4">
                <h5 class="fw-bold mb-3"><i class="bi bi-clock-history text-primary me-2"></i>Availability Schedule</h5>
                <c:choose>
                    <c:when test="${not empty availabilities}">
                        <ul class="list-group list-group-flush small">
                            <c:forEach var="slot" items="${availabilities}">
                                <li class="list-group-item px-0 d-flex justify-content-between align-items-center">
                                    <span class="fw-semibold">${slot.dayOfWeek}</span>
                                    <span class="badge bg-light text-dark border">${slot.startTime} - ${slot.endTime}</span>
                                </li>
                            </c:forEach>
                        </ul>
                    </c:when>
                    <c:otherwise>
                        <p class="small text-muted mb-0">No active availability slots listed.</p>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- Profile Details -->
        <div class="col-lg-8">
            <!-- Bio & Background -->
            <div class="card card-cc p-4 mb-4">
                <h5 class="fw-bold mb-3"><i class="bi bi-person-lines-fill text-primary me-2"></i>About & Academic Interests</h5>
                <p class="text-muted">${not empty profileUser.bio ? profileUser.bio : 'No personal bio added yet.'}</p>

                <div class="row g-3 mt-1">
                    <div class="col-md-6">
                        <div class="p-3 bg-light rounded border">
                            <h6 class="fw-bold text-secondary small mb-1"><i class="bi bi-bullseye me-1"></i>Career Goals</h6>
                            <p class="small text-dark mb-0">${not empty profileUser.careerGoals ? profileUser.careerGoals : 'Not specified'}</p>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="p-3 bg-light rounded border">
                            <h6 class="fw-bold text-secondary small mb-1"><i class="bi bi-code-slash me-1"></i>Project Interests</h6>
                            <p class="small text-dark mb-0">${not empty profileUser.projectInterests ? profileUser.projectInterests : 'Not specified'}</p>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Skills Section (TEACH vs LEARN) -->
            <div class="card card-cc p-4 mb-4">
                <h5 class="fw-bold mb-3"><i class="bi bi-award text-success me-2"></i>Skills & Expertise</h5>
                <div class="row g-4">
                    <div class="col-md-6">
                        <h6 class="fw-bold text-primary small text-uppercase">Can Teach / Mentor:</h6>
                        <c:choose>
                            <c:when test="${not empty teachingSkills}">
                                <div class="d-flex flex-wrap gap-2 mt-2">
                                    <c:forEach var="sk" items="${teachingSkills}">
                                        <span class="badge bg-primary-subtle text-primary border border-primary-subtle p-2">
                                            <strong>${sk.skillName}</strong> <span class="badge bg-primary text-white ms-1">${sk.proficiencyLevel}</span>
                                        </span>
                                    </c:forEach>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <p class="small text-muted mt-2">Not offering mentorship in any skills yet.</p>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <div class="col-md-6">
                        <h6 class="fw-bold text-secondary small text-uppercase">Wants to Learn:</h6>
                        <c:choose>
                            <c:when test="${not empty learningSkills}">
                                <div class="d-flex flex-wrap gap-2 mt-2">
                                    <c:forEach var="sk" items="${learningSkills}">
                                        <span class="badge bg-light text-dark border p-2">
                                            ${sk.skillName} <span class="badge bg-secondary text-white ms-1">${sk.proficiencyLevel}</span>
                                        </span>
                                    </c:forEach>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <p class="small text-muted mt-2">No learning goals listed yet.</p>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>

            <!-- Endorsements Received -->
            <div class="card card-cc p-4 mb-4">
                <h5 class="fw-bold mb-3"><i class="bi bi-patch-check-fill text-info me-2"></i>Verified Peer Endorsements</h5>
                <c:choose>
                    <c:when test="${not empty endorsements}">
                        <div class="list-group list-group-flush">
                            <c:forEach var="end" items="${endorsements}">
                                <div class="list-group-item px-0 py-2">
                                    <div class="d-flex justify-content-between align-items-center">
                                        <span class="badge bg-info text-dark fw-bold">${end.skillName}</span>
                                        <span class="small text-muted">Endorsed by <strong>${end.learnerName}</strong></span>
                                    </div>
                                    <div class="small text-muted mt-1 fst-italic">"${end.comment}"</div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <p class="small text-muted mb-0">No verified endorsements received yet. Complete mentorship sessions to receive skill endorsements!</p>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- Feedback Received -->
            <div class="card card-cc p-4">
                <h5 class="fw-bold mb-3"><i class="bi bi-chat-quote-fill text-warning me-2"></i>Student Session Feedback</h5>
                <c:choose>
                    <c:when test="${not empty feedbacks}">
                        <div class="list-group list-group-flush">
                            <c:forEach var="fb" items="${feedbacks}">
                                <div class="list-group-item px-0 py-3">
                                    <div class="d-flex justify-content-between align-items-center mb-1">
                                        <div class="star-rating small">
                                            <c:forEach begin="1" end="${fb.rating}"><i class="bi bi-star-fill"></i></c:forEach>
                                            <span class="fw-bold text-dark ms-1">${fb.rating} / 5</span>
                                        </div>
                                        <span class="small text-muted">By ${fb.learnerName}</span>
                                    </div>
                                    <div class="text-dark small">${fb.feedbackText}</div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <p class="small text-muted mb-0">No session reviews received yet.</p>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>