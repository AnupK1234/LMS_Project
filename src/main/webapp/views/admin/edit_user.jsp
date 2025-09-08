<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>Edit User</title>
</head>
<body>
	<h3>
		Edit User:
		<c:out value="${user.firstName} ${user.lastName}" />
	</h3>
	<div class="card p-4">
		<form action="${pageContext.request.contextPath}/admin/updateUser"
			method="post">
			<input type="hidden" name="id" value="${user.id}">
			<div class="row">
				<div class="col-md-6 mb-3">
					<label for="username" class="form-label">Username</label> <input
						type="text" class="form-control" id="username" name="username"
						value="<c:out value="${user.username}"/>" required>
				</div>
				<div class="col-md-6 mb-3">
					<label for="email" class="form-label">Email address</label> <input
						type="email" class="form-control" id="email" name="email"
						value="<c:out value="${user.email}"/>" required>
				</div>
			</div>
			<div class="row">
				<div class="col-md-6 mb-3">
					<label for="firstName" class="form-label">First Name</label> <input
						type="text" class="form-control" id="firstName" name="firstName"
						value="<c:out value="${user.firstName}"/>" required>
				</div>
				<div class="col-md-6 mb-3">
					<label for="lastName" class="form-label">Last Name</label> <input
						type="text" class="form-control" id="lastName" name="lastName"
						value="<c:out value="${user.lastName}"/>" required>
				</div>
			</div>
			<div class="row">
				<div class="col-md-6 mb-3">
					<label for="role" class="form-label">Role</label> <select
						class="form-select" id="role" name="role" required>
						<option value="EMPLOYEE"
							${user.role == 'EMPLOYEE' ? 'selected' : ''}>Employee</option>
						<option value="MANAGER"
							${user.role == 'MANAGER' ? 'selected' : ''}>Manager</option>
					</select>
				</div>
				<div class="col-md-6 mb-3">
					<label for="managerId" class="form-label">Assign Manager
						(Optional)</label> <select class="form-select" id="managerId"
						name="managerId">
						<option value="">-- No Manager --</option>
						<c:forEach var="manager" items="${managers}">
							<option value="${manager.id}"
								${user.managerId == manager.id ? 'selected' : ''}>
								${manager.firstName} ${manager.lastName}</option>
						</c:forEach>
					</select>
				</div>
			</div>
			<p class="form-text">Password cannot be changed from this form.</p>
			<button type="submit" class="btn btn-primary">Update User</button>
			<a
				href="${pageContext.request.contextPath}/admin?action=manageEmployees"
				class="btn btn-secondary">Cancel</a>
		</form>
	</div>
</body>
</html>