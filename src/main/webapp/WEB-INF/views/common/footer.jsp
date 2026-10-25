<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<footer>
    <div class="container">
        <div class="row g-4 mb-4">
            <div class="col-md-5">
                <h5 class="text-white fw-bold d-flex align-items-center gap-2">
                    <i class="bi bi-mortarboard-fill text-info"></i> CampusConnect
                </h5>
                <p class="small text-secondary">
                    A university-exclusive platform empowering students, seniors, and alumni through peer mentorship, collaborative student teams, skill exchange, and academic knowledge preservation.
                </p>
            </div>
            <div class="col-md-3">
                <h6 class="text-white fw-bold mb-3">Quick Navigation</h6>
                <ul class="list-unstyled small">
                    <li class="mb-2"><a href="${pageContext.request.contextPath}/mentors">Find Mentors</a></li>
                    <li class="mb-2"><a href="${pageContext.request.contextPath}/doubts">Doubt Board</a></li>
                    <li class="mb-2"><a href="${pageContext.request.contextPath}/resources">Resource Library</a></li>
                    <li class="mb-2"><a href="${pageContext.request.contextPath}/projects">Student Projects</a></li>
                </ul>
            </div>
            <div class="col-md-4">
                <h6 class="text-white fw-bold mb-3">University Ecosystem</h6>
                <div class="small text-secondary mb-2">
                    <i class="bi bi-shield-check text-success me-1"></i> Authorized University Accounts Only
                </div>
                <div class="small text-secondary mb-2">
                    <i class="bi bi-coin text-warning me-1"></i> Transaction-based Skill Credit Economy
                </div>
                <div class="small text-secondary">
                    <i class="bi bi-clock-history text-info me-1"></i> Conflict-Free Smart Booking
                </div>
            </div>
        </div>
        <hr class="border-secondary opacity-25">
        <div class="d-flex flex-wrap justify-content-between align-items-center small text-secondary">
            <div>&copy; 2026 CampusConnect Platform. Built for University Academic Excellence.</div>
            <div>All rights reserved.</div>
        </div>
    </div>
</footer>

<!-- Bootstrap 5 Bundle JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<!-- Custom JS -->
<script src="${pageContext.request.contextPath}/assets/js/app.js"></script>
</body>
</html>