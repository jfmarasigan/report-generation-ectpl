package com.cpi.ectpl.report.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringJoiner;

import com.cpi.ectpl.report.db.DatabaseConnection;
import com.cpi.ectpl.report.exception.ReportGenerationException;

public final class DatabaseOperations {

	private Integer policyId;
	private Integer tranId;

	public DatabaseOperations(Integer policyId, Integer tranId) {
		this.policyId = policyId;
		this.tranId = tranId;
	}

	/**
	 * Updates report status upon generation of reports
	 * */
	public void updateGenerateReportStatus(String message) {
		String query = "UPDATE EPIT_CTPL_TRANS SET GEN_REPORT_STAT = ?, LAST_UPDATE = sysdate() WHERE policy_id = ? AND tran_id = ?";

		try (Connection connection = DatabaseConnection.getMySQLConnection();) {
			try (PreparedStatement stmt = connection.prepareStatement(query);) {
				connection.setAutoCommit(false);
				stmt.setString(1, message);
				stmt.setInt(2, policyId);
				stmt.setInt(3, tranId);
				stmt.executeUpdate();
				connection.commit();
			} catch (SQLException e) {
				connection.rollback();
			}
		} catch (SQLException e) {
			throw new ReportGenerationException("An error occurred while updating report generation status", e);
		}
	}

	/**
	 * Updates email status upon sending reports through email
	 * */
	public void updateEmailSendingStatus(String message) {
		String query = "UPDATE EPIT_CTPL_TRANS SET EMAIL_REPORT_STAT = ?, LAST_UPDATE = sysdate() WHERE policy_id = ? AND tran_id = ?";

		try (Connection connection = DatabaseConnection.getMySQLConnection();) {
			try (PreparedStatement stmt = connection.prepareStatement(query);) {
				connection.setAutoCommit(false);
				stmt.setString(1, message);
				stmt.setInt(2, policyId);
				stmt.setInt(3, tranId);
				stmt.executeUpdate();
				connection.commit();
			} catch (SQLException e) {
				connection.rollback();
			}
		} catch (SQLException e) {
			throw new ReportGenerationException("An error occurred while updating report generation status", e);
		}
	}
	
	/**
	 * Retrieves a double-comma delimited string containing the reports to be generated
	 * */
	public String getReportsToBeGenerated(String reportType) {
		String query = "";
		
		try (Connection connection = DatabaseConnection.getMySQLConnection();
			 PreparedStatement stmt = connection.prepareStatement(query);) {
			ResultSet result = stmt.executeQuery();
			StringJoiner reports = new StringJoiner(",,");
			while (result.next()) {
				reports.add(result.getString("REPORT_NAME"));
			}
			return reports.toString();
		} catch (SQLException e) {
			throw new ReportGenerationException("An error occurred while retrieving reports to be generated.", e);
		}
	}
	
	/**
	 * Retrieves the password string to be used for report protection
	 * */
	public String getDocumentPassword() {
		String query = "{ CALL GET_DOCUMENT_PASSWORD(?) }";
		String pwd = null;
		
		try (Connection connection = DatabaseConnection.getMySQLConnection();
			 CallableStatement stmt = connection.prepareCall(query);) {
			stmt.setInt(1, policyId);
			ResultSet result = stmt.executeQuery();
			while (result.next()) {
				pwd = result.getString("password");
			}
			return pwd;
		} catch (SQLException e) {
			throw new ReportGenerationException("An error occurred while retrieving reports to be generated.", e);
		}
	}
}
