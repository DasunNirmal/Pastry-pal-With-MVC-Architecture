package lk.ijse.PastryPal.model;

import lk.ijse.PastryPal.DB.DbConnection;
import lk.ijse.PastryPal.dto.CustomerDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerModel {

    private String splitCustomerID(String currentCustomerID){
        if (currentCustomerID != null){
            String [] split = currentCustomerID.split("00");

            int id = Integer.parseInt(split[1]);
            id++;
            return "C00" + id;
        }else {
            return "C001";
        }
    }

    public String generateNextCustomer() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT customer_id FROM customer ORDER BY customer_id DESC LIMIT 1";
        PreparedStatement ptsm = connection.prepareStatement(sql);
        ResultSet resultSet = ptsm.executeQuery();
        if (resultSet.next()){
            return splitCustomerID(resultSet.getString(1));
        }
        return splitCustomerID(null);
    }

    public boolean save(CustomerDto dto) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "INSERT INTO customer VALUES (?,?,?,?)";
        PreparedStatement ptsm = connection.prepareStatement(sql);

        ptsm.setString(1,dto.getCustomer_id());
        ptsm.setString(2,dto.getName());
        ptsm.setString(3,dto.getAddress());
        ptsm.setString(4, String.valueOf(dto.getPhone_number()));

        return ptsm.executeUpdate() > 0;
    }
}
