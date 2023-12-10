package lk.ijse.PastryPal.model;

import lk.ijse.PastryPal.DB.DbConnection;
import lk.ijse.PastryPal.dto.ItemDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemModel {
    private String splitItemID(String currentItemID){
        if (currentItemID != null){
            String [] split = currentItemID.split("[I]");

            int id = Integer.parseInt(split[1]);
            id++;
            return String.format("I%03d",id);
        }else {
            return "I001";
        }
    }
    public String generateNextItemID() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT item_id FROM items ORDER BY item_id DESC LIMIT 1";
        PreparedStatement ptsm = connection.prepareStatement(sql);
        ResultSet resultSet = ptsm.executeQuery();
        if (resultSet.next()){
            return splitItemID(resultSet.getString(1));
        }
        return splitItemID(null);
    }

    public boolean saveItems(ItemDto dto) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "INSERT INTO items VALUES (?,?,?,?,?,?)";
        PreparedStatement ptsm = connection.prepareStatement(sql);
        ptsm.setString(1,dto.getItem_id());
        ptsm.setString(2,dto.getProduct_name());
        ptsm.setString(3, String.valueOf(dto.getQty()));
        ptsm.setString(4,dto.getSupplier_id());
        ptsm.setString(5,dto.getSupplier_name());
        ptsm.setString(6, String.valueOf(dto.getSupplier_phone_number()));

        return ptsm.executeUpdate() > 0;
    }

    public boolean deleteItems(String id) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "DELETE FROM items WHERE item_id = ?";
        PreparedStatement ptsm = connection.prepareStatement(sql);
        ptsm.setString(1,id);

        return ptsm.executeUpdate() > 0;
    }

    public boolean updateItems(ItemDto dto) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "UPDATE items SET product_name = ?, qty = ?, supplier_id = ?, supplier_name = ?, supplier_phone_number = ? WHERE item_id = ?";
        PreparedStatement ptsm = connection.prepareStatement(sql);
        ptsm.setString(1, dto.getProduct_name());
        ptsm.setString(2, String.valueOf(dto.getQty()));
        ptsm.setString(3,dto.getSupplier_id());
        ptsm.setString(4,dto.getSupplier_name());
        ptsm.setString(5, String.valueOf(dto.getSupplier_phone_number()));
        ptsm.setString(6,dto.getItem_id());

        return ptsm.executeUpdate() > 0;
    }

    public List<ItemDto> getAllItems() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT * FROM items";
        PreparedStatement ptsm = connection.prepareStatement(sql);
        ResultSet resultSet = ptsm.executeQuery();

        ArrayList<ItemDto> dtoList = new ArrayList<>();

        while (resultSet.next()){
            dtoList.add(
                    new ItemDto(
                            resultSet.getString(1),
                            resultSet.getString(2),
                            resultSet.getDouble(3),
                            resultSet.getString(4),
                            resultSet.getString(5),
                            resultSet.getString(6)
                    )
            );
        }
        return dtoList;
    }
}
