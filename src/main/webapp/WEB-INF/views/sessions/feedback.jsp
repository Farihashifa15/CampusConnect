<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Rate & Endorse Mentor — CampusConnect"/>
</jsp:include>
<jsp:include page="/WEB-INF/views/common/navbar.jsp"/>

<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4 pb-2 border-bottom">
        <div>
            <h2 class="fw-bold mb-1">Session Feedback & Peer Endorsement</h2>
            <div class="text-muted small">Submit your rating and verify your mentor's demonstrated skill</div>
        </div>
        <a href="${pageContext.request.contextPath}/sessions/view?id=${targetSession.id}" class="btn btn-outline-secondary rounded-pill px-3">
            <i class="bi bi-arrow-left me-1"></i>Back to Session
        </a>
    </div>

    <jsp:include page="/WEB-INF/views/common/alerts.jsp"/>

    <div class="row justify-content-center">
        <div class="col-lg-7">
            <div class="card card-cc p-4 p-md-5">
                <div class="text-center mb-4">
                    <div class="fs-1 text-warning mb-2"><i class="bi bi-stars"></i></div>
                    <h4 class="fw-bold">How was your session with ${targetSession.mentorName}?</h4>
                    <p class="text-muted small">Session on <strong>${targetSession.skillName}</strong> on ${targetSession.sessionDate}</p>
                </div>

                <form action="${pageContext.request.contextPath}/feedback/submit" method="post">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                    <input type="hidden" name="sessionId" value="${targetSession.id}">

                    <!-- 1. Star Rating -->
                    <div class="mb-4 text-center">
                        <label class="form-label fw-bold d-block mb-2">Overall Rating (1 to 5 Stars)</label>
                        <div class="d-flex justify-content-center gap-3 fs-3 star-rating-select">
                            <c:forEach begin="1" end="5" var="val">
                                <label class="star-rating-label text-warning cursor-pointer" style="cursor: pointer;">
                                    <input type="radio" name="rating" value="${val}" ${val == 5 ? 'checked' : ''} class="d-none">
                                    <i class="bi bi-star-fill"></i>
                                </label>
                            </c:forEach>
                        </div>
                    </div>

                    <!-- 2. Written Feedback -->
                    <div class="mb-4">
                        <label for="feedbackText" class="form-label fw-semibold">Written Review / Feedback</label>
                        <textarea class="form-control" id="feedbackText" name="feedbackText" rows="3" placeholder="Share how helpful the mentor was, the topics covered, and your learning experience." required></textarea>
                    </div>

                    <!-- 3. Skill Endorsement Checkbox -->
                    <div class="card p-3 bg-light border mb-4">
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" id="endorseSkill" name="endorseSkill" value="true" checked>
                            <label class="form-check-label fw-bold" for="endorseSkill">
                                <i class="bi bi-patch-check-fill text-primary me-1"></i> Endorse mentor for "${targetSession.skillName}"
                            </label>
                        </div>
                        <div class="small text-muted mt-1 ps-4">
                            This will appear on the mentor's public university profile as a verified skill endorsement.
                        </div>
                        <div class="mt-2 ps-4">
                            <input type="text" class="form-control form-control-sm" name="endorsementComment" placeholder="Optional endorsement note (e.g. Demonstrated deep mastery of algorithms)">
                        </div>
                    </div>

                    <button type="submit" class="btn btn-primary w-100 py-2 rounded-pill fw-bold shadow-sm">
                        Submit Feedback & Endorsement
                    </button>
                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>