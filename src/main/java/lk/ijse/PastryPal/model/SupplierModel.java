package lk.ijse.PastryPal.model;

import lk.ijse.PastryPal.DB.DbConnection;
import lk.ijse.PastryPal.dto.SupplierDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    public List<SupplierDto> getAllSuppliers() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT  * FROM suppliers";
        PreparedStatement ptsm = connection.prepareStatement(sql);
        ResultSet resultSet = ptsm.executeQuery();

        ArrayList<SupplierDto> dtoList = new ArrayList<>();

        while (resultSet.next()){
            dtoList.add(
                    new SupplierDto(
                            resultSet.getString(1),
                            resultSet.getString(2),
                            resultSet.getDate(3).toLocalDate(),
                            resultSet.getString(4)
                    )
            );
        }
        return dtoList;
    }

    public SupplierDto searchSupplierById(String searchId) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT * FROM suppliers WHERE supplier_id = ?";
        PreparedStatement ptsm = connection.prepareStatement(sql);
        ptsm.setString(1,searchId);
        ResultSet resultSet = ptsm.executeQuery();

        SupplierDto dto = null;
        if (resultSet.next()) {
            String supplier_id = resultSet.getString(1);
            String supplier_name = resultSet.getString(2);
            LocalDate supplier_date = LocalDate.parse(resultSet.getString(3));
            String supplier_phone_number = resultSet.getString(4);

            dto = new SupplierDto(supplier_id ,supplier_name, supplier_date ,supplier_phone_number);
        }
        return dto;
    }

    public SupplierDto searchSupplierByPhoneNumber(String searchPhoneNumber) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT * FROM suppliers WHERE phone_number = ?";
        PreparedStatement ptsm = connection.prepareStatement(sql);
        ptsm.setString(1,searchPhoneNumber);
        ResultSet resultSet = ptsm.executeQuery();

        SupplierDto dto = null;
        if (resultSet.next()){
            String supplier_id = resultSet.getString(1);
            String supplier_name = resultSet.getString(2);
            LocalDate supplier_date = LocalDate.parse(resultSet.getString(3));
            String supplier_phone_number = resultSet.getString(4);

            dto = new SupplierDto(supplier_id, supplier_name, supplier_date, supplier_phone_number);
        }
        return dto;
    }

    public boolean deleteSuppliers(String id) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "DELETE FROM suppliers WHERE supplier_id = ?";
        PreparedStatement ptsm = connection.prepareStatement(sql);
        ptsm.setString(1,id);

        return ptsm.executeUpdate() > 0;
    }

    public boolean updateSuppliers(SupplierDto dto) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "UPDATE suppliers SET name = ?,s_date = ?,phone_number = ? WHERE supplier_id = ?";
        PreparedStatement ptsm = connection.prepareStatement(sql);
        ptsm.setString(1, dto.getName());
        ptsm.setString(2, String.valueOf(dto.getDate()));
        ptsm.setString(3, dto.getPhone_number());
        ptsm.setString(4, dto.getSupplier_id());

        return ptsm.executeUpdate() > 0;
    }
}
