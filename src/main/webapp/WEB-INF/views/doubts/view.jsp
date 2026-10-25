<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="${question.title} — CampusConnect Doubts"/>
</jsp:include>
<jsp:include page="/WEB-INF/views/common/navbar.jsp"/>

<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4 pb-2 border-bottom">
        <div>
            <div class="d-flex align-items-center gap-2 mb-1">
                <span class="badge bg-light text-dark border">${question.category}</span>
                <c:if test="${not empty question.skillName}">
                    <span class="badge bg-primary-subtle text-primary border border-primary-subtle">${question.skillName}</span>
                </c:if>
                <c:choose>
                    <c:when test="${question.status == 'RESOLVED'}">
                        <span class="badge bg-success-subtle text-success border border-success-subtle">
                            <i class="bi bi-check-circle-fill me-1"></i>Resolved
                        </span>
                    </c:when>
                    <c:otherwise>
                        <span class="badge bg-warning-subtle text-warning-emphasis border border-warning-subtle">Open</span>
                    </c:otherwise>
                </c:choose>
            </div>
            <h2 class="fw-bold mb-0 text-dark">${question.title}</h2>
        </div>
        <a href="${pageContext.request.contextPath}/doubts" class="btn btn-outline-secondary rounded-pill px-3">
            <i class="bi bi-arrow-left me-1"></i>Back to Doubts
        </a>
    </div>

    <jsp:include page="/WEB-INF/views/common/alerts.jsp"/>

    <div class="row g-4">
        <!-- Main Question & Answers Column -->
        <div class="col-lg-8">
            <!-- Question Box -->
            <div class="card card-cc p-4 mb-4">
                <div class="text-dark mb-4" style="line-height: 1.7; white-space: pre-wrap;">${question.description}</div>

                <div class="d-flex justify-content-between align-items-center pt-3 border-top text-muted small">
                    <div class="d-flex align-items-center gap-2">
                        <div class="bg-primary text-white rounded-circle d-flex align-items-center justify-content-center" style="width: 32px; height: 32px; font-weight: bold;">
                            ${question.authorName.substring(0, 1)}
                        </div>
                        <div>
                            <a href="${pageContext.request.contextPath}/profile?id=${question.authorId}" class="fw-bold text-dark text-decoration-none">
                                ${question.authorName}
                            </a>
                            <span class="badge badge-role-${question.authorRole} ms-1" style="font-size: 0.7rem;">${question.authorRole}</span>
                            <span class="text-secondary ms-1">&bull; ${question.createdAt}</span>
                        </div>
                    </div>

                    <!-- Report Button -->
                    <button type="button" class="btn btn-outline-danger btn-sm py-0 px-2 rounded-pill" data-bs-toggle="modal" data-bs-target="#reportModal" data-content-id="${question.id}" data-content-type="QUESTION">
                        <i class="bi bi-flag"></i> Report
                    </button>
                </div>
            </div>

            <!-- Answers Count Header -->
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h4 class="fw-bold mb-0"><i class="bi bi-chat-left-dots text-primary me-2"></i>${answers.size()} Answers</h4>
            </div>

            <!-- Answers List -->
            <c:choose>
                <c:when test="${not empty answers}">
                    <div class="d-flex flex-column gap-3 mb-4">
                        <c:forEach var="a" items="${answers}">
                            <div class="card card-cc p-4 ${a.accepted ? 'border-success bg-success-subtle bg-opacity-10' : ''}">
                                <c:if test="${a.accepted}">
                                    <div class="badge bg-success text-white mb-2 d-inline-flex align-items-center gap-1 align-self-start px-3 py-2 rounded-pill">
                                        <i class="bi bi-check2-circle"></i> Accepted Answer (+15 Skill Credits Awarded)
                                    </div>
                                </c:if>

                                <div class="text-dark mb-3" style="line-height: 1.7; white-space: pre-wrap;">${a.answerText}</div>

                                <div class="d-flex justify-content-between align-items-center pt-2 border-top text-muted small">
                                    <div class="d-flex align-items-center gap-2">
                                        <div class="bg-secondary text-white rounded-circle d-flex align-items-center justify-content-center" style="width: 28px; height: 28px; font-weight: bold; font-size: 0.8rem;">
                                            ${a.authorName.substring(0, 1)}
                                        </div>
                                        <div>
                                            <a href="${pageContext.request.contextPath}/profile?id=${a.authorId}" class="fw-bold text-dark text-decoration-none">
                                                ${a.authorName}
                                            </a>
                                            <span class="badge badge-role-${a.authorRole} ms-1" style="font-size: 0.65rem;">${a.authorRole}</span>
                                            <span class="text-secondary ms-1">&bull; ${a.createdAt}</span>
                                        </div>
                                    </div>

                                    <div class="d-flex gap-2 align-items-center">
                                        <!-- Question author can accept answer -->
                                        <c:if test="${sessionScope.currentUser.id == question.authorId && !a.accepted}">
                                            <form action="${pageContext.request.contextPath}/doubts/accept" method="post" class="d-inline m-0">
                                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                                <input type="hidden" name="questionId" value="${question.id}">
                                                <input type="hidden" name="answerId" value="${a.id}">
                                                <button type="submit" class="btn btn-outline-success btn-sm rounded-pill px-3" data-confirm="Accept this answer? This will award 15 Skill Credits to the author.">
                                                    <i class="bi bi-check-lg me-1"></i>Accept Answer
                                                </button>
                                            </form>
                                        </c:if>

                                        <button type="button" class="btn btn-link text-secondary btn-sm p-0 text-decoration-none" data-bs-toggle="modal" data-bs-target="#reportModal" data-content-id="${a.id}" data-content-type="ANSWER">
                                            <i class="bi bi-flag"></i>
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="card card-cc p-4 text-center text-muted mb-4">
                        <i class="bi bi-chat-left-dots fs-1 text-secondary mb-2 d-block"></i>
                        <div>No answers yet. Share your knowledge and help a peer solve this doubt!</div>
                    </div>
                </c:otherwise>
            </c:choose>

            <!-- Submit Answer Form -->
            <div class="card card-cc p-4">
                <h5 class="fw-bold mb-3"><i class="bi bi-pencil-square text-primary me-2"></i>Post Your Solution</h5>
                <c:choose>
                    <c:when test="${not empty sessionScope.currentUser}">
                        <form action="${pageContext.request.contextPath}/doubts/answer" method="post">
                            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                            <input type="hidden" name="questionId" value="${question.id}">

                            <div class="mb-3">
                                <textarea class="form-control" name="answerText" rows="5" placeholder="Write a clear, structured explanation with code examples or steps..." required></textarea>
                            </div>
                            <div class="d-flex justify-content-between align-items-center">
                                <button type="submit" class="btn btn-primary rounded-pill px-4 fw-bold shadow-sm">
                                    <i class="bi bi-send-fill me-1"></i>Submit Answer
                                </button>
                                <div class="small text-muted"><i class="bi bi-coin text-warning me-1"></i>Earn +15 Skill Credits if your answer is accepted</div>
                            </div>
                        </form>
                    </c:when>
                    <c:otherwise>
                        <div class="alert alert-info py-2 small mb-0">
                            Please <a href="${pageContext.request.contextPath}/auth/login">sign in with your university account</a> to post an answer.
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- Right Sidebar Info -->
        <div class="col-lg-4">
            <div class="card card-cc p-4 mb-4">
                <h6 class="fw-bold text-secondary text-uppercase small mb-3">Academic Guidelines</h6>
                <ul class="small text-muted ps-3 mb-0">
                    <li class="mb-2">Provide well-explained answers rather than just short one-word replies.</li>
                    <li class="mb-2">Cite course textbooks, official documentation, or verified papers.</li>
                    <li class="mb-2">Accepted answers award +15 Skill Credits to the contributor.</li>
                    <li>Be courteous and supportive of junior students.</li>
                </ul>
            </div>
        </div>
    </div>
</div>

<!-- Moderation Report Modal -->
<div class="modal fade" id="reportModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <form action="${pageContext.request.contextPath}/doubts/report" method="post">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                <input type="hidden" name="type" id="reportContentType" value="QUESTION">
                <input type="hidden" name="contentId" id="reportContentId" value="${question.id}">

                <div class="modal-header">
                    <h5 class="modal-title fw-bold"><i class="bi bi-flag-fill text-danger me-2"></i>Report Inappropriate Content</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Reason for Report</label>
                        <textarea class="form-control" name="reason" rows="3" placeholder="Explain why this content violates academic integrity, community standards, or platform rules." required></textarea>
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