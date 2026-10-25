<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="pageTitle" value="User Governance — CampusConnect Admin"/>
</jsp:include>
<jsp:include page="/WEB-INF/views/common/navbar.jsp"/>

<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4 pb-2 border-bottom">
        <div>
            <h2 class="fw-bold mb-1">User Management & Role Governance</h2>
            <div class="text-muted small">Search users, review profiles, manage active status, and audit credit balances</div>
        </div>
        <a href="${pageContext.request.contextPath}/admin" class="btn btn-outline-secondary rounded-pill px-3">
            <i class="bi bi-arrow-left me-1"></i>Admin Dashboard
        </a>
    </div>

    <jsp:include page="/WEB-INF/views/common/alerts.jsp"/>

    <!-- Search & Filter Controls -->
    <div class="card card-cc p-4 mb-4">
        <form action="${pageContext.request.contextPath}/admin/users" method="get">
            <div class="row g-3">
                <div class="col-md-7">
                    <div class="input-group">
                        <span class="input-group-text"><i class="bi bi-search"></i></span>
                        <input type="text" class="form-control" name="search" value="${search}" placeholder="Search by full name, email, or register number...">
                    </div>
                </div>
                <div class="col-md-4">
                    <select class="form-select" name="role">
                        <option value="ALL">All Roles</option>
                        <option value="STUDENT" ${selectedRole == 'STUDENT' ? 'selected' : ''}>Student</option>
                        <option value="SENIOR" ${selectedRole == 'SENIOR' ? 'selected' : ''}>Senior (Mentor)</option>
                        <option value="ALUMNI" ${selectedRole == 'ALUMNI' ? 'selected' : ''}>Alumni</option>
                        <option value="ADMIN" ${selectedRole == 'ADMIN' ? 'selected' : ''}>Administrator</option>
                    </select>
                </div>
                <div class="col-md-1">
                    <button type="submit" class="btn btn-primary w-100 rounded-pill">Filter</button>
                </div>
            </div>
        </form>
    </div>

    <!-- Users Table -->
    <div class="card card-cc p-4">
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-light">
                    <tr>
                        <th>ID</th>
                        <th>User</th>
                        <th>Department & Year</th>
                        <th>Role</th>
                        <th>Credits</th>
                        <th>Status</th>
                        <th class="text-end">Administrative Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="u" items="${users}">
                        <tr>
                            <td>#${u.id}</td>
                            <td>
                                <div class="fw-bold text-dark">${u.fullName}</div>
                                <div class="small text-muted">${u.email} &bull; ${u.registerNumber}</div>
                            </td>
                            <td>
                                <div>${u.department}</div>
                                <div class="small text-muted">Year ${u.academicYear}</div>
                            </td>
                            <td><span class="badge badge-role-${u.role}">${u.role}</span></td>
                            <td><span class="credit-badge small">${u.creditBalance}</span></td>
                            <td>
                                <span class="badge ${u.status == 'ACTIVE' ? 'bg-success' : 'bg-danger'}">
                                    ${u.status}
                                </span>
                            </td>
                            <td class="text-end">
                                <div class="btn-group btn-group-sm">
                                    <a href="${pageContext.request.contextPath}/profile?id=${u.id}" class="btn btn-outline-secondary" title="View Profile">
                                        <i class="bi bi-eye"></i>
                                    </a>

                                    <!-- Status Toggle Form -->
                                    <c:choose>
                                        <c:when test="${u.status == 'ACTIVE'}">
                                            <form action="${pageContext.request.contextPath}/admin/user/status" method="post" class="d-inline m-0">
                                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                                <input type="hidden" name="userId" value="${u.id}">
                                                <input type="hidden" name="status" value="SUSPENDED">
                                                <input type="hidden" name="reason" value="Suspended by administrator via control panel">
                                                <button type="submit" class="btn btn-outline-warning" title="Suspend Account" data-confirm="Suspend user ${u.fullName}? They will be blocked from logging in.">
                                                    <i class="bi bi-pause-circle"></i>
                                                </button>
                                            </form>
                                        </c:when>
                                        <c:otherwise>
                                            <form action="${pageContext.request.contextPath}/admin/user/status" method="post" class="d-inline m-0">
                                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                                <input type="hidden" name="userId" value="${u.id}">
                                                <input type="hidden" name="status" value="ACTIVE">
                                                <input type="hidden" name="reason" value="Restored by administrator">
                                                <button type="submit" class="btn btn-outline-success" title="Restore Account">
                                                    <i class="bi bi-play-circle"></i>
                                                </button>
                                            </form>
                                        </c:otherwise>
                                    </c:choose>

                                    <!-- Role Change Modal Trigger -->
                                    <button type="button" class="btn btn-outline-primary" data-bs-toggle="modal" data-bs-target="#roleModal-${u.id}" title="Change Role">
                                        <i class="bi bi-person-gear"></i>
                                    </button>

                                    <!-- Credit Adjustment Modal Trigger -->
                                    <button type="button" class="btn btn-outline-secondary" data-bs-toggle="modal" data-bs-target="#creditModal-${u.id}" title="Adjust Credits">
                                        <i class="bi bi-coin"></i>
                                    </button>
                                </div>

                                <!-- Role Modal for User -->
                                <div class="modal fade text-start" id="roleModal-${u.id}" tabindex="-1" aria-hidden="true">
                                    <div class="modal-dialog">
                                        <div class="modal-content">
                                            <form action="${pageContext.request.contextPath}/admin/user/role" method="post">
                                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                                <input type="hidden" name="userId" value="${u.id}">

                                                <div class="modal-header">
                                                    <h5 class="modal-title fw-bold">Update Role for ${u.fullName}</h5>
                                                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                                </div>
                                                <div class="modal-body">
                                                    <div class="mb-3">
                                                        <label class="form-label fw-semibold">Target User Role</label>
                                                        <select class="form-select" name="role">
                                                            <option value="STUDENT" ${u.role == 'STUDENT' ? 'selected' : ''}>STUDENT</option>
                                                            <option value="SENIOR" ${u.role == 'SENIOR' ? 'selected' : ''}>SENIOR (Mentor)</option>
                                                            <option value="ALUMNI" ${u.role == 'ALUMNI' ? 'selected' : ''}>ALUMNI</option>
                                                            <option value="ADMIN" ${u.role == 'ADMIN' ? 'selected' : ''}>ADMIN</option>
                                                        </select>
                                                    </div>
                                                    <div class="mb-3">
                                                        <label class="form-label fw-semibold">Reason for Role Change</label>
                                                        <input type="text" class="form-control" name="reason" placeholder="e.g. Promoted to Senior based on academic standing" required>
                                                    </div>
                                                </div>
                                                <div class="modal-footer">
                                                    <button type="button" class="btn btn-secondary rounded-pill" data-bs-dismiss="modal">Cancel</button>
                                                    <button type="submit" class="btn btn-primary rounded-pill px-4">Update Role</button>
                                                </div>
                                            </form>
                                        </div>
                                    </div>
                                </div>

                                <!-- Credit Adjustment Modal for User -->
                                <div class="modal fade text-start" id="creditModal-${u.id}" tabindex="-1" aria-hidden="true">
                                    <div class="modal-dialog">
                                        <div class="modal-content">
                                            <form action="${pageContext.request.contextPath}/admin/credits/adjust" method="post">
                                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                                <input type="hidden" name="userId" value="${u.id}">

                                                <div class="modal-header">
                                                    <h5 class="modal-title fw-bold">Adjust Skill Credits for ${u.fullName}</h5>
                                                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                                </div>
                                                <div class="modal-body">
                                                    <div class="mb-3">
                                                        <label class="form-label fw-semibold">Adjustment Amount (Positive to add, Negative to deduct)</label>
                                                        <input type="number" class="form-control" name="amount" placeholder="e.g. 20 or -10" required>
                                                        <div class="form-text small">Current Balance: <strong>${u.creditBalance} Credits</strong></div>
                                                    </div>
                                                    <div class="mb-3">
                                                        <label class="form-label fw-semibold">Administrative Reason / Justification</label>
                                                        <input type="text" class="form-control" name="reason" placeholder="e.g. Academic competition award or manual correction" required>
                                                    </div>
                                                </div>
                                                <div class="modal-footer">
                                                    <button type="button" class="btn btn-secondary rounded-pill" data-bs-dismiss="modal">Cancel</button>
                                                    <button type="submit" class="btn btn-warning rounded-pill px-4">Apply Adjustment</button>
                                                </div>
                                            </form>
                                        </div>
                                    </div>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>