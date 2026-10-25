<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Create Project Listing — CampusConnect"/>
</jsp:include>
<jsp:include page="/WEB-INF/views/common/navbar.jsp"/>

<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4 pb-2 border-bottom">
        <div>
            <h2 class="fw-bold mb-1">Post a Collaboration Project</h2>
            <div class="text-muted small">Recruit fellow university students with complementary skills</div>
        </div>
        <a href="${pageContext.request.contextPath}/projects" class="btn btn-outline-secondary rounded-pill px-3">
            <i class="bi bi-arrow-left me-1"></i>Back to Projects
        </a>
    </div>

    <jsp:include page="/WEB-INF/views/common/alerts.jsp"/>

    <div class="row justify-content-center">
        <div class="col-lg-8">
            <div class="card card-cc p-4 p-md-5">
                <form action="${pageContext.request.contextPath}/projects/new" method="post">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                    <div class="mb-3">
                        <label for="title" class="form-label fw-semibold">Project Title</label>
                        <input type="text" class="form-control" id="title" name="title" placeholder="e.g. AI-Assisted Campus Navigation System" required>
                    </div>

                    <div class="row g-3 mb-3">
                        <div class="col-md-6">
                            <label for="category" class="form-label fw-semibold">Project Domain / Category</label>
                            <select class="form-select" id="category" name="category" required>
                                <option value="Mobile & Web">Mobile & Web</option>
                                <option value="Artificial Intelligence">Artificial Intelligence</option>
                                <option value="Productivity Tools">Productivity Tools</option>
                                <option value="Robotics & Embedded Systems">Robotics & Embedded Systems</option>
                                <option value="General Engineering">General Engineering</option>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label for="availablePositions" class="form-label fw-semibold">Available Positions / Open Roles</label>
                            <input type="number" class="form-control" id="availablePositions" name="availablePositions" value="2" min="1" max="10" required>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label for="requiredSkills" class="form-label fw-semibold">Required Skills (Comma-separated)</label>
                        <input type="text" class="form-control" id="requiredSkills" name="requiredSkills" placeholder="e.g. Flutter, Firebase, Python, Computer Vision" required>
                        <div class="form-text small">Students will match their profiles against these skills.</div>
                    </div>

                    <div class="mb-4">
                        <label for="description" class="form-label fw-semibold">Project Description & Scope</label>
                        <textarea class="form-control" id="description" name="description" rows="5" placeholder="Detail the objectives of your project, expected time commitment, meeting schedule, and what you hope to achieve together." required></textarea>
                    </div>

                    <div class="d-flex justify-content-between align-items-center">
                        <button type="submit" class="btn btn-primary rounded-pill px-4 fw-bold shadow-sm">
                            <i class="bi bi-kanban-fill me-1"></i>Publish Project Listing
                        </button>
                        <a href="${pageContext.request.contextPath}/projects" class="btn btn-link text-secondary text-decoration-none">Cancel</a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>