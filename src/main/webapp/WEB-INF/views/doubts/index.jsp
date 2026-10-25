<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Doubt Board — CampusConnect"/>
</jsp:include>
<jsp:include page="/WEB-INF/views/common/navbar.jsp"/>

<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center flex-wrap gap-2 mb-4 pb-2 border-bottom">
        <div>
            <h2 class="fw-bold mb-1">University Doubt Board</h2>
            <div class="text-muted small">Ask academic questions, help peers, and earn Skill Credits for accepted solutions</div>
        </div>
        <a href="${pageContext.request.contextPath}/doubts/new" class="btn btn-primary rounded-pill px-4 shadow-sm fw-bold">
            <i class="bi bi-patch-question-fill me-1"></i>Ask a Question
        </a>
    </div>

    <jsp:include page="/WEB-INF/views/common/alerts.jsp"/>

    <!-- Search & Filter Bar -->
    <div class="card card-cc p-4 mb-4">
        <form action="${pageContext.request.contextPath}/doubts" method="get">
            <div class="row g-3">
                <div class="col-md-8">
                    <div class="input-group">
                        <span class="input-group-text"><i class="bi bi-search"></i></span>
                        <input type="text" class="form-control" name="q" value="${keyword}" placeholder="Search questions by keyword, topic, or concept...">
                    </div>
                </div>
                <div class="col-md-3">
                    <select class="form-select" name="category">
                        <option value="">All Categories</option>
                        <option value="Computer Science Core" ${selectedCategory == 'Computer Science Core' ? 'selected' : ''}>Computer Science Core</option>
                        <option value="Backend Development" ${selectedCategory == 'Backend Development' ? 'selected' : ''}>Backend Development</option>
                        <option value="Databases" ${selectedCategory == 'Databases' ? 'selected' : ''}>Databases</option>
                        <option value="Artificial Intelligence" ${selectedCategory == 'Artificial Intelligence' ? 'selected' : ''}>Artificial Intelligence</option>
                        <option value="Data Science" ${selectedCategory == 'Data Science' ? 'selected' : ''}>Data Science</option>
                        <option value="General Academic" ${selectedCategory == 'General Academic' ? 'selected' : ''}>General Academic</option>
                    </select>
                </div>
                <div class="col-md-1">
                    <button type="submit" class="btn btn-primary w-100 rounded-pill">Filter</button>
                </div>
            </div>
        </form>
    </div>

    <!-- Questions Feed -->
    <c:choose>
        <c:when test="${not empty questions}">
            <div class="d-flex flex-column gap-3">
                <c:forEach var="q" items="${questions}">
                    <div class="card card-cc p-4">
                        <div class="d-flex justify-content-between align-items-start mb-2">
                            <div>
                                <span class="badge bg-light text-dark border small me-1">${q.category}</span>
                                <c:if test="${not empty q.skillName}">
                                    <span class="badge bg-primary-subtle text-primary border border-primary-subtle small">${q.skillName}</span>
                                </c:if>
                                <c:choose>
                                    <c:when test="${q.status == 'RESOLVED'}">
                                        <span class="badge bg-success-subtle text-success border border-success-subtle small ms-1">
                                            <i class="bi bi-check-circle-fill me-1"></i>Resolved
                                        </span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-warning-subtle text-warning-emphasis border border-warning-subtle small ms-1">Open</span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <span class="badge bg-light text-secondary border px-3 py-2 rounded-pill small">
                                <i class="bi bi-chat-left-text me-1"></i>${q.answerCount} Answers
                            </span>
                        </div>

                        <h5 class="fw-bold mb-2">
                            <a href="${pageContext.request.contextPath}/doubts/view?id=${q.id}" class="text-dark text-decoration-none">
                                ${q.title}
                            </a>
                        </h5>

                        <p class="text-muted small mb-3">
                            ${q.description.length() > 200 ? q.description.substring(0, 200).concat('...') : q.description}
                        </p>

                        <div class="d-flex justify-content-between align-items-center text-muted small pt-2 border-top">
                            <div>Asked by <strong class="text-dark">${q.authorName}</strong> (${q.authorRole}) &bull; ${q.createdAt}</div>
                            <a href="${pageContext.request.contextPath}/doubts/view?id=${q.id}" class="btn btn-outline-primary btn-sm rounded-pill px-3">
                                View & Answer &rarr;
                            </a>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:when>
        <c:otherwise>
            <div class="card card-cc p-5 text-center my-4">
                <i class="bi bi-question-diamond display-4 text-secondary mb-3"></i>
                <h4 class="fw-bold">No Questions Found</h4>
                <p class="text-muted">No doubts match your search query. Be the first to ask!</p>
                <div>
                    <a href="${pageContext.request.contextPath}/doubts/new" class="btn btn-primary rounded-pill px-4">Ask a Question</a>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>