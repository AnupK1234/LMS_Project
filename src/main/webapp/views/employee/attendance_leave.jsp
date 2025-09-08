<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>Attendance & Leave Management</title>
<!-- Bootstrap 5 CSS -->
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
	rel="stylesheet">
<!-- Icons (Bootstrap Icons) -->
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css"
	rel="stylesheet">
<!-- Vanilla Calendar CSS -->
<link
	href="https://cdn.jsdelivr.net/npm/@uvarov.frontend/vanilla-calendar/build/vanilla-calendar.min.css"
	rel="stylesheet">
<link
	href="https://cdn.jsdelivr.net/npm/@uvarov.frontend/vanilla-calendar/build/themes/light.min.css"
	rel="stylesheet">
<style>
body {
	background: #f3f5f7;
}

.main-card {
	border-radius: 1.5rem;
	box-shadow: 0 4px 32px rgba(0, 0, 0, 0.08);
	background: #fff;
	padding: 2rem 2rem 2rem 2rem;
}

.calendar-card {
	border-radius: 1rem;
	background: #fcfcfc;
	padding: 1.5rem;
	box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.actions-card {
	border-radius: 1rem;
	background: #fcfcfc;
	padding: 1.5rem;
	box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.section-title {
	font-weight: 700;
	font-size: 1.65rem;
	margin-bottom: 1rem;
	color: #1976D2;
}

.calendar-legend {
	margin: 1rem 0 0.5rem 0;
	font-size: 0.93rem;
	gap: 1rem;
	display: flex;
	flex-wrap: wrap;
}

.legend-item {
	font-size: 0.93rem;
	gap: 0.3rem;
	display: flex;
	align-items: center;
}

.legend-circle {
	width: 0.85em;
	height: 0.85em;
	border-radius: 50%;
	margin-right: 0.2em;
	display: inline-block;
}

/* --- CALENDAR STATUS COLORS --- */
#calendar .vanilla-calendar-day_weekend {
	background: #f8f9fa !important;
	color: #adb5bd !important;
}

#calendar .vanilla-calendar-day_holiday {
	background: #e3f2fd !important;
	color: #1976D2 !important;
	font-weight: 700;
	border-radius: 8px;
}

#calendar .vanilla-calendar-day_present {
	background: transparent !important;
	color: #388e3c !important; /* Green */
	font-weight: 700;
	border-radius: 8px;
}

#calendar .vanilla-calendar-day_onleave {
	background: transparent !important;
	color: #FFC107 !important; /* Yellow */
	font-weight: 700;
	border-radius: 8px;
}

