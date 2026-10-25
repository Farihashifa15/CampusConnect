<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Book Session with ${mentor.fullName} — CampusConnect"/>
</jsp:include>
<jsp:include page="/WEB-INF/views/common/navbar.jsp"/>

<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4 pb-2 border-bottom">
        <div>
            <h2 class="fw-bold mb-1">Book Mentorship Session</h2>
            <div class="text-muted small">Schedule an individual learning session without scheduling conflicts</div>
        </div>
        <a href="${pageContext.request.contextPath}/mentors" class="btn btn-outline-secondary rounded-pill px-3">
            <i class="bi bi-arrow-left me-1"></i>Back to Mentors
        </a>
    </div>

    <jsp:include page="/WEB-INF/views/common/alerts.jsp"/>

    <div class="row g-4 justify-content-center">
        <!-- Mentor Summary Sidebar -->
        <div class="col-lg-4">
            <div class="card card-cc p-4 mb-4">
                <div class="text-center mb-3">
                    <div class="bg-primary text-white rounded-circle d-inline-flex align-items-center justify-content-center mx-auto mb-2 shadow-sm" style="width: 60px; height: 60px; font-size: 1.5rem; font-weight: bold;">
                        ${mentor.fullName.substring(0, 1)}
                    </div>
                    <h5 class="fw-bold mb-0">${mentor.fullName}</h5>
                    <span class="badge badge-role-${mentor.role} small">${mentor.role}</span>
                    <div class="small text-muted mt-1">${mentor.department} &bull; Year ${mentor.academicYear}</div>
                    <div class="star-rating small mt-1">
                        <i class="bi bi-star-fill"></i> ${mentor.mentorRating} / 5 (${mentor.completedSessionsCount} sessions)
                    </div>
                </div>

                <hr>

                <h6 class="fw-bold text-secondary small text-uppercase mb-2">Mentor's Weekly Availability:</h6>
                <c:choose>
                    <c:when test="${not empty availabilities}">
                        <ul class="list-group list-group-flush small mb-3">
                            <c:forEach var="slot" items="${availabilities}">
                                <li class="list-group-item px-0 py-1 d-flex justify-content-between">
                                    <span class="fw-semibold">${slot.dayOfWeek}:</span>
                                    <span>${slot.startTime} - ${slot.endTime}</span>
                                </li>
                            </c:forEach>
                        </ul>
                    </c:when>
                    <c:otherwise>
                        <p class="small text-muted mb-3">No specific recurring slots listed.</p>
                    </c:otherwise>
                </c:choose>

                <!-- Skill Credits Escrow Info Box -->
                <div class="p-3 bg-light rounded border">
                    <div class="d-flex justify-content-between small text-muted mb-1">
                        <span>Required Session Fee:</span>
                        <span class="fw-bold text-dark">${creditCost} Credits</span>
                    </div>
                    <div class="d-flex justify-content-between small text-muted mb-2">
                        <span>Your Current Balance:</span>
                        <span class="fw-bold ${currentBalance >= creditCost ? 'text-success' : 'text-danger'}">${currentBalance} Credits</span>
                    </div>
                    <div class="small text-secondary" style="font-size: 0.8rem;">
                        <i class="bi bi-info-circle me-1"></i> Credits are held in escrow and debited upon request. If the session is declined or cancelled, your credits are refunded immediately.
                    </div>
                </div>
            </div>
        </div>

        <!-- Booking Form -->
        <div class="col-lg-7">
            <div class="card card-cc p-4">
                <h5 class="fw-bold mb-3"><i class="bi bi-calendar-plus text-primary me-2"></i>Select Topic & Session Interval</h5>

                <form action="${pageContext.request.contextPath}/sessions/book" method="post">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                    <input type="hidden" name="mentorId" value="${mentor.id}">

                    <div class="mb-3">
                        <label for="skillId" class="form-label fw-semibold">Mentorship Topic / Skill</label>
                        <select class="form-select" id="skillId" name="skillId" required>
                            <option value="" disabled selected>Select a skill the mentor offers...</option>
                            <c:forEach var="sk" items="${teachingSkills}">
                                <option value="${sk.skillId}">${sk.skillName} (${sk.proficiencyLevel})</option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="row g-3 mb-3">
                        <div class="col-md-4">
                            <label for="sessionDate" class="form-label fw-semibold">Session Date</label>
                            <input type="date" class="form-control" id="sessionDate" name="sessionDate" required>
                        </div>
                        <div class="col-md-4">
                            <label for="startTime" class="form-label fw-semibold">Start Time</label>
                            <input type="time" class="form-control" id="startTime" name="startTime" value="10:00" required>
                        </div>
                        <div class="col-md-4">
                            <label for="endTime" class="form-label fw-semibold">End Time</label>
                            <input type="time" class="form-control" id="endTime" name="endTime" value="11:00" required>
                        </div>
                    </div>

                    <div class="mb-4">
                        <label for="notes" class="form-label fw-semibold">Session Notes & Objectives</label>
                        <textarea class="form-control" id="notes" name="notes" rows="4" placeholder="Mention the specific topics, assignment questions, or interview topics you want to cover with your mentor." required></textarea>
                    </div>

                    <c:choose>
                        <c:when test="${currentBalance >= creditCost}">
                            <button type="submit" class="btn btn-primary rounded-pill px-4 fw-bold shadow-sm">
                                <i class="bi bi-check-circle me-1"></i>Confirm & Escrow ${creditCost} Credits
                            </button>
                        </c:when>
                        <c:otherwise>
                            <div class="alert alert-danger py-2 small mb-3">
                                <i class="bi bi-exclamation-octagon-fill me-1"></i> You need at least ${creditCost} Skill Credits to book this session. Answer questions on the Doubt Board or share notes to earn credits.
                            </div>
                            <button type="button" class="btn btn-secondary rounded-pill px-4 fw-bold" disabled>
                                Insufficient Credits
                            </button>
                        </c:otherwise>
                    </c:choose>
                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>