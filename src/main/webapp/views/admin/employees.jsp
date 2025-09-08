<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>Manage Users</title>
<link
	href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap"
	rel="stylesheet">
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css"
	rel="stylesheet">
<style>
body, .card, .table, .form-control, .badge, .btn {
	font-family: 'Poppins', Arial, sans-serif !important;
}

h3 {
	font-weight: 700;
	font-size: 2rem;
	color: #232323;
	margin-bottom: 1.8rem;
}

.modern-card {
	background: #fff;
	border-radius: 1.25rem;
	box-shadow: 0 6px 28px rgba(44, 62, 80, 0.17), 0 1.5px 7px
		rgba(44, 62, 80, 0.08);
	padding: 2rem 2.1rem 2.2rem 2.1rem;
	margin-bottom: 2rem;
	border: 1px solid #f1f4f6;
}

.table thead th {
	background: #f8fafb !important;
	border-bottom: 2px solid #e8ecef;
	font-weight: 600;
	font-size: 1.04rem;
	color: #495057;
}

.btn-action {
	margin-right: 5px;
}
</style>
</head>
<body>
	<div class="d-flex justify-content-between align-items-center">
		<h3>Manage Users</h3>
		<a
			href="${pageContext.request.contextPath}/admin?action=showAddUserForm"
			class="btn btn-primary mb-3"> <i class="bi bi-plus-circle"></i>
			Add User
		</a>
	</div>
	<div class="modern-card">
		<table class="table table-striped table-hover" id="usersTable">
			<thead class="table-light">
				<tr>
					<th>First Name</th>
					<th>Last Name</th>
					<th>Email</th>
					<th>Role</th>
					<th>Actions</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="user" items="${userList}">
					<tr>
						<td><c:out value="${user.firstName}" /></td>
						<td><c:out value="${user.lastName}" /></td>
						<td><c:out value="${user.email}" /></td>
						<td><c:out value="${user.role}" /></td>
						<td><a
							href="${pageContext.request.contextPath}/admin?action=showEditUserForm&id=${user.id}"
							class="btn btn-sm btn-outline-primary btn-action"> <i
								class="bi bi-pencil-square"></i> Edit
						</a>
							<button type="button"
								class="btn btn-sm btn-outline-danger btn-action"
								data-bs-toggle="modal" data-bs-target="#deleteUserModal"
								data-user-id="${user.id}"
								data-user-name="${user.firstName} ${user.lastName}">
								<i class="bi bi-trash"></i> Delete
							</button></td>
					</tr>
				</c:forEach>
				<c:if test="${empty userList}">
					<tr>
						<td colspan="5" class="text-center text-muted">No users
							found.</td>
					</tr>
				</c:if>
			</tbody>
		</table>
	</div>

	<!-- Delete Confirmation Modal -->
	<div class="modal fade" id="deleteUserModal" tabindex="-1"
		aria-labelledby="deleteUserModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="deleteUserModalLabel">Confirm
						Deletion</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal"
						aria-label="Close"></button>
				</div>
				<div class="modal-body">
					Are you sure you want to delete the user <strong
						id="userNameToDelete"></strong>? This action cannot be undone.
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary"
						data-bs-dismiss="modal">Cancel</button>
					<!-- This is now a form that submits via POST -->
					<form action="${pageContext.request.contextPath}/admin/deleteUser"
						method="post" class="d-inline">
						<input type="hidden" id="userIdToDelete" name="id" value="">
						<button type="submit" class="btn btn-danger">Delete</button>
					</form>
				</div>
			</div>
		</div>
	</div>

	<script>
		const deleteUserModal = document.getElementById('deleteUserModal');
		deleteUserModal.addEventListener('show.bs.modal', function(event) {
			const button = event.relatedTarget;
			const userId = button.getAttribute('data-user-id');
			const userName = button.getAttribute('data-user-name');

			const modalBodyName = deleteUserModal
					.querySelector('#userNameToDelete');
			// Get the hidden input field inside the modal's form
			const hiddenInput = deleteUserModal
					.querySelector('#userIdToDelete');

			modalBodyName.textContent = userName;
			// Set the value of the hidden input field, which will be submitted with the form
			hiddenInput.value = userId;
		});
	</script>
</body>
</html>