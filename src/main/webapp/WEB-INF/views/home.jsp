<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="CampusConnect — University Skill, Mentorship and Collaboration Platform"/>
</jsp:include>
<jsp:include page="/WEB-INF/views/common/navbar.jsp"/>

<!-- Hero Banner -->
<section class="hero-banner text-center">
    <div class="container py-4">
        <span class="badge bg-white text-primary px-3 py-2 rounded-pill fw-bold mb-3 shadow-sm">
            <i class="bi bi-mortarboard-fill me-1"></i> University Exclusive Network
        </span>
        <h1 class="display-4 fw-bold mb-3">Connect. Mentor. Collaborate.</h1>
        <p class="lead mx-auto mb-4 text-light opacity-90" style="max-width: 750px;">
            A unified peer-to-peer ecosystem connecting students, seniors, and alumni. Find verified mentors, earn Skill Credits by teaching, collaborate on multidisciplinary student projects, and solve academic doubts together.
        </p>
        <div class="d-flex justify-content-center gap-3 flex-wrap">
            <c:choose>
                <c:when test="${not empty sessionScope.currentUser}">
                    <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-light btn-lg rounded-pill px-4 fw-bold shadow-sm">
                        <i class="bi bi-speedometer2 me-1"></i> Open Dashboard
                    </a>
                    <a href="${pageContext.request.contextPath}/mentors" class="btn btn-outline-light btn-lg rounded-pill px-4 fw-bold">
                        <i class="bi bi-search me-1"></i> Find a Mentor
                    </a>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/auth/register" class="btn btn-primary btn-lg rounded-pill px-4 fw-bold shadow-sm">
                        <i class="bi bi-person-plus-fill me-1"></i> Join University Platform
                    </a>
                    <a href="${pageContext.request.contextPath}/auth/login" class="btn btn-outline-light btn-lg rounded-pill px-4 fw-bold">
                        <i class="bi bi-box-arrow-in-right me-1"></i> University Sign In
                    </a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</section>

<!-- Live Platform Metrics -->
<section class="container mb-5">
    <div class="row g-3">
        <div class="col-6 col-md-3">
            <div class="stat-box shadow-sm">
                <div class="number">${not empty stats.totalUsers ? stats.totalUsers : 0}</div>
                <div class="label">University Members</div>
            </div>
        </div>
        <div class="col-6 col-md-3">
            <div class="stat-box shadow-sm">
                <div class="number">${not empty stats.activeMentors ? stats.activeMentors : 0}</div>
                <div class="label">Active Mentors</div>
            </div>
        </div>
        <div class="col-6 col-md-3">
            <div class="stat-box shadow-sm">
                <div class="number">${not empty stats.totalSessions ? stats.totalSessions : 0}</div>
                <div class="label">Mentorship Sessions</div>
            </div>
        </div>
        <div class="col-6 col-md-3">
            <div class="stat-box shadow-sm">
                <div class="number">${not empty stats.totalResources ? stats.totalResources : 0}</div>
                <div class="label">Shared Resources</div>
            </div>
        </div>
    </div>
</section>

