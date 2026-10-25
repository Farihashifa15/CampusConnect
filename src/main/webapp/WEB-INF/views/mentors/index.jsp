<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Find Mentors — CampusConnect"/>
</jsp:include>
<jsp:include page="/WEB-INF/views/common/navbar.jsp"/>

<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center flex-wrap gap-2 mb-4 pb-2 border-bottom">
        <div>
            <h2 class="fw-bold mb-1">Find a University Mentor</h2>
            <div class="text-muted small">Weighted mentor ranking by requested skill, rating, and schedule availability</div>
        </div>
    </div>

    <jsp:include page="/WEB-INF/views/common/alerts.jsp"/>

    <!-- Search & Filter Bar -->
    <div class="card card-cc p-4 mb-4">
        <form action="${pageContext.request.contextPath}/mentors" method="get">
            <div class="row g-3">
                <div class="col-md-4">
                    <label class="form-label fw-semibold small">Skill Required</label>
                    <select class="form-select" name="skillId">
                        <option value="">All Skills / Any Expertise</option>
                        <c:forEach var="sk" items="${allSkills}">
                            <option value="${sk.id}" ${selectedSkillId == sk.id ? 'selected' : ''}>${sk.name} (${sk.category})</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-3">
                    <label class="form-label fw-semibold small">Preferred Day</label>
                    <select class="form-select" name="dayOfWeek">
                        <option value="">Any Day of the Week</option>
                        <option value="MONDAY" ${selectedDay == 'MONDAY' ? 'selected' : ''}>Monday</option>
                        <option value="TUESDAY" ${selectedDay == 'TUESDAY' ? 'selected' : ''}>Tuesday</option>
                        <option value="WEDNESDAY" ${selectedDay == 'WEDNESDAY' ? 'selected' : ''}>Wednesday</option>
                        <option value="THURSDAY" ${selectedDay == 'THURSDAY' ? 'selected' : ''}>Thursday</option>
                        <option value="FRIDAY" ${selectedDay == 'FRIDAY' ? 'selected' : ''}>Friday</option>
                        <option value="SATURDAY" ${selectedDay == 'SATURDAY' ? 'selected' : ''}>Saturday</option>
                        <option value="SUNDAY" ${selectedDay == 'SUNDAY' ? 'selected' : ''}>Sunday</option>
                    </select>
                </div>
                <div class="col-md-2">
                    <label class="form-label fw-semibold small">Start Time</label>
                    <input type="time" class="form-control" name="startTime" value="${selectedStartTime}">
                </div>
                <div class="col-md-2">
                    <label class="form-label fw-semibold small">End Time</label>
                    <input type="time" class="form-control" name="endTime" value="${selectedEndTime}">
                </div>
                <div class="col-md-1 d-flex align-items-end">
                    <button type="submit" class="btn btn-primary w-100 rounded-pill"><i class="bi bi-search"></i></button>
                </div>
            </div>
        </form>
    </div>

    <!-- Algorithm Results Explanation -->
    <div class="d-flex justify-content-between align-items-center mb-3 text-muted small">
        <div>Showing <strong>${not empty mentors ? mentors.size() : 0}</strong> mentors ranked by match score</div>
        <div class="text-secondary fst-italic">
            Formula: 0.5 &times; SkillMatch + 0.3 &times; NormalizedRating + 0.2 &times; Availability
        </div>
    </div>

    <!-- Mentor Cards Grid -->
    <c:choose>
        <c:when test="${not empty mentors}">
            <div class="row g-4">
                <c:forEach var="res" items="${mentors}">
                    <div class="col-md-6 col-lg-4">
                        <div class="card card-cc h-100 p-4">
                            <div class="d-flex justify-content-between align-items-start mb-2">
                                <div>
                                    <h5 class="fw-bold mb-0">${res.mentor.fullName}</h5>
                                    <span class="badge badge-role-${res.mentor.role} small">${res.mentor.role}</span>
                                    <span class="small text-muted ms-1">${res.mentor.department} &bull; Yr ${res.mentor.academicYear}</span>
                                </div>
                                <span class="match-score-pill">
                                    <fmt:formatNumber value="${res.totalScore * 100}" maxFractionDigits="0"/>% Match
                                </span>
                            </div>

                            <p class="small text-muted mb-3" style="min-height: 40px;">
                                ${not empty res.mentor.bio ? res.mentor.bio : 'Experienced university member offering peer academic guidance.'}
                            </p>

                            <div class="mb-3">
                                <div class="small fw-bold text-secondary mb-1">Teaching Skills:</div>
                                <div class="d-flex flex-wrap gap-1">
                                    <c:forEach var="sk" items="${res.offeredSkills}">
                                        <span class="badge ${sk.skillId == selectedSkillId ? 'bg-primary text-white' : 'bg-light text-dark border'} small">
                                            ${sk.skillName}
                                        </span>
                                    </c:forEach>
                                </div>
                            </div>

                            <div class="mb-3 small">
                                <div class="d-flex justify-content-between text-muted mb-1">
                                    <span>Peer Rating:</span>
                                    <span class="star-rating fw-bold text-dark"><i class="bi bi-star-fill"></i> ${res.mentor.mentorRating} / 5</span>
                                </div>
                                <div class="d-flex justify-content-between text-muted mb-1">
                                    <span>Sessions Completed:</span>
                                    <span class="fw-semibold text-dark">${res.mentor.completedSessionsCount}</span>
                                </div>
                                <div class="d-flex justify-content-between text-muted">
                                    <span>Available Slots:</span>
                                    <span class="badge ${res.availability > 0 ? 'bg-success-subtle text-success border border-success-subtle' : 'bg-secondary-subtle text-secondary'}">
                                        ${res.availableSlots.size()} Active Slots
                                    </span>
                                </div>
                            </div>

                            <div class="mt-auto d-flex gap-2">
                                <a href="${pageContext.request.contextPath}/profile?id=${res.mentor.id}" class="btn btn-outline-secondary btn-sm flex-fill rounded-pill">View Profile</a>
                                <a href="${pageContext.request.contextPath}/sessions/book?mentorId=${res.mentor.id}${not empty selectedSkillId ? '&skillId='.concat(selectedSkillId) : ''}" class="btn btn-primary btn-sm flex-fill rounded-pill">Book Session</a>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:when>
        <c:otherwise>
            <div class="card card-cc p-5 text-center my-4">
                <i class="bi bi-people display-4 text-secondary mb-3"></i>
                <h4 class="fw-bold">No Mentors Matched</h4>
                <p class="text-muted">No mentors offering the selected criteria were found. Try removing filters or searching by a different skill.</p>
                <div>
                    <a href="${pageContext.request.contextPath}/mentors" class="btn btn-primary rounded-pill px-4">Reset Filters</a>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>