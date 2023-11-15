package lk.ijse.PastryPal.model;

import lk.ijse.PastryPal.DB.DbConnection;
import lk.ijse.PastryPal.dto.ItemDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ItemModel {

    private String splitItemID(String currentItemID){
        if (currentItemID != null){
            String [] split = currentItemID.split("00");

            int id = Integer.parseInt(split[1]);
            id++;
            return "I00" + id;
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

    public boolean saveItem(ItemDto dto) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "INSERT INTO items VALUES (?,?,?,?)";
        PreparedStatement ptsm = connection.prepareStatement(sql);
        ptsm.setString(1, dto.getItem_id());
        ptsm.setString(2, dto.getDescription());
        ptsm.setString(3, String.valueOf(dto.getQty()));
        ptsm.setString(4, String.valueOf(dto.getPrice()));

        return ptsm.executeUpdate() > 0;
    }
}
