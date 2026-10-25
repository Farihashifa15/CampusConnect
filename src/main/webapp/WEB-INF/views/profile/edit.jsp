<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Edit Profile & Skills — CampusConnect"/>
</jsp:include>
<jsp:include page="/WEB-INF/views/common/navbar.jsp"/>

<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4 pb-2 border-bottom">
        <div>
            <h2 class="fw-bold mb-1">Profile & Skills Management</h2>
            <div class="text-muted small">Update your bio, teaching expertise, learning goals, and mentor availability</div>
        </div>
        <a href="${pageContext.request.contextPath}/profile" class="btn btn-outline-secondary rounded-pill px-3">
            <i class="bi bi-arrow-left me-1"></i>View Profile
        </a>
    </div>

    <jsp:include page="/WEB-INF/views/common/alerts.jsp"/>

    <div class="row g-4">
        <!-- 1. Basic Info Form -->
        <div class="col-lg-6">
            <div class="card card-cc p-4 mb-4">
                <h5 class="fw-bold mb-3"><i class="bi bi-person-gear text-primary me-2"></i>Personal & Academic Information</h5>
                <form action="${pageContext.request.contextPath}/profile/edit" method="post">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                    <div class="mb-3">
                        <label class="form-label fw-semibold">Full Name</label>
                        <input type="text" class="form-control" name="fullName" value="${user.fullName}" required>
                    </div>

                    <div class="row g-3 mb-3">
                        <div class="col-md-8">
                            <label class="form-label fw-semibold">Department</label>
                            <input type="text" class="form-control" name="department" value="${user.department}" required>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label fw-semibold">Academic Year</label>
                            <input type="number" class="form-control" name="academicYear" value="${user.academicYear}" min="1" max="6">
                        </div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-semibold">Bio / Introduction</label>
                        <textarea class="form-control" name="bio" rows="3" placeholder="Tell other students about yourself, your interests, and your academic background">${user.bio}</textarea>
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-semibold">Career Goals</label>
                        <input type="text" class="form-control" name="careerGoals" value="${user.careerGoals}" placeholder="e.g. Distributed Systems Engineer at tech firm">
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-semibold">Project Interests</label>
                        <input type="text" class="form-control" name="projectInterests" value="${user.projectInterests}" placeholder="e.g. Web platforms, AI, autonomous robotics">
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-semibold">Club Memberships & Activities</label>
                        <input type="text" class="form-control" name="clubMemberships" value="${user.clubMemberships}" placeholder="e.g. ACM Chapter, GDSC, IEEE">
                    </div>

                    <button type="submit" class="btn btn-primary rounded-pill px-4 fw-bold">Save Information</button>
                </form>
            </div>
        </div>

        <!-- 2. Skills & Availability Management -->
        <div class="col-lg-6">
            <!-- Skills Management -->
            <div class="card card-cc p-4 mb-4">
                <h5 class="fw-bold mb-3"><i class="bi bi-award text-success me-2"></i>Manage Skills</h5>

                <!-- Add Skill Form -->
                <form action="${pageContext.request.contextPath}/profile/skills/add" method="post" class="p-3 bg-light rounded border mb-4">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                    <h6 class="fw-bold mb-2 small text-secondary">Add New Skill:</h6>
                    <div class="row g-2 mb-2">
                        <div class="col-md-6">
                            <select class="form-select form-select-sm" name="skillId" required>
                                <option value="" disabled selected>Select a Skill...</option>
                                <c:forEach var="sk" items="${allSkills}">
                                    <option value="${sk.id}">${sk.name} (${sk.category})</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-3">
                            <select class="form-select form-select-sm" name="skillType">
                                <option value="TEACH">Can Teach</option>
                                <option value="LEARN">Want to Learn</option>
                            </select>
                        </div>
                        <div class="col-md-3">
                            <select class="form-select form-select-sm" name="proficiencyLevel">
                                <option value="BEGINNER">Beginner</option>
                                <option value="INTERMEDIATE" selected>Intermediate</option>
                                <option value="ADVANCED">Advanced</option>
                            </select>
                        </div>
                    </div>
                    <button type="submit" class="btn btn-success btn-sm rounded-pill px-3">Add Skill</button>
                </form>

                <!-- Current Skills Lists -->
                <div class="mb-3">
                    <h6 class="fw-bold text-primary small text-uppercase">Skills You Can Teach (Mentorship):</h6>
                    <c:choose>
                        <c:when test="${not empty teachingSkills}">
                            <div class="d-flex flex-wrap gap-2 mt-1">
                                <c:forEach var="sk" items="${teachingSkills}">
                                    <span class="badge bg-primary-subtle text-primary border border-primary-subtle p-2 d-flex align-items-center gap-1">
                                        <strong>${sk.skillName}</strong> (${sk.proficiencyLevel})
                                        <form action="${pageContext.request.contextPath}/profile/skills/remove" method="post" class="d-inline m-0">
                                            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                            <input type="hidden" name="skillId" value="${sk.skillId}">
                                            <input type="hidden" name="skillType" value="TEACH">
                                            <button type="submit" class="btn-close btn-close-white" style="font-size: 0.5rem;" title="Remove"></button>
                                        </form>
                                    </span>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise><p class="small text-muted mb-0">None listed.</p></c:otherwise>
                    </c:choose>
                </div>

                <div>
                    <h6 class="fw-bold text-secondary small text-uppercase">Skills You Want To Learn:</h6>
                    <c:choose>
                        <c:when test="${not empty learningSkills}">
                            <div class="d-flex flex-wrap gap-2 mt-1">
                                <c:forEach var="sk" items="${learningSkills}">
                                    <span class="badge bg-light text-dark border p-2 d-flex align-items-center gap-1">
                                        ${sk.skillName} (${sk.proficiencyLevel})
                                        <form action="${pageContext.request.contextPath}/profile/skills/remove" method="post" class="d-inline m-0">
                                            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                            <input type="hidden" name="skillId" value="${sk.skillId}">
                                            <input type="hidden" name="skillType" value="LEARN">
                                            <button type="submit" class="btn-close" style="font-size: 0.5rem;" title="Remove"></button>
                                        </form>
                                    </span>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise><p class="small text-muted mb-0">None listed.</p></c:otherwise>
                    </c:choose>
                </div>
            </div>

            <!-- Availability Schedule Management -->
            <div class="card card-cc p-4">
                <h5 class="fw-bold mb-3"><i class="bi bi-clock-history text-primary me-2"></i>Mentor Availability Slots</h5>

                <!-- Add Slot Form -->
                <form action="${pageContext.request.contextPath}/profile/availability/add" method="post" class="p-3 bg-light rounded border mb-4">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                    <h6 class="fw-bold mb-2 small text-secondary">Add Recurring Slot:</h6>
                    <div class="row g-2 mb-2">
                        <div class="col-md-4">
                            <select class="form-select form-select-sm" name="dayOfWeek">
                                <option value="MONDAY">Monday</option>
                                <option value="TUESDAY">Tuesday</option>
                                <option value="WEDNESDAY">Wednesday</option>
                                <option value="THURSDAY">Thursday</option>
                                <option value="FRIDAY">Friday</option>
                                <option value="SATURDAY">Saturday</option>
                                <option value="SUNDAY">Sunday</option>
                            </select>
                        </div>
                        <div class="col-md-4">
                            <input type="time" class="form-control form-control-sm" name="startTime" value="10:00" required>
                        </div>
                        <div class="col-md-4">
                            <input type="time" class="form-control form-control-sm" name="endTime" value="12:00" required>
                        </div>
                    </div>
                    <button type="submit" class="btn btn-primary btn-sm rounded-pill px-3">Add Slot</button>
                </form>

                <!-- Current Slots -->
                <c:choose>
                    <c:when test="${not empty availabilities}">
                        <ul class="list-group list-group-flush small">
                            <c:forEach var="slot" items="${availabilities}">
                                <li class="list-group-item px-0 d-flex justify-content-between align-items-center">
                                    <span><strong>${slot.dayOfWeek}</strong> &bull; ${slot.startTime} - ${slot.endTime}</span>
                                    <form action="${pageContext.request.contextPath}/profile/availability/delete" method="post" class="d-inline m-0">
                                        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                        <input type="hidden" name="slotId" value="${slot.id}">
                                        <button type="submit" class="btn btn-outline-danger btn-sm py-0 px-2 rounded-pill" data-confirm="Remove this availability slot?">
                                            <i class="bi bi-trash"></i>
                                        </button>
                                    </form>
                                </li>
                            </c:forEach>
                        </ul>
                    </c:when>
                    <c:otherwise>
                        <p class="small text-muted mb-0">No active availability slots.</p>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>