package lk.ijse.PastryPal.model;

import lk.ijse.PastryPal.DB.DbConnection;
import lk.ijse.PastryPal.dto.SupplierDto;
import org.apache.poi.hssf.record.DBCellRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SupplierModel {
    private String splitSupplierID(String currentSupplierID) {
        if (currentSupplierID != null){
            String [] split = currentSupplierID.split("00");

            int id = Integer.parseInt(split[1]);
            id++;
            return "S00" + id;
        }else {
            return "S001";
        }
    }

    public String generateNextSupplierID() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT supplier_id FROM suppliers ORDER BY supplier_id DESC LIMIT 1";
        PreparedStatement ptsm = connection.prepareStatement(sql);
        ResultSet resultSet = ptsm.executeQuery();
        if (resultSet.next()){
            return splitSupplierID(resultSet.getString(1));
        }
        return splitSupplierID(null);
    }

    public boolean saveSupplier(SupplierDto dto) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "INSERT INTO suppliers VALUES (?,?,?,?)";
        PreparedStatement ptsm = connection.prepareStatement(sql);
        ptsm.setString(1, dto.getSupplier_id());
        ptsm.setString(2, dto.getName());
        ptsm.setString(3, String.valueOf(dto.getDate()));
        ptsm.setString(4,dto.getPhone_number());

        return ptsm.executeUpdate() > 0;
    }
}
