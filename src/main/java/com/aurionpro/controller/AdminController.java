package com.aurionpro.controller;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

import com.aurionpro.dao.EmployeeDAO;
import com.aurionpro.dao.HolidayDAO;
import com.aurionpro.dao.LeaveRequestDao;
import com.aurionpro.dao.UserDao;
import com.aurionpro.model.Holiday;
import com.aurionpro.model.LeaveRequest;
import com.aurionpro.model.User;
import com.aurionpro.service.LeaveService;
import com.aurionpro.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/admin/*")
public class AdminController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private LeaveService leaveService;
	private UserService userService;
	private UserDao userDao;

	@Override
	public void init() {
		this.leaveService = new LeaveService();
		this.userService = new UserService();
		this.userDao = new UserDao();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String action = request.getParameter("action");
		String pathInfo = request.getPathInfo();

		if (action == null) {
			if (pathInfo != null && !pathInfo.equals("/")) {
				action = pathInfo.substring(1);
			} else {
				action = "dashboard";
			}
		}

		switch (action) {
		case "showPendingRequests":
			showPendingRequestsPage(request, response);
			break;
		case "manageEmployees":
			showManageEmployeesPage(request, response);
			break;
		case "showHoliday":
			showCreateHoliday(request, response);
			break;
		case "showAddUserForm":
			showAddUserForm(request, response);
			break;
		case "showEditUserForm":
			showEditUserForm(request, response);
			break;
		// The deleteUser case is removed from doGet
		case "dashboard":
		default:
			showDashboard(request, response);
			break;
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String action = request.getParameter("action");
		String pathInfo = request.getPathInfo();

		if (action == null) {
			if (pathInfo != null && !pathInfo.equals("/")) {
				action = pathInfo.substring(1);
			} else {
				action = "dashboard";
			}
		}
		switch (action) {
		case "createHoliday":
			createHoliday(request, response);
			break;
		case "processLeave":
			processLeaveRequest(request, response);
			break;
		case "addUser":
			addUser(request, response);
			break;
		case "updateUser":
			updateUser(request, response);
			break;
		case "deleteUser":
			deleteUser(request, response);
			break;
		}

	}

	private void showPendingRequestsPage(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		List<LeaveRequest> pendingRequests = leaveService.getPendingRequestsForAllManager();

		request.setAttribute("pendingRequests", pendingRequests);
		request.setAttribute("view", "pending_requests");
		request.getRequestDispatcher("/views/admin/dashboard.jsp").forward(request, response);
	}

	private void showDashboard(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		User admin = (User) session.getAttribute("user");
		EmployeeDAO employeeDAO = new EmployeeDAO();
		LeaveRequestDao leaveDAO = new LeaveRequestDao();
		HolidayDAO holidayDAO = new HolidayDAO();
		int employeeCount = 0, managerCount = 0, pendingCount = 0, holidayCount = 0;
		List<LeaveRequest> recentPending = null;
		List<Holiday> upcomingHolidays = null;

		try {
			employeeCount = employeeDAO.countByRole("EMPLOYEE");
			managerCount = employeeDAO.countByRole("MANAGER");
			pendingCount = leaveDAO.countPendingRequests();
			holidayCount = holidayDAO.countUpcomingHolidays();

			recentPending = leaveDAO.findRecentPendingRequests(5);
			upcomingHolidays = holidayDAO.findUpcomingHolidays(5);

		} catch (Exception e) {
			e.printStackTrace();
		}

		request.setAttribute("employeeCount", employeeCount);
		request.setAttribute("managerCount", managerCount);
		request.setAttribute("pendingCount", pendingCount);
		request.setAttribute("holidayCount", holidayCount);
		request.setAttribute("recentPendingRequests", recentPending);
		request.setAttribute("upcomingHolidays", upcomingHolidays);
		request.setAttribute("view", "dashboard");

		request.getRequestDispatcher("/views/admin/dashboard.jsp").forward(request, response);
	}

	private void showManageEmployeesPage(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		List<User> userList = userService.getAllEmployees();

		request.setAttribute("userList", userList);
		request.setAttribute("view", "manage_employees");
		request.getRequestDispatcher("/views/admin/dashboard.jsp").forward(request, response);
	}

	private void createHoliday(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String holidayDateStr = request.getParameter("holiday_date");
		String description = request.getParameter("description");

		try {
			Date holidayDate = Date.valueOf(holidayDateStr); // java.sql.Date
			HolidayDAO holidayDAO = new HolidayDAO();
			holidayDAO.addHoliday(holidayDate, description);
			request.getSession().setAttribute("success_toast", "Holiday created successfully!");
			request.setAttribute("view", "showHoliday");
		} catch (Exception e) {
			e.printStackTrace();
			request.getSession().setAttribute("error_toast", "Failed to create holiday.");
		}
		request.getRequestDispatcher("/views/admin/dashboard.jsp").forward(request, response);
	}

	private void showCreateHoliday(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setAttribute("view", "showHoliday");
		request.getRequestDispatcher("/views/admin/dashboard.jsp").forward(request, response);
	}

	private void processLeaveRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
		int leaveId = Integer.parseInt(request.getParameter("leaveId"));
		String decision = request.getParameter("decision");
		HttpSession session = request.getSession();
		boolean success = false;

		if ("approve".equals(decision)) {
			success = leaveService.approveLeave(leaveId);
			if (success)
				session.setAttribute("success_toast", "Leave request approved.");
		} else if ("reject".equals(decision)) {
			String reason = request.getParameter("rejectionReason");
			success = leaveService.rejectLeave(leaveId, reason);
			if (success)
				session.setAttribute("success_toast", "Leave request rejected.");
		}

		if (!success) {
			session.setAttribute("error_toast", "Failed to process leave request.");
		}

		response.sendRedirect(request.getContextPath() + "/admin?action=showPendingRequests");
	}

	private void showAddUserForm(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		List<User> managers = userDao.findAllManagers();
		request.setAttribute("managers", managers);
		request.setAttribute("view", "add_user");
		request.getRequestDispatcher("/views/admin/dashboard.jsp").forward(request, response);
	}

	private void showEditUserForm(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int userId = Integer.parseInt(request.getParameter("id"));
		User user = userDao.findUserById(userId);
		List<User> managers = userDao.findAllManagers();
		request.setAttribute("user", user);
		request.setAttribute("managers", managers);
		request.setAttribute("view", "edit_user");
		request.getRequestDispatcher("/views/admin/dashboard.jsp").forward(request, response);
	}

	private void addUser(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		User newUser = new User();
		newUser.setUsername(request.getParameter("username"));
		newUser.setFirstName(request.getParameter("firstName"));
		newUser.setLastName(request.getParameter("lastName"));
		newUser.setEmail(request.getParameter("email"));
		newUser.setPassword(request.getParameter("password")); // Password will be hashed in the service
		newUser.setRole(request.getParameter("role"));

		String managerIdStr = request.getParameter("managerId");
		if (managerIdStr != null && !managerIdStr.isEmpty()) {
			newUser.setManagerId(Integer.parseInt(managerIdStr));
		}

		boolean success = userService.addUser(newUser);

		if (success) {
			request.getSession().setAttribute("success_toast", "User added successfully!");
		} else {
			request.getSession().setAttribute("error_toast",
					"Failed to add user. Email or username may already exist.");
		}
		response.sendRedirect(request.getContextPath() + "/admin?action=manageEmployees");
	}

	private void updateUser(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		User user = new User();
		user.setId(Integer.parseInt(request.getParameter("id")));
		user.setUsername(request.getParameter("username"));
		user.setFirstName(request.getParameter("firstName"));
		user.setLastName(request.getParameter("lastName"));
		user.setEmail(request.getParameter("email"));
		user.setRole(request.getParameter("role"));

		String managerIdStr = request.getParameter("managerId");
		if (managerIdStr != null && !managerIdStr.isEmpty()) {
			user.setManagerId(Integer.parseInt(managerIdStr));
		} else {
			user.setManagerId(null);
		}

		boolean success = userService.updateUser(user);

		if (success) {
			request.getSession().setAttribute("success_toast", "User updated successfully!");
		} else {
			request.getSession().setAttribute("error_toast", "Failed to update user.");
		}

		response.sendRedirect(request.getContextPath() + "/admin?action=manageEmployees");
	}

	private void deleteUser(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		String userIdStr = request.getParameter("id");

		// Check if the ID is null or empty to prevent NumberFormatException
		if (userIdStr == null || userIdStr.trim().isEmpty()) {
			request.getSession().setAttribute("error_toast", "Invalid user ID provided for deletion.");
			response.sendRedirect(request.getContextPath() + "/admin?action=manageEmployees");
			return; // Stop execution
		}

		try {
			int userId = Integer.parseInt(userIdStr);
			boolean success = userService.deleteUser(userId);

			if (success) {
				request.getSession().setAttribute("success_toast", "User deleted successfully!");
			} else {
				request.getSession().setAttribute("error_toast",
						"Failed to delete user. The user might be a manager to other employees.");
			}
		} catch (NumberFormatException e) {
			// This catch block handles cases where ID is not a valid number
			request.getSession().setAttribute("error_toast", "Invalid user ID format.");
		}

		response.sendRedirect(request.getContextPath() + "/admin?action=manageEmployees");
	}
}