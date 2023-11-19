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

    public boolean updateItems(ItemDto dto) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "UPDATE items SET description = ?, qty = ?, price = ? WHERE item_id = ?";
        PreparedStatement ptsm = connection.prepareStatement(sql);
        ptsm.setString(1,dto.getDescription());
        ptsm.setString(2, String.valueOf(dto.getQty()));
        ptsm.setString(3, String.valueOf(dto.getPrice()));
        ptsm.setString(4, dto.getItem_id());

        return ptsm.executeUpdate() > 0;
    }

    public ItemDto searchItemById(String searchId) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT * FROM items WHERE item_id = ?";
        PreparedStatement ptsm = connection.prepareStatement(sql);
        ptsm.setString(1,searchId);
        ResultSet resultSet = ptsm.executeQuery();

        ItemDto dto = null;
        if (resultSet.next()){
            String Item_id = resultSet.getString(1);
            String Item_description = resultSet.getString(2);
            double Item_qty = Double.parseDouble(resultSet.getString(3));
            double Item_price = Double.parseDouble(resultSet.getString(4));

            dto = new ItemDto(Item_id, Item_description, Item_qty ,Item_price);
        }
        return dto;
    }

    public ItemDto searchItemByName(String searchName) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT * FROM items WHERE description = ?";
        PreparedStatement ptsm = connection.prepareStatement(sql);
        ptsm.setString(1,searchName);
        ResultSet resultSet = ptsm.executeQuery();

        ItemDto dto = null;
        if (resultSet.next()){
            String Item_Id = resultSet.getString(1);
            String Item_description = resultSet.getString(2);
            double Item_qty = Double.parseDouble(resultSet.getString(3));
            double Item_price = Double.parseDouble(resultSet.getString(4));

            dto = new ItemDto(Item_Id, Item_description, Item_qty, Item_price);
        }
        return  dto;
    }

    public boolean deleteItems(String itemId) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "DELETE FROM items WHERE item_id = ?";
        PreparedStatement ptsm = connection.prepareStatement(sql);
        ptsm.setString(1,itemId);
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
                            resultSet.getDouble(4)
                    )
            );
        }
        return dtoList;
    }
}
