package lk.ijse.PastryPal.model;

import lk.ijse.PastryPal.DB.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

public class SupplierDetailsModel {

    public boolean saveDetails(String itemId, String sId) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        try {
            String sql = "INSERT INTO supplier_details VALUES (?,?)";
            try (PreparedStatement ptsm = connection.prepareStatement(sql)) {
                ptsm.setString(1, itemId);
                ptsm.setString(2, sId);

                return ptsm.executeUpdate() > 0;
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            // Log the exception or handle it appropriately
            e.printStackTrace();

            // Print more details about the error
            System.out.println("Error Details:");
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Message: " + e.getMessage());

            return false; // Indicate that the operation was not successful
        } catch (SQLException e) {
            // Handle other SQL exceptions
            e.printStackTrace();
            return false; // Indicate that the operation was not successful
        }
    }

}
