<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>Dashboard Overview</title>
<link
	href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600;700&display=swap"
	rel="stylesheet">
<style>
/* Card and typography styles */
body, .card, .alert, .sidebar, .h2, .fw-bold {
	font-family: 'Poppins', Arial, sans-serif !important;
}

h3 {
	font-weight: 700;
	font-size: 2.2rem;
	margin-bottom: 2rem;
	color: #232323;
}
/* Stat card styles */
.modern-stat-card {
	flex: 1 1 200px;
	min-width: 220px;
	background: #fff;
	border-radius: 1.5rem;
	box-shadow: 0 4px 24px rgba(44, 62, 80, 0.1), 0 1.5px 7px
		rgba(44, 62, 80, 0.05);
	display: flex;
	align-items: center;
	padding: 1.2rem 1.6rem;
	position: relative;
	transition: all 0.2s ease-in-out;
	border: 1px solid #f2f3f7;
}

.modern-stat-card:hover {
	transform: translateY(-4px);
	box-shadow: 0 12px 36px rgba(44, 62, 80, 0.12), 0 3px 12px
		rgba(44, 62, 80, 0.08);
}

.modern-stat-icon {
	width: 46px;
	height: 46px;
	border-radius: 50%;
	background: #f7fafd;
	display: flex;
	align-items: center;
	justify-content: center;
	margin-left: 1.1rem;
	font-size: 1.65rem;
	color: #adb5bd;
}

.modern-stat-content {
	flex: 1;
}

.modern-stat-label {
	font-size: 0.97rem;
	font-weight: 600;
}

.modern-stat-value {
	font-size: 1.8rem;
	font-weight: 700;
	color: #232323;
}
/* Main content layout cards */
.content-card {
	background: #fff;
	border-radius: 1.5rem;
	box-shadow: 0 4px 24px rgba(44, 62, 80, 0.1), 0 1.5px 7px
		rgba(44, 62, 80, 0.05);
	padding: 1.8rem 2rem;
	height: 100%;
}

/* --- CSS FOR HOLIDAY TABLE --- */
.holiday-table-container {
	max-height: 250px; /* Set a max height */
	overflow-y: auto; /* Enable vertical scrolling */
}

/* Optional: Make table header sticky */
.holiday-table-container thead th {
	position: sticky;
	top: 0;
	background-color: #fff; /* Match card background */
	z-index: 1;
}
</style>
</head>
<body>
	<h3>Dashboard Overview</h3>

	<!-- Stat Cards Row -->
	<div class="d-flex flex-wrap gap-3 mb-4">
		<div class="modern-stat-card">
			<div class="modern-stat-content">
				<div class="text-success modern-stat-label">Leave Balance</div>
				<div class="modern-stat-value">${leaveBalance}Days</div>
			</div>
			<div class="modern-stat-icon text-success">
				<i class="bi bi-calendar2-check"></i>
			</div>
			<a
				href="${pageContext.request.contextPath}/employee/showAttendanceLeave"
				class="stretched-link"></a>
		</div>
		<div class="modern-stat-card">
			<div class="modern-stat-content">
				<div class="text-warning modern-stat-label">Pending Leaves</div>
				<div class="modern-stat-value">${pendingCount}</div>
			</div>
			<div class="modern-stat-icon text-warning">
				<i class="bi bi-card-list"></i>
			</div>
			<a href="${pageContext.request.contextPath}/employee/leaveHistory"
				class="stretched-link"></a>
		</div>
		<div class="modern-stat-card">
			<div class="modern-stat-content">
				<div class="text-info modern-stat-label">Approved Leaves</div>
				<div class="modern-stat-value">${approvedCount}</div>
			</div>
			<div class="modern-stat-icon text-info">
				<i class="bi bi-clock-history"></i>
			</div>
			<a href="${pageContext.request.contextPath}/employee/leaveHistory"
				class="stretched-link"></a>
		</div>
	</div>

	<!-- Attendance and Holidays Row -->
	<div class="row g-4">
		<!-- Attendance Column -->
		<div class="col-lg-5">
			<div
				class="content-card d-flex flex-column justify-content-center text-center">
				<h4 class="mb-3">
					<i class="bi bi-person-check me-2"></i>Daily Attendance
				</h4>
				<p class="text-muted">Please mark your attendance for today.</p>

				<c:choose>
					<c:when test="${isAttendanceMarkedToday}">
						<button type="button" class="btn btn-success btn-lg" disabled>
							<i class="bi bi-check-all me-2"></i>Attendance Marked
						</button>
					</c:when>
					<c:when test="${isTodayWorkable}">
						<form action="${pageContext.request.contextPath}/employee"
							method="post">
							<input type="hidden" name="action" value="markAttendance">
							<button type="submit" class="btn btn-primary btn-lg">
								<i class="bi bi-check-circle me-2"></i>Mark Today's Attendance
							</button>
						</form>
					</c:when>
					<c:otherwise>
						<button type="button" class="btn btn-secondary btn-lg" disabled>
							<i class="bi bi-calendar-x me-2"></i>Non-Working Day
						</button>
					</c:otherwise>
				</c:choose>
			</div>
		</div>

		<!-- Holidays Column -->
		<div class="col-lg-7">
			<div class="content-card">
				<h4 class="mb-3">
					<i class="bi bi-calendar-heart me-2"></i>Company Holidays
				</h4>
				<!-- NEW HOLIDAY TABLE STRUCTURE -->
				<div class="holiday-table-container">
					<table class="table table-striped table-hover">
						<thead class="table-light">
							<tr>
								<th>Date</th>
								<th>Description</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="holiday" items="${holidays}">
								<tr>
									<td><c:out value="${holiday.holidayDate}" /></td>
									<td><c:out value="${holiday.title}" /></td>
								</tr>
							</c:forEach>
							<c:if test="${empty holidays}">
								<tr>
									<td colspan="2" class="text-center text-muted">No holidays
										have been listed yet.</td>
								</tr>
							</c:if>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>

</body>
</html>