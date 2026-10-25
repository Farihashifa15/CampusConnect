<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Project Collaboration Marketplace — CampusConnect"/>
</jsp:include>
<jsp:include page="/WEB-INF/views/common/navbar.jsp"/>

<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center flex-wrap gap-2 mb-4 pb-2 border-bottom">
        <div>
            <h2 class="fw-bold mb-1">Student Project Collaboration Marketplace</h2>
            <div class="text-muted small">Join multidisciplinary teams for university capstones, research, and hackathons</div>
        </div>
        <a href="${pageContext.request.contextPath}/projects/new" class="btn btn-primary rounded-pill px-4 shadow-sm fw-bold">
            <i class="bi bi-plus-circle-fill me-1"></i>Post Project Listing
        </a>
    </div>

    <jsp:include page="/WEB-INF/views/common/alerts.jsp"/>

    <!-- Search & Filter Bar -->
    <div class="card card-cc p-4 mb-4">
        <form action="${pageContext.request.contextPath}/projects" method="get">
            <div class="row g-3">
                <div class="col-md-8">
                    <div class="input-group">
                        <span class="input-group-text"><i class="bi bi-search"></i></span>
                        <input type="text" class="form-control" name="q" value="${keyword}" placeholder="Search projects by title, required skills, or tech stack...">
                    </div>
                </div>
                <div class="col-md-3">
                    <select class="form-select" name="category">
                        <option value="">All Project Categories</option>
                        <option value="Mobile & Web" ${selectedCategory == 'Mobile & Web' ? 'selected' : ''}>Mobile & Web</option>
                        <option value="Artificial Intelligence" ${selectedCategory == 'Artificial Intelligence' ? 'selected' : ''}>Artificial Intelligence</option>
                        <option value="Productivity Tools" ${selectedCategory == 'Productivity Tools' ? 'selected' : ''}>Productivity Tools</option>
                        <option value="Robotics & Embedded Systems" ${selectedCategory == 'Robotics & Embedded Systems' ? 'selected' : ''}>Robotics & Embedded Systems</option>
                        <option value="General Engineering" ${selectedCategory == 'General Engineering' ? 'selected' : ''}>General Engineering</option>
                    </select>
                </div>
                <div class="col-md-1">
                    <button type="submit" class="btn btn-primary w-100 rounded-pill">Filter</button>
                </div>
            </div>
        </form>
    </div>

    <!-- Projects Grid -->
    <c:choose>
        <c:when test="${not empty projects}">
            <div class="row g-4">
                <c:forEach var="p" items="${projects}">
                    <div class="col-md-6 col-lg-4">
                        <div class="card card-cc h-100 p-4">
                            <div class="d-flex justify-content-between align-items-start mb-2">
                                <span class="badge bg-light text-dark border small">${p.category}</span>
                                <span class="badge badge-status-${p.status}">${p.status}</span>
                            </div>

                            <h5 class="fw-bold mb-2">
                                <a href="${pageContext.request.contextPath}/projects/view?id=${p.id}" class="text-dark text-decoration-none">
                                    ${p.title}
                                </a>
                            </h5>

                            <p class="small text-muted mb-3" style="min-height: 40px;">
                                ${p.description.length() > 140 ? p.description.substring(0, 140).concat('...') : p.description}
                            </p>

                            <div class="mb-3">
                                <div class="small fw-bold text-secondary mb-1">Required Skills:</div>
                                <div class="d-flex flex-wrap gap-1">
                                    <c:forEach var="sk" items="${p.requiredSkills.split(',')}">
                                        <span class="badge bg-primary-subtle text-primary border border-primary-subtle small">${sk.trim()}</span>
                                    </c:forEach>
                                </div>
                            </div>

                            <div class="mt-auto pt-3 border-top d-flex justify-content-between align-items-center">
                                <div class="small text-muted">
                                    <div>Lead: <strong>${p.creatorName}</strong></div>
                                    <div class="fw-semibold ${p.hasOpenPositions() ? 'text-success' : 'text-danger'}">
                                        <i class="bi bi-people-fill me-1"></i>${p.filledPositions} / ${p.availablePositions} filled
                                    </div>
                                </div>
                                <a href="${pageContext.request.contextPath}/projects/view?id=${p.id}" class="btn btn-outline-primary btn-sm rounded-pill px-3">
                                    View Team
                                </a>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:when>
        <c:otherwise>
            <div class="card card-cc p-5 text-center my-4">
                <i class="bi bi-kanban display-4 text-secondary mb-3"></i>
                <h4 class="fw-bold">No Projects Found</h4>
                <p class="text-muted">No student project listings matched your search criteria.</p>
                <div>
                    <a href="${pageContext.request.contextPath}/projects/new" class="btn btn-primary rounded-pill px-4">Create Project Listing</a>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>