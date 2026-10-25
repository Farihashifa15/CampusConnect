<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Upload Resource — CampusConnect"/>
</jsp:include>
<jsp:include page="/WEB-INF/views/common/navbar.jsp"/>

<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4 pb-2 border-bottom">
        <div>
            <h2 class="fw-bold mb-1">Upload Academic Resource</h2>
            <div class="text-muted small">Share educational materials with university peers and earn Skill Credits</div>
        </div>
        <a href="${pageContext.request.contextPath}/resources" class="btn btn-outline-secondary rounded-pill px-3">
            <i class="bi bi-arrow-left me-1"></i>Back to Library
        </a>
    </div>

    <jsp:include page="/WEB-INF/views/common/alerts.jsp"/>

    <div class="row justify-content-center">
        <div class="col-lg-8">
            <div class="card card-cc p-4 p-md-5">
                <div class="alert alert-success d-flex align-items-center py-2 mb-4">
                    <i class="bi bi-coin text-warning fs-4 me-2"></i>
                    <div class="small">
                        <strong>Earn +5 Skill Credits!</strong> Valid educational resource contributions earn you credits to book mentorship sessions.
                    </div>
                </div>

                <form action="${pageContext.request.contextPath}/resources/upload" method="post" enctype="multipart/form-data">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                    <div class="mb-3">
                        <label for="title" class="form-label fw-semibold">Resource Title</label>
                        <input type="text" class="form-control" id="title" name="title" placeholder="e.g. Distributed Database Architecture & Sharding Guide" required>
                    </div>

                    <div class="row g-3 mb-3">
                        <div class="col-md-6">
                            <label for="category" class="form-label fw-semibold">Resource Category</label>
                            <select class="form-select" id="category" name="category" required>
                                <c:forEach var="cat" items="${categories}">
                                    <option value="${cat.displayName}">${cat.displayName}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label for="tags" class="form-label fw-semibold">Tags (Comma-separated)</label>
                            <input type="text" class="form-control" id="tags" name="tags" placeholder="e.g. Java, Databases, Sharding, Midterm">
                        </div>
                    </div>

                    <div class="mb-3">
                        <label for="description" class="form-label fw-semibold">Description</label>
                        <textarea class="form-control" id="description" name="description" rows="3" placeholder="Provide a summary of what topics or chapters are covered in this document."></textarea>
                    </div>

                    <div class="mb-4">
                        <label for="file" class="form-label fw-semibold">Select Document / File</label>
                        <input class="form-control" type="file" id="file" name="file" required>
                        <div class="form-text small">
                            Permitted file formats: <strong>.pdf, .doc, .docx, .txt, .ppt, .pptx, .zip, .png, .jpg</strong> (Max size: 25 MB).
                        </div>
                    </div>

                    <div class="d-flex justify-content-between align-items-center">
                        <button type="submit" class="btn btn-primary rounded-pill px-4 fw-bold shadow-sm">
                            <i class="bi bi-cloud-arrow-up-fill me-1"></i>Publish Resource
                        </button>
                        <a href="${pageContext.request.contextPath}/resources" class="btn btn-link text-secondary text-decoration-none">Cancel</a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>