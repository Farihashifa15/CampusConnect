<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="${project.title} — CampusConnect Projects"/>
</jsp:include>
<jsp:include page="/WEB-INF/views/common/navbar.jsp"/>

<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4 pb-2 border-bottom">
        <div>
            <div class="d-flex align-items-center gap-2 mb-1">
                <span class="badge bg-light text-dark border">${project.category}</span>
                <span class="badge badge-status-${project.status}">${project.status}</span>
            </div>
            <h2 class="fw-bold mb-0 text-dark">${project.title}</h2>
        </div>
        <a href="${pageContext.request.contextPath}/projects" class="btn btn-outline-secondary rounded-pill px-3">
            <i class="bi bi-arrow-left me-1"></i>Back to Projects
        </a>
    </div>

    <jsp:include page="/WEB-INF/views/common/alerts.jsp"/>

    <div class="row g-4">
        <!-- Main Project Description -->
        <div class="col-lg-8">
            <div class="card card-cc p-4 mb-4">
                <h5 class="fw-bold mb-3"><i class="bi bi-file-text text-primary me-2"></i>Project Overview</h5>
                <p class="text-dark mb-4" style="line-height: 1.7; white-space: pre-wrap;">${project.description}</p>

                <div class="mb-4">
                    <h6 class="fw-bold text-secondary small text-uppercase mb-2">Required Skills / Roles Needed:</h6>
                    <div class="d-flex flex-wrap gap-2">
                        <c:forEach var="sk" items="${project.requiredSkills.split(',')}">
                            <span class="badge bg-primary-subtle text-primary border border-primary-subtle p-2">
                                <i class="bi bi-check2 me-1"></i>${sk.trim()}
                            </span>
                        </c:forEach>
                    </div>
                </div>

                <div class="d-flex justify-content-between align-items-center pt-3 border-top small text-muted">
                    <div>Created on ${project.createdAt}</div>
                    <button type="button" class="btn btn-outline-danger btn-sm rounded-pill px-2 py-0" data-bs-toggle="modal" data-bs-target="#reportModal">
                        <i class="bi bi-flag"></i> Report
                    </button>
                </div>
            </div>

            <!-- Team Lead Reviewing Applications -->
            <c:if test="${isOwner}">
                <div class="card card-cc p-4 mb-4">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <h5 class="fw-bold mb-0"><i class="bi bi-people-fill text-primary me-2"></i>Project Applicants (${not empty applications ? applications.size() : 0})</h5>
                        <span class="badge bg-light text-secondary border">${project.filledPositions} of ${project.availablePositions} positions filled</span>
                    </div>

                    <c:choose>
                        <c:when test="${not empty applications}">
                            <div class="list-group list-group-flush">
                                <c:forEach var="app" items="${applications}">
                                    <div class="list-group-item px-0 py-3">
                                        <div class="d-flex justify-content-between align-items-start mb-2">
                                            <div>
                                                <a href="${pageContext.request.contextPath}/profile?id=${app.applicantId}" class="fw-bold text-dark text-decoration-none fs-6">
                                                    ${app.applicantName}
                                                </a>
                                                <span class="small text-muted ms-1">(${app.applicantDepartment})</span>
                                                <div class="small text-secondary">Applied on ${app.createdAt}</div>
                                            </div>
                                            <span class="badge badge-status-${app.status}">${app.status}</span>
                                        </div>
                                        <div class="small bg-light p-3 rounded mb-2 border">
                                            <strong>Applicant Pitch:</strong> "${app.pitch}"
                                        </div>
                                        <c:if test="${app.status == 'PENDING'}">
                                            <div class="d-flex gap-2">
                                                <form action="${pageContext.request.contextPath}/projects/application/respond" method="post" class="d-inline">
                                                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                                    <input type="hidden" name="projectId" value="${project.id}">
                                                    <input type="hidden" name="applicationId" value="${app.id}">
                                                    <input type="hidden" name="decision" value="accept">
                                                    <button type="submit" class="btn btn-success btn-sm rounded-pill px-3" ${!project.hasOpenPositions() ? 'disabled' : ''}>
                                                        <i class="bi bi-check-lg me-1"></i>Accept onto Team
                                                    </button>
                                                </form>
                                                <form action="${pageContext.request.contextPath}/projects/application/respond" method="post" class="d-inline">
                                                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                                    <input type="hidden" name="projectId" value="${project.id}">
                                                    <input type="hidden" name="applicationId" value="${app.id}">
                                                    <input type="hidden" name="decision" value="reject">
                                                    <button type="submit" class="btn btn-outline-danger btn-sm rounded-pill px-3" data-confirm="Decline this student application?">
                                                        Decline
                                                    </button>
                                                </form>
                                            </div>
                                        </c:if>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="text-center py-4 text-muted small">
                                No students have applied to this project listing yet.
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </c:if>

            <!-- Student Application Form -->
            <c:if test="${!isOwner}">
                <div class="card card-cc p-4">
                    <h5 class="fw-bold mb-3"><i class="bi bi-person-plus text-primary me-2"></i>Join This Project Team</h5>

                    <c:choose>
                        <c:when test="${not empty myApplication}">
                            <div class="alert alert-info py-3 mb-0">
                                <div class="d-flex align-items-center gap-2 mb-1">
                                    <strong>Application Status:</strong>
                                    <span class="badge badge-status-${myApplication.status}">${myApplication.status}</span>
                                </div>
                                <div class="small text-muted">You submitted an application on ${myApplication.createdAt}:</div>
                                <div class="small fst-italic mt-1">"${myApplication.pitch}"</div>
                            </div>
                        </c:when>
                        <c:when test="${!project.hasOpenPositions()}">
                            <div class="alert alert-secondary py-3 mb-0">
                                <i class="bi bi-lock-fill me-1"></i> This project has filled all ${project.availablePositions} available positions and is closed to new applicants.
                            </div>
                        </c:when>
                        <c:when test="${not empty sessionScope.currentUser}">
                            <form action="${pageContext.request.contextPath}/projects/apply" method="post">
                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                <input type="hidden" name="projectId" value="${project.id}">

                                <div class="mb-3">
                                    <label class="form-label fw-semibold">Your Pitch & Experience</label>
                                    <textarea class="form-control" name="pitch" rows="3" placeholder="Briefly state your relevant skills, past coursework/projects, and what role you would like to play on the team." required></textarea>
                                </div>
                                <button type="submit" class="btn btn-primary rounded-pill px-4 fw-bold shadow-sm">
                                    <i class="bi bi-send-fill me-1"></i>Submit Application
                                </button>
                            </form>
                        </c:when>
                        <c:otherwise>
                            <div class="alert alert-info py-2 small mb-0">
                                Please <a href="${pageContext.request.contextPath}/auth/login">sign in with your university account</a> to apply for this project.
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </c:if>
        </div>

        <!-- Right Sidebar Info -->
        <div class="col-lg-4">
            <!-- Project Lead Card -->
            <div class="card card-cc p-4 mb-4">
                <h6 class="fw-bold text-secondary text-uppercase small mb-3">Project Lead</h6>
                <div class="d-flex align-items-center gap-3 mb-3">
                    <div class="bg-primary text-white rounded-circle d-flex align-items-center justify-content-center" style="width: 48px; height: 48px; font-weight: bold; font-size: 1.25rem;">
                        ${project.creatorName.substring(0, 1)}
                    </div>
                    <div>
                        <a href="${pageContext.request.contextPath}/profile?id=${project.creatorId}" class="fw-bold text-dark text-decoration-none">
                            ${project.creatorName}
                        </a>
                        <div><span class="badge badge-role-${project.creatorRole} small">${project.creatorRole}</span></div>
                    </div>
                </div>
                <div class="d-grid">
                    <a href="${pageContext.request.contextPath}/profile?id=${project.creatorId}" class="btn btn-outline-secondary btn-sm rounded-pill">
                        View Lead's Profile
                    </a>
                </div>
            </div>

            <!-- Team Capacity -->
            <div class="card card-cc p-4">
                <h6 class="fw-bold text-secondary text-uppercase small mb-3">Team Capacity</h6>
                <div class="d-flex justify-content-between mb-2 small">
                    <span>Positions Filled:</span>
                    <span class="fw-bold">${project.filledPositions} of ${project.availablePositions}</span>
                </div>
                <div class="progress mb-2" style="height: 8px;">
                    <div class="progress-bar bg-primary" role="progressbar" style="width: ${(project.filledPositions / project.availablePositions) * 100}%;"></div>
                </div>
                <div class="small text-muted">
                    <c:choose>
                        <c:when test="${project.hasOpenPositions()}">
                            <span class="text-success"><i class="bi bi-check-circle-fill me-1"></i>${project.availablePositions - project.filledPositions} spot(s) remaining</span>
                        </c:when>
                        <c:otherwise>
                            <span class="text-secondary"><i class="bi bi-x-circle me-1"></i>Team is currently full</span>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Report Modal -->
<div class="modal fade" id="reportModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <form action="${pageContext.request.contextPath}/projects/report" method="post">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                <input type="hidden" name="projectId" value="${project.id}">

                <div class="modal-header">
                    <h5 class="modal-title fw-bold"><i class="bi bi-flag-fill text-danger me-2"></i>Report Project Listing</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Reason for Report</label>
                        <textarea class="form-control" name="reason" rows="3" placeholder="Explain why this project violates university policy or guidelines." required></textarea>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary rounded-pill px-3" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-danger rounded-pill px-4">Submit Report</button>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>