#calendar .vanilla-calendar-day_absent {
	background: transparent !important;
	color: #F44336 !important; /* Red */
	font-weight: 700;
	border-radius: 8px;
}
</style>
</head>
<body>
	<div class="container pt-2 pb-5">
		<div class="main-card">
			<div class="row g-4 align-items-stretch">
				<!-- Calendar Section -->
				<div class="col-lg-5 mb-3 mb-lg-0">
					<div class="calendar-card h-100">
						<div class="d-flex align-items-center mb-3">
							<i class="bi bi-calendar3 display-6 me-2 text-primary"></i>
							<h2 class="section-title mb-0">Your Attendance</h2>
						</div>
						<div class="calendar-legend">
							<span class="legend-item"><span class="legend-circle"
								style="background: #388e3c;"></span>Present</span> <span
								class="legend-item"><span class="legend-circle"
								style="background: #FFC107;"></span>On Leave</span> <span
								class="legend-item"><span class="legend-circle"
								style="background: #1976D2;"></span>Holiday</span> <span
								class="legend-item"><span class="legend-circle"
								style="background: #F44336;"></span>Absent</span>
						</div>
						<div id="calendar"></div>
					</div>
				</div>
				<!-- Actions Section -->
				<div class="col-lg-7">
					<div class="actions-card h-100 d-flex flex-column">
						<div>
							<h2 class="section-title">Apply for Leave</h2>
							<p class="text-muted mb-3">Select a valid working day from
								the calendar to begin your leave application.</p>
							<hr>
							<!-- Leave Application Form -->
							<form action="${pageContext.request.contextPath}/employee"
								method="post" class="needs-validation" novalidate>
								<input type="hidden" name="action" value="applyLeave">
								<div class="row g-3">
									<div class="col-sm-6">
										<label for="startDate" class="form-label">Start Date</label> <input
											type="text" class="form-control" id="startDate"
											name="startDate" readonly required>
										<div class="invalid-feedback">Please select a start
											date.</div>
									</div>
									<div class="col-sm-6">
										<label for="endDate" class="form-label">End Date</label> <input
											type="text" class="form-control" id="endDate" name="endDate"
											readonly required>
										<div class="invalid-feedback">Please select an end date.</div>
									</div>
									<div class="col-12">
										<label for="reason" class="form-label">Reason for
											Leave</label>
										<textarea class="form-control" id="reason" name="reason"
											rows="3" required></textarea>
										<div class="invalid-feedback">Please provide a reason
											for your leave.</div>
									</div>
								</div>
								<button type="submit" class="btn btn-warning mt-4 w-100">
									<i class="bi bi-send me-2"></i>Submit Leave Application
								</button>
							</form>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<!-- JS includes -->
	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
	<script
		src="https://cdn.jsdelivr.net/npm/@uvarov.frontend/vanilla-calendar/build/vanilla-calendar.min.js"
		defer></script>
	<script>
    document.addEventListener('DOMContentLoaded', () => {
        // --- DATA PREPARATION ---
        const holidays = [
            <c:forEach var="h" items="${holidays}"> { date: '${h.holidayDate}', title: '<c:out value="${h.title}" />' }, </c:forEach>
        ];
		const attendanceRecords = [
			<c:forEach var="a" items="${attendanceRecords}"> { date: '${a.date}', status: '<c:out value="${a.status}" />' }, </c:forEach>
		];
		const missedDates = [ <c:forEach var="d" items="${missedDates}"> '${d}', </c:forEach> ];
		const leaveHistory = [
			<c:forEach var="lr" items="${leaveHistory}"> { startDate: '${lr.startDate}', endDate: '${lr.endDate}', status: '${lr.status}' }, </c:forEach>
		];

        const calendarPopups = {};
        const holidayDates = holidays.map(h => {
            calendarPopups[h.date] = { modifier: 'vanilla-calendar-day_holiday', html: h.title };
            return h.date;
        });

		missedDates.forEach(date => calendarPopups[date] = { modifier: 'vanilla-calendar-day_absent', html: 'Absent' });
		attendanceRecords.forEach(a => {
			if (a.status.toUpperCase() === 'PRESENT') calendarPopups[a.date] = { modifier: 'vanilla-calendar-day_present', html: 'Present' };
		});
		leaveHistory.forEach(lr => {
			if (lr.status.toUpperCase() === 'APPROVED') {
				let currentDate = new Date(lr.startDate);
				let endDate = new Date(lr.endDate);
				while (currentDate <= endDate) {
					let formattedDate = currentDate.toISOString().split('T')[0];
					calendarPopups[formattedDate] = { modifier: 'vanilla-calendar-day_onleave', html: 'On Approved Leave' };
					currentDate.setDate(currentDate.getDate() + 1);
				}
			}
		});

        // --- JAVASCRIPT HELPER FUNCTION ---
        function calculateWorkingDaysJS(startDateStr, endDateStr, holidaysArr) {
            let count = 0;
            const start = new Date(startDateStr);
            const end = new Date(endDateStr);
            let current = new Date(start.toISOString().slice(0, 10)); // Normalize to avoid timezone issues

            while (current <= end) {
                const dayOfWeek = current.getUTCDay();
                const currentDateStr = current.toISOString().slice(0, 10);
                const isWeekend = dayOfWeek === 0 || dayOfWeek === 6;
                const isHoliday = holidaysArr.includes(currentDateStr);

                if (!isWeekend && !isHoliday) {
                    count++;
                }
                current.setDate(current.getDate() + 1);
            }
            return count;
        }

        // --- CALENDAR INITIALIZATION ---
        const calendar = new VanillaCalendar('#calendar', {
            popups: calendarPopups,
            settings: {
                selection: { day: 'multiple-ranged' },
                visibility: { weekend: true, daysOutside: false },
            },
            actions: {
                clickDay(e, dates) {
                    const clickedDateStr = e.target.dataset.calendarDay;
                    if (!clickedDateStr) return;

                    // --- VALIDATION 1: Check for weekends and holidays ---
                    const clickedDate = new Date(clickedDateStr);
                    const dayOfWeek = clickedDate.getUTCDay();

                    if (dayOfWeek === 0 || dayOfWeek === 6 || holidayDates.includes(clickedDateStr)) {
                        alert('You cannot select weekends or company holidays for a leave application.');
                        const index = dates.indexOf(clickedDateStr);
                        if (index > -1) dates.splice(index, 1);
                        calendar.settings.selected.dates = dates;
                        calendar.update(); 
                        return;
                    }

                    // --- VALIDATION 2: Check for monthly leave day limit ---
                    const month = clickedDate.getUTCMonth() + 1; // JS months are 0-11
                    const year = clickedDate.getUTCFullYear();

                    let daysAlreadyTaken = 0;
                    leaveHistory.forEach(leave => {
                        if (leave.status.toUpperCase() === 'APPROVED') {
                            const leaveStartDate = new Date(leave.startDate);
                            if (leaveStartDate.getUTCFullYear() === year && (leaveStartDate.getUTCMonth() + 1) === month) {
                                daysAlreadyTaken += calculateWorkingDaysJS(leave.startDate, leave.endDate, holidayDates);
                            }
                        }
                    });

                    const newSelectionStartDate = dates.length > 0 ? dates[0] : clickedDateStr;
                    const newSelectionEndDate = dates.length > 0 ? dates[dates.length - 1] : clickedDateStr;
                    const daysInNewRequest = calculateWorkingDaysJS(newSelectionStartDate, newSelectionEndDate, holidayDates);
                    
                    if (daysAlreadyTaken + daysInNewRequest > 2) {
                         const remainingDays = 2 - daysAlreadyTaken;
                         let alertMessage = remainingDays > 0 
                             ? `You only have ${remainingDays} day(s) of leave remaining for this month.`
                             : "You have already used all 2 of your leave days for this month.";
                         
                         alert(`Cannot select this range. ${alertMessage}`);

                         // Revert the selection
                         const index = dates.indexOf(clickedDateStr);
                         if (index > -1) dates.splice(index, 1);
                         calendar.settings.selected.dates = dates;
                         calendar.update();
                         return; // Stop processing
                    }


                    // --- Update form fields if all validations pass ---
                    if (dates && dates.length > 0) {
                        dates.sort((a, b) => new Date(a) - new Date(b));
                        document.getElementById('startDate').value = dates[0];
                        document.getElementById('endDate').value = dates[dates.length - 1];
                    } else {
                        document.getElementById('startDate').value = '';
                        document.getElementById('endDate').value = '';
                    }
                },
            },
        });
        calendar.init();

        // --- Bootstrap form validation ---
        (() => {
            'use strict';
            var forms = document.querySelectorAll('.needs-validation');
            Array.prototype.slice.call(forms).forEach(form => {
                form.addEventListener('submit', event => {
                    if (!form.checkValidity()) {
                        event.preventDefault();
                        event.stopPropagation();
                    }
                    form.classList.add('was-validated');
                }, false);
            });
        })();
    });
    </script>
</body>
</html>