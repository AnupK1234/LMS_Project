package com.aurionpro.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.aurionpro.model.User;
import com.aurionpro.util.DatabaseUtil;

public class UserDao {

	public User findUserByEmail(String email) {
		String sql = "SELECT * FROM users WHERE email = ?";
		User user = null;

		try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, email);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					user = new User();
					user.setId(rs.getInt("id"));
					user.setUsername(rs.getString("username"));
					user.setPassword(rs.getString("password"));
					user.setFirstName(rs.getString("first_name"));
					user.setLastName(rs.getString("last_name"));
					user.setEmail(rs.getString("email"));
					user.setRole(rs.getString("role"));
					user.setManagerId((Integer) rs.getObject("manager_id"));
					user.setLeaveBalance(rs.getInt("leave_balance"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return user;
	}

	public User findUserById(int id) {
		String sql = "SELECT * FROM users WHERE id = ?";
		User user = null;

		try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setInt(1, id);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					user = new User();
					user.setId(rs.getInt("id"));
					user.setUsername(rs.getString("username"));
					user.setPassword(rs.getString("password"));
					user.setFirstName(rs.getString("first_name"));
					user.setLastName(rs.getString("last_name"));
					user.setEmail(rs.getString("email"));
					user.setRole(rs.getString("role"));
					user.setManagerId((Integer) rs.getObject("manager_id"));
					user.setLeaveBalance(rs.getInt("leave_balance"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return user;
	}

	public List<User> findEmployeesByManagerId(int managerId) {
		List<User> employees = new ArrayList<>();
		String sql = "SELECT * FROM users WHERE manager_id = ? AND role = 'EMPLOYEE' ORDER BY first_name, last_name";

		try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setInt(1, managerId);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					User user = new User();
					user.setId(rs.getInt("id"));
					user.setFirstName(rs.getString("first_name"));
					user.setLastName(rs.getString("last_name"));
					user.setEmail(rs.getString("email"));
					user.setLeaveBalance(rs.getInt("leave_balance"));
					employees.add(user);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employees;
	}

	public boolean updateUserProfile(User user) {
		String sql = "UPDATE users SET first_name = ?, last_name = ?, email = ? WHERE id = ?";
		try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, user.getFirstName());
			stmt.setString(2, user.getLastName());
			stmt.setString(3, user.getEmail());
			stmt.setInt(4, user.getId());

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean isEmailTakenByOtherUser(String email, int userIdToExclude) {
		String sql = "SELECT 1 FROM users WHERE email = ? AND id != ? LIMIT 1";

		try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, email);
			stmt.setInt(2, userIdToExclude);

			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return true;
		}
	}

	public int getLeaveBalance(int id) {
		String sql = "SELECT leave_balance FROM users WHERE id = ?";

		try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setInt(1, id);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return rs.getInt("leave_balance");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public List<User> findAllEmployees() {
		List<User> users = new ArrayList<>();
		String sql = "SELECT * FROM users WHERE role <> 'ADMIN' ORDER BY first_name, last_name";

		try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					User user = new User();
					user.setId(rs.getInt("id"));
					user.setFirstName(rs.getString("first_name"));
					user.setLastName(rs.getString("last_name"));
					user.setEmail(rs.getString("email"));
					user.setRole(rs.getString("role"));
					user.setLeaveBalance(rs.getInt("leave_balance"));
					users.add(user);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return users;
	}

	public void updatePassword(String email, String password) {
		String sql = "UPDATE users SET password=? WHERE email=?";
		try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, password);
			ps.setString(2, email);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean addUser(User user) {
		String sql = "INSERT INTO users (username, password, first_name, last_name, email, role, manager_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
		try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, user.getUsername());
			stmt.setString(2, user.getPassword());
			stmt.setString(3, user.getFirstName());
			stmt.setString(4, user.getLastName());
			stmt.setString(5, user.getEmail());
			stmt.setString(6, user.getRole());
			if (user.getManagerId() != null) {
				stmt.setInt(7, user.getManagerId());
			} else {
				stmt.setNull(7, java.sql.Types.INTEGER);
			}
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace(); // Consider logging this exception
			return false;
		}
	}

	public boolean updateUser(User user) {
		String sql = "UPDATE users SET username = ?, first_name = ?, last_name = ?, email = ?, role = ?, manager_id = ? WHERE id = ?";
		try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, user.getUsername());
			stmt.setString(2, user.getFirstName());
			stmt.setString(3, user.getLastName());
			stmt.setString(4, user.getEmail());
			stmt.setString(5, user.getRole());
			if (user.getManagerId() != null) {
				stmt.setInt(6, user.getManagerId());
			} else {
				stmt.setNull(6, java.sql.Types.INTEGER);
			}
			stmt.setInt(7, user.getId());
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean deleteUser(int userId) {
		String sql = "DELETE FROM users WHERE id = ?";
		try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, userId);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<User> findAllManagers() {
		List<User> managers = new ArrayList<>();
		String sql = "SELECT id, first_name, last_name FROM users WHERE role = 'MANAGER'";
		try (Connection conn = DatabaseUtil.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				User manager = new User();
				manager.setId(rs.getInt("id"));
				manager.setFirstName(rs.getString("first_name"));
				manager.setLastName(rs.getString("last_name"));
				managers.add(manager);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return managers;
	}

	public boolean isUsernameTaken(String username) {
		String sql = "SELECT 1 FROM users WHERE username = ? LIMIT 1";
		try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, username);
			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return true; // Assume taken on error to be safe
		}
	}

}