<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>Add New User</title>
</head>
<body>
	<h3>Add New User</h3>
	<div class="card p-4">
		<form action="${pageContext.request.contextPath}/admin/addUser"
			method="post">
			<div class="row">
				<div class="col-md-6 mb-3">
					<label for="username" class="form-label">Username</label> <input
						type="text" class="form-control" id="username" name="username"
						required>
				</div>
				<div class="col-md-6 mb-3">
					<label for="email" class="form-label">Email address</label> <input
						type="email" class="form-control" id="email" name="email" required>
				</div>
			</div>
			<div class="row">
				<div class="col-md-6 mb-3">
					<label for="firstName" class="form-label">First Name</label> <input
						type="text" class="form-control" id="firstName" name="firstName"
						required>
				</div>
				<div class="col-md-6 mb-3">
					<label for="lastName" class="form-label">Last Name</label> <input
						type="text" class="form-control" id="lastName" name="lastName"
						required>
				</div>
			</div>
			<div class="row">
				<div class="col-md-6 mb-3">
					<label for="password" class="form-label">Password</label> <input
						type="password" class="form-control" id="password" name="password"
						required>
				</div>
				<div class="col-md-6 mb-3">
					<label for="role" class="form-label">Role</label> <select
						class="form-select" id="role" name="role" required>
						<option value="EMPLOYEE">Employee</option>
						<option value="MANAGER">Manager</option>
					</select>
				</div>
			</div>
			<div class="mb-3">
				<label for="managerId" class="form-label">Assign Manager
					(Optional)</label> <select class="form-select" id="managerId"
					name="managerId">
					<option value="">-- No Manager --</option>
					<c:forEach var="manager" items="${managers}">
						<option value="${manager.id}">${manager.firstName}
							${manager.lastName}</option>
					</c:forEach>
				</select>
			</div>
			<button type="submit" class="btn btn-primary">Add User</button>
			<a
				href="${pageContext.request.contextPath}/admin?action=manageEmployees"
				class="btn btn-secondary">Cancel</a>
		</form>
	</div>
</body>
</html>