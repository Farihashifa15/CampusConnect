<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Resource Library — CampusConnect"/>
</jsp:include>
<jsp:include page="/WEB-INF/views/common/navbar.jsp"/>

<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center flex-wrap gap-2 mb-4 pb-2 border-bottom">
        <div>
            <h2 class="fw-bold mb-1">Academic Resource Library</h2>
            <div class="text-muted small">Search and download verified lecture notes, interview roadmaps, and past exam papers</div>
        </div>
        <a href="${pageContext.request.contextPath}/resources/upload" class="btn btn-primary rounded-pill px-4 shadow-sm fw-bold">
            <i class="bi bi-cloud-arrow-up-fill me-1"></i>Upload Resource
        </a>
    </div>

    <jsp:include page="/WEB-INF/views/common/alerts.jsp"/>

    <!-- Search & Category Filters -->
    <div class="card card-cc p-4 mb-4">
        <form action="${pageContext.request.contextPath}/resources" method="get">
            <div class="row g-3">
                <div class="col-md-7">
                    <div class="input-group">
                        <span class="input-group-text"><i class="bi bi-search"></i></span>
                        <input type="text" class="form-control" name="q" value="${keyword}" placeholder="Search resources by title, subject, or tags (e.g. Concurrency, AWS, Midterm)...">
                    </div>
                </div>
                <div class="col-md-4">
                    <select class="form-select" name="category">
                        <option value="">All Resource Categories</option>
                        <c:forEach var="cat" items="${categories}">
                            <option value="${cat.displayName}" ${selectedCategory == cat.displayName ? 'selected' : ''}>${cat.displayName}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-1">
                    <button type="submit" class="btn btn-primary w-100 rounded-pill">Filter</button>
                </div>
            </div>
        </form>
    </div>

    <!-- Resources Grid -->
    <c:choose>
        <c:when test="${not empty resources}">
            <div class="row g-4">
                <c:forEach var="r" items="${resources}">
                    <div class="col-md-6 col-lg-4">
                        <div class="card card-cc h-100 p-4">
                            <div class="d-flex justify-content-between align-items-start mb-2">
                                <span class="badge bg-primary-subtle text-primary border border-primary-subtle small">${r.category.displayName}</span>
                                <span class="small text-muted"><i class="bi bi-download me-1"></i>${r.downloadCount}</span>
                            </div>

                            <h5 class="fw-bold mb-2">
                                <a href="${pageContext.request.contextPath}/resources/download?id=${r.id}" class="text-dark text-decoration-none">
                                    ${r.title}
                                </a>
                            </h5>

                            <p class="small text-muted mb-3" style="min-height: 40px;">
                                ${not empty r.description ? r.description : 'Academic learning materials contributed by university members.'}
                            </p>

                            <c:if test="${not empty r.tags}">
                                <div class="mb-3 d-flex flex-wrap gap-1">
                                    <c:forEach var="tag" items="${r.tags.split(',')}">
                                        <span class="badge bg-light text-secondary border small">#${tag.trim()}</span>
                                    </c:forEach>
                                </div>
                            </c:if>

                            <div class="mt-auto pt-3 border-top d-flex justify-content-between align-items-center">
                                <div class="small text-muted">
                                    <div>By <strong>${r.uploaderName}</strong></div>
                                    <div style="font-size: 0.75rem;"><fmt:formatNumber value="${r.fileSize / 1024}" maxFractionDigits="1"/> KB &bull; ${r.originalFilename}</div>
                                </div>
                                <a href="${pageContext.request.contextPath}/resources/download?id=${r.id}" class="btn btn-outline-primary btn-sm rounded-pill px-3">
                                    <i class="bi bi-download me-1"></i>Download
                                </a>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:when>
        <c:otherwise>
            <div class="card card-cc p-5 text-center my-4">
                <i class="bi bi-folder-x display-4 text-secondary mb-3"></i>
                <h4 class="fw-bold">No Resources Found</h4>
                <p class="text-muted">No academic materials match your search. Upload notes and earn Skill Credits!</p>
                <div>
                    <a href="${pageContext.request.contextPath}/resources/upload" class="btn btn-primary rounded-pill px-4">Upload First Resource</a>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>