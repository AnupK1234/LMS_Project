package com.aurionpro.controller;
 
import java.io.IOException;
import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
 
import com.aurionpro.model.Attendance;
import com.aurionpro.model.Holiday;
import com.aurionpro.model.LeaveRequest;
import com.aurionpro.model.User;
import com.aurionpro.service.AttendanceService;
import com.aurionpro.service.EmployeeService;
import com.aurionpro.service.HolidayService;
import com.aurionpro.service.LeaveService;
 
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
 
@WebServlet("/employee/*")
public class EmployeeController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private EmployeeService employeeService;
	private LeaveService leaveService;
	private HolidayService holidayService;
	private AttendanceService attendanceService;
 
	@Override
	public void init() throws ServletException {
		employeeService = new EmployeeService();
		leaveService = new LeaveService();
		holidayService = new HolidayService();
		attendanceService = new AttendanceService();
	}
 
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
 
		String pathInfo = request.getPathInfo();
		if (pathInfo == null || pathInfo.equals("/")) {
			pathInfo = "/dashboard"; // default
		}
 
		switch (pathInfo) {
		case "/dashboard":
			showEmployeeDashboard(request, response);
			break;
		case "/showProfile":
			showProfile(request, response);
			break;
		case "/showAttendanceLeave":
			showAttendanceLeavePage(request, response);
			break;
		case "/leaveHistory":
			showLeaveHistory(request, response);
			break;
		default:
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}
 
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
 
		String action = request.getParameter("action");
		if (action == null) {
			response.sendRedirect(request.getContextPath() + "/employee/dashboard");
			return;
		}
 
		switch (action) {
		case "updateProfile":
			updateProfile(request, response);
			break;
		case "applyLeave":
			applyForLeave(request, response);
			break;
		case "markAttendance":
			markAttendance(request, response);
			break;
		default:
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action requested.");
			break;
		}
	}
 
	// --- GET handlers ---
	private void showEmployeeDashboard(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
 
		HttpSession session = request.getSession();
		User loggedInUser = (User) session.getAttribute("user");
 
		// Fetch dashboard stats
		int leaveBalance = employeeService.getLeaveBalance(loggedInUser.getId());
		List<LeaveRequest> leaveHistory = leaveService.getLeaveHistoryForUser(loggedInUser.getId());
		int pendingCount = (int) leaveHistory.stream().filter(lr -> "PENDING".equalsIgnoreCase(lr.getStatus())).count();
		int approvedCount = (int) leaveHistory.stream().filter(lr -> "APPROVED".equalsIgnoreCase(lr.getStatus()))
				.count();
 
		// Fetch data for attendance button and holiday list
		List<Holiday> holidays = holidayService.getAllHolidays();
		Set<LocalDate> holidayDates = holidayService.getAllHolidayDates();
		List<Attendance> attendanceRecords = attendanceService.getAttendanceForUser(loggedInUser.getId());
		boolean isAttendanceMarkedToday = attendanceService.isAttendanceMarkedToday(attendanceRecords);
 
		LocalDate today = LocalDate.now();
		DayOfWeek day = today.getDayOfWeek();
		boolean isTodayWorkable = !(day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY
				|| holidayDates.contains(today));
 
		// Set attributes for the JSP
		request.setAttribute("leaveBalance", leaveBalance);
		request.setAttribute("pendingCount", pendingCount);
		request.setAttribute("approvedCount", approvedCount);
		request.setAttribute("holidays", holidays);
		request.setAttribute("isAttendanceMarkedToday", isAttendanceMarkedToday);
		request.setAttribute("isTodayWorkable", isTodayWorkable);
 
		request.setAttribute("view", "dashboard");
		request.getRequestDispatcher("/views/employee/employee_home.jsp").forward(request, response);
	}
 
	private void showProfile(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setAttribute("view", "edit_profile");
		request.getRequestDispatcher("/views/employee/employee_home.jsp").forward(request, response);
	}
 
	private void showAttendanceLeavePage(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User currentUser = (User) request.getSession().getAttribute("user");
 
		List<Holiday> holidays = holidayService.getAllHolidays();
		List<Attendance> attendanceRecords = attendanceService.getAttendanceForUser(currentUser.getId());
		List<LeaveRequest> leaveHistory = leaveService.getLeaveHistoryForUser(currentUser.getId());
		List<String> missedDates = attendanceService.calculateMissedAttendanceDates(holidays, attendanceRecords,
				leaveHistory);
 
		request.setAttribute("holidays", holidays);
		request.setAttribute("attendanceRecords", attendanceRecords);
		request.setAttribute("leaveHistory", leaveHistory);
		request.setAttribute("missedDates", missedDates);
 
		request.setAttribute("view", "attendance_leave");
		request.getRequestDispatcher("/views/employee/employee_home.jsp").forward(request, response);
	}
 
	private void showLeaveHistory(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User currentUser = (User) request.getSession().getAttribute("user");
		List<LeaveRequest> leaveHistory = leaveService.getLeaveHistoryForUser(currentUser.getId());
 
		request.setAttribute("leaveHistory", leaveHistory);
		request.setAttribute("view", "leave_history");
		request.getRequestDispatcher("/views/employee/employee_home.jsp").forward(request, response);
	}
 
	// --- POST handlers ---
	private void updateProfile(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		User currentUser = (User) session.getAttribute("user");
		String originalEmail = currentUser.getEmail();
 
		User userToUpdate = new User();
		userToUpdate.setId(currentUser.getId());
		userToUpdate.setFirstName(request.getParameter("firstName"));
		userToUpdate.setLastName(request.getParameter("lastName"));
		userToUpdate.setEmail(request.getParameter("email"));
 
		boolean success = employeeService.updateUserProfile(userToUpdate, originalEmail);
 
		if (success) {
			currentUser.setFirstName(userToUpdate.getFirstName());
			currentUser.setLastName(userToUpdate.getLastName());
			currentUser.setEmail(userToUpdate.getEmail());
			session.setAttribute("user", currentUser);
			session.setAttribute("success_toast", "Profile updated successfully!");
		} else {
			session.setAttribute("error_toast", "Email is already registered by another user.");
		}
		response.sendRedirect(request.getContextPath() + "/employee/showProfile");
	}
 
	private void applyForLeave(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		User loggedInUser = (User) session.getAttribute("user");
 
		String startDateStr = request.getParameter("startDate");
		String endDateStr = request.getParameter("endDate");
		String reason = request.getParameter("reason");
 
		if (startDateStr == null || startDateStr.isEmpty() || endDateStr == null || endDateStr.isEmpty()
				|| reason == null || reason.trim().isEmpty()) {
			session.setAttribute("error_toast", "All fields are required. Please fill out the form completely.");
			response.sendRedirect(request.getContextPath() + "/employee/showAttendanceLeave");
			return;
		}
 
		LocalDate startDate = LocalDate.parse(startDateStr);
		LocalDate endDate = LocalDate.parse(endDateStr);
		Set<LocalDate> holidayDates = holidayService.getAllHolidayDates();
 
		long daysInNewRequest = calculateWorkingDays(startDate, endDate, holidayDates);
 
		if (daysInNewRequest == 0) {
			session.setAttribute("error_toast", "Cannot apply for leave. The selected range contains no working days.");
			response.sendRedirect(request.getContextPath() + "/employee/showAttendanceLeave");
			return;
		}
 
		int year = startDate.getYear();
		int month = startDate.getMonthValue();
		List<LeaveRequest> leavesInMonth = leaveService.getLeaveRequestsInMonth(loggedInUser.getId(), year, month);
 
		long daysAlreadyTaken = 0;
		for (LeaveRequest approvedLeave : leavesInMonth) {
			daysAlreadyTaken += calculateWorkingDays(approvedLeave.getStartDate().toLocalDate(),
					approvedLeave.getEndDate().toLocalDate(), holidayDates);
		}
 
		if (daysAlreadyTaken + daysInNewRequest > 2) {
			long remainingDays = 2 - daysAlreadyTaken;
			String errorMessage = (remainingDays > 0)
					? "You have only " + remainingDays + " day(s) of leave remaining this month."
					: "You have already used your 2 days of leave for this month.";
 
			session.setAttribute("error_toast", "Cannot apply. " + errorMessage);
			response.sendRedirect(request.getContextPath() + "/employee/showAttendanceLeave");
			return;
		}
 
		LeaveRequest leave = new LeaveRequest();
		leave.setUserId(loggedInUser.getId());
		leave.setStartDate(Date.valueOf(startDate));
		leave.setEndDate(Date.valueOf(endDate));
		leave.setReason(reason.trim());
 
		boolean success = leaveService.applyForLeave(leave);
 
		if (success) {
			session.setAttribute("success_toast", "Your leave application has been submitted!");
			response.sendRedirect(request.getContextPath() + "/employee/leaveHistory");
		} else {
			session.setAttribute("error_toast", "Failed to submit leave application. Please try again.");
			response.sendRedirect(request.getContextPath() + "/employee/showAttendanceLeave");
		}
	}
 
	private long calculateWorkingDays(LocalDate startDate, LocalDate endDate, Set<LocalDate> holidayDates) {
		long workingDays = 0;
		for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
			DayOfWeek day = date.getDayOfWeek();
			if (!(day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY || holidayDates.contains(date))) {
				workingDays++;
			}
		}
		return workingDays;
	}
 
	private void markAttendance(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession();
 
		LocalDate today = LocalDate.now();
		DayOfWeek day = today.getDayOfWeek();
		Set<LocalDate> holidayDates = holidayService.getAllHolidayDates();
 
		if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY || holidayDates.contains(today)) {
			session.setAttribute("error_toast", "Cannot mark attendance on a weekend or holiday.");
			response.sendRedirect(request.getContextPath() + "/employee/dashboard"); // Redirect to dashboard
			return;
		}
 
		User currentUser = (User) session.getAttribute("user");
		boolean success = attendanceService.markUserAttendance(currentUser);
 
		if (success) {
			session.setAttribute("success_toast", "Attendance marked successfully for today!");
		} else {
			session.setAttribute("error_toast", "Attendance was already marked or an error occurred.");
		}
		response.sendRedirect(request.getContextPath() + "/employee/dashboard"); // Redirect to dashboard
	}
}
 
 