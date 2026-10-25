<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="Skill Credit Ledger — CampusConnect"/>
</jsp:include>
<jsp:include page="/WEB-INF/views/common/navbar.jsp"/>

<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4 pb-2 border-bottom">
        <div>
            <h2 class="fw-bold mb-1">Skill Credit Economy & Ledger</h2>
            <div class="text-muted small">Transactional record of credits earned by teaching and spent on peer mentorship</div>
        </div>
        <a href="${pageContext.request.contextPath}/mentors" class="btn btn-outline-primary rounded-pill px-3">
            <i class="bi bi-calendar-plus me-1"></i>Book a Mentor
        </a>
    </div>

    <jsp:include page="/WEB-INF/views/common/alerts.jsp"/>

    <!-- Balance Summary Hero Cards -->
    <div class="row g-3 mb-4">
        <div class="col-md-4">
            <div class="card card-cc p-4 bg-primary text-white">
                <div class="small text-uppercase opacity-75 fw-semibold mb-1">Available Credit Balance</div>
                <div class="display-5 fw-bold mb-0">${balance}</div>
                <div class="small mt-2 opacity-90"><i class="bi bi-shield-check me-1"></i>Verified & backed by transaction ledger</div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card card-cc p-4">
                <div class="small text-uppercase text-muted fw-semibold mb-1">Total Credits Earned</div>
                <div class="display-5 fw-bold text-success mb-0">+${totalEarned}</div>
                <div class="small text-muted mt-2">Earned through mentoring, notes & doubts</div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card card-cc p-4">
                <div class="small text-uppercase text-muted fw-semibold mb-1">Total Credits Spent</div>
                <div class="display-5 fw-bold text-danger mb-0">-${totalSpent}</div>
                <div class="small text-muted mt-2">Redeemed for 1-on-1 peer learning sessions</div>
            </div>
        </div>
    </div>

    <!-- Credit Policy Rules -->
    <div class="card card-cc p-4 mb-4 bg-light border">
        <h6 class="fw-bold text-dark mb-3"><i class="bi bi-info-circle text-primary me-2"></i>CampusConnect University Credit Policy</h6>
        <div class="row g-3 small">
            <div class="col-md-3">
                <div class="p-2 bg-white rounded border">
                    <div class="text-muted">Session Booking:</div>
                    <div class="fw-bold text-danger">-${sessionCost} Credits</div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="p-2 bg-white rounded border">
                    <div class="text-muted">Session Completed:</div>
                    <div class="fw-bold text-success">+${sessionReward} Credits</div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="p-2 bg-white rounded border">
                    <div class="text-muted">Resource Upload:</div>
                    <div class="fw-bold text-success">+${resourceReward} Credits</div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="p-2 bg-white rounded border">
                    <div class="text-muted">Accepted Answer:</div>
                    <div class="fw-bold text-success">+${answerReward} Credits</div>
                </div>
            </div>
        </div>
    </div>

    <!-- Chronological Ledger History -->
    <div class="card card-cc p-4">
        <h5 class="fw-bold mb-3"><i class="bi bi-journal-text text-primary me-2"></i>Skill Credit Transaction History</h5>
        <c:choose>
            <c:when test="${not empty history}">
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0">
                        <thead class="table-light">
                            <tr>
                                <th>Transaction Type</th>
                                <th>Reason & Details</th>
                                <th>Amount</th>
                                <th>Related Activity</th>
                                <th>Timestamp</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="tx" items="${history}">
                                <tr>
                                    <td>
                                        <span class="badge ${tx.amount > 0 ? 'bg-success-subtle text-success border border-success-subtle' : 'bg-danger-subtle text-danger border border-danger-subtle'}">
                                            ${tx.transactionType}
                                        </span>
                                    </td>
                                    <td class="text-dark fw-semibold">${tx.reason}</td>
                                    <td class="fw-bold ${tx.amount > 0 ? 'text-success' : 'text-danger'}">
                                        ${tx.amount > 0 ? '+' : ''}${tx.amount}
                                    </td>
                                    <td>
                                        <c:if test="${not empty tx.relatedEntityType}">
                                            <span class="badge bg-light text-secondary border small">${tx.relatedEntityType} #${tx.relatedEntityId}</span>
                                        </c:if>
                                    </td>
                                    <td class="text-muted small">${tx.createdAt}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:when>
            <c:otherwise>
                <div class="text-center py-4 text-muted">
                    <i class="bi bi-journal-x display-4 text-secondary mb-2 d-block"></i>
                    <div>No ledger transactions recorded yet.</div>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>