<!-- Core Pillars -->
<section class="container py-4 mb-5">
    <div class="text-center mb-5">
        <h2 class="fw-bold">Designed for University Knowledge Exchange</h2>
        <p class="text-muted">Replacing informal, scattered group chats with structured, persistent academic collaboration.</p>
    </div>

    <div class="row g-4">
        <div class="col-md-6 col-lg-3">
            <div class="card card-cc h-100 p-4">
                <div class="fs-1 text-primary mb-3"><i class="bi bi-sliders"></i></div>
                <h5 class="fw-bold">Smart Mentor Matching</h5>
                <p class="text-muted small">
                    Weighted algorithm matching skills, availability, and peer ratings without scheduling conflicts.
                </p>
                <a href="${pageContext.request.contextPath}/mentors" class="mt-auto fw-bold text-decoration-none">Explore Mentors &rarr;</a>
            </div>
        </div>

        <div class="col-md-6 col-lg-3">
            <div class="card card-cc h-100 p-4">
                <div class="fs-1 text-success mb-3"><i class="bi bi-coin"></i></div>
                <h5 class="fw-bold">Skill Credit Ledger</h5>
                <p class="text-muted small">
                    Earn credits for mentoring, answering doubts, and sharing notes. Spend credits to book 1-on-1 sessions.
                </p>
                <a href="${pageContext.request.contextPath}/credits" class="mt-auto fw-bold text-decoration-none">How Credits Work &rarr;</a>
            </div>
        </div>

        <div class="col-md-6 col-lg-3">
            <div class="card card-cc h-100 p-4">
                <div class="fs-1 text-info mb-3"><i class="bi bi-kanban"></i></div>
                <h5 class="fw-bold">Project Marketplace</h5>
                <p class="text-muted small">
                    Form cross-department project teams for hackathons, research publications, and capstone coursework.
                </p>
                <a href="${pageContext.request.contextPath}/projects" class="mt-auto fw-bold text-decoration-none">Find Projects &rarr;</a>
            </div>
        </div>

        <div class="col-md-6 col-lg-3">
            <div class="card card-cc h-100 p-4">
                <div class="fs-1 text-warning mb-3"><i class="bi bi-patch-question"></i></div>
                <h5 class="fw-bold">Doubt Board & Library</h5>
                <p class="text-muted small">
                    Ask academic questions, review past exam solutions, interview roadmaps, and verified course notes.
                </p>
                <a href="${pageContext.request.contextPath}/doubts" class="mt-auto fw-bold text-decoration-none">Ask Questions &rarr;</a>
            </div>
        </div>
    </div>
</section>

<!-- Featured Mentors Section -->
<c:if test="${not empty featuredMentors}">
    <section class="bg-white py-5 border-top border-bottom mb-5">
        <div class="container">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h3 class="fw-bold mb-1">Featured Peer Mentors</h3>
                    <p class="text-muted small mb-0">Learn from top-rated seniors and alumni across engineering and sciences.</p>
                </div>
                <a href="${pageContext.request.contextPath}/mentors" class="btn btn-outline-primary btn-sm rounded-pill px-3">View All Mentors</a>
            </div>

            <div class="row g-4">
                <c:forEach var="item" items="${featuredMentors}">
                    <div class="col-md-4">
                        <div class="card card-cc h-100 p-4">
                            <div class="d-flex justify-content-between align-items-start mb-3">
                                <div>
                                    <h5 class="fw-bold mb-0">${item.mentor.fullName}</h5>
                                    <span class="badge badge-role-${item.mentor.role} small">${item.mentor.role}</span>
                                    <span class="small text-muted ms-1">${item.mentor.department}</span>
                                </div>
                                <div class="star-rating small">
                                    <i class="bi bi-star-fill"></i>
                                    <span class="fw-bold text-dark">${item.mentor.mentorRating}</span>
                                </div>
                            </div>
                            <p class="small text-muted mb-3">${item.mentor.bio}</p>
                            <div class="mb-3">
                                <div class="small fw-bold text-secondary mb-1">Skills Offered:</div>
                                <div class="d-flex flex-wrap gap-1">
                                    <c:forEach var="sk" items="${item.offeredSkills}">
                                        <span class="badge bg-light text-dark border small">${sk.skillName}</span>
                                    </c:forEach>
                                </div>
                            </div>
                            <div class="mt-auto d-flex gap-2">
                                <a href="${pageContext.request.contextPath}/profile?id=${item.mentor.id}" class="btn btn-outline-secondary btn-sm flex-fill rounded-pill">Profile</a>
                                <a href="${pageContext.request.contextPath}/sessions/book?mentorId=${item.mentor.id}" class="btn btn-primary btn-sm flex-fill rounded-pill">Book Session</a>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>
    </section>
</c:if>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>