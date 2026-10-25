<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Ask a Question — CampusConnect"/>
</jsp:include>
<jsp:include page="/WEB-INF/views/common/navbar.jsp"/>

<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4 pb-2 border-bottom">
        <div>
            <h2 class="fw-bold mb-1">Ask an Academic Question</h2>
            <div class="text-muted small">Post your question to the university community for peer answers</div>
        </div>
        <a href="${pageContext.request.contextPath}/doubts" class="btn btn-outline-secondary rounded-pill px-3">
            <i class="bi bi-arrow-left me-1"></i>Back to Doubts
        </a>
    </div>

    <jsp:include page="/WEB-INF/views/common/alerts.jsp"/>

    <div class="row justify-content-center">
        <div class="col-lg-8">
            <div class="card card-cc p-4 p-md-5">
                <form action="${pageContext.request.contextPath}/doubts/new" method="post">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                    <div class="mb-3">
                        <label for="title" class="form-label fw-semibold">Question Title / Topic Summary</label>
                        <input type="text" class="form-control" id="title" name="title" placeholder="e.g. How does ConcurrentHashMap achieve lock-free reads in Java 8?" required>
                        <div class="form-text small">Be specific and imagine you are asking another computer science student.</div>
                    </div>

                    <div class="row g-3 mb-3">
                        <div class="col-md-6">
                            <label for="category" class="form-label fw-semibold">Category</label>
                            <select class="form-select" id="category" name="category" required>
                                <option value="Computer Science Core">Computer Science Core</option>
                                <option value="Backend Development">Backend Development</option>
                                <option value="Databases">Databases</option>
                                <option value="Artificial Intelligence">Artificial Intelligence</option>
                                <option value="Data Science">Data Science</option>
                                <option value="General Academic">General Academic</option>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label for="skillId" class="form-label fw-semibold">Related Skill (Optional)</label>
                            <select class="form-select" id="skillId" name="skillId">
                                <option value="">None / General</option>
                                <c:forEach var="sk" items="${allSkills}">
                                    <option value="${sk.id}">${sk.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>

                    <div class="mb-4">
                        <label for="description" class="form-label fw-semibold">Detailed Description & Context</label>
                        <textarea class="form-control" id="description" name="description" rows="7" placeholder="Explain your doubt in detail. Mention code snippets, algorithm steps, or what you have tried so far." required></textarea>
                    </div>

                    <div class="d-flex justify-content-between align-items-center">
                        <button type="submit" class="btn btn-primary rounded-pill px-4 fw-bold shadow-sm">
                            <i class="bi bi-send-fill me-1"></i>Publish Question
                        </button>
                        <a href="${pageContext.request.contextPath}/doubts" class="btn btn-link text-secondary text-decoration-none">Cancel</a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>