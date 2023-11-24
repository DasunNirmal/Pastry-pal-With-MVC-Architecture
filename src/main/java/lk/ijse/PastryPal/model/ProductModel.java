package lk.ijse.PastryPal.model;

import lk.ijse.PastryPal.DB.DbConnection;
import lk.ijse.PastryPal.dto.ProductDto;
import lk.ijse.PastryPal.dto.tm.OrderTm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductModel {
    private String splitItemID(String currentItemID){
        if (currentItemID != null){
            String [] split = currentItemID.split("[P]");

            int id = Integer.parseInt(split[1]);
            id++;
            return String.format("P%03d",id);
        }else {
            return "P001";
        }
    }

    public String generateNextItemID() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT product_id FROM products ORDER BY product_id DESC LIMIT 1";
        PreparedStatement ptsm = connection.prepareStatement(sql);
        ResultSet resultSet = ptsm.executeQuery();
        if (resultSet.next()){
            return splitItemID(resultSet.getString(1));
        }
        return splitItemID(null);
    }

    public boolean saveProduct(ProductDto dto) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "INSERT INTO products VALUES (?,?,?,?)";
        PreparedStatement ptsm = connection.prepareStatement(sql);
        ptsm.setString(1, dto.getProduct_id());
        ptsm.setString(2, dto.getDescription());
        ptsm.setString(3, String.valueOf(dto.getQty()));
        ptsm.setString(4, String.valueOf(dto.getPrice()));

        return ptsm.executeUpdate() > 0;
    }

    public boolean updateProducts(ProductDto dto) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "UPDATE products SET description = ?, qty = ?, price = ? WHERE product_id = ?";
        PreparedStatement ptsm = connection.prepareStatement(sql);
        ptsm.setString(1,dto.getDescription());
        ptsm.setString(2, String.valueOf(dto.getQty()));
        ptsm.setString(3, String.valueOf(dto.getPrice()));
        ptsm.setString(4, dto.getProduct_id());

        return ptsm.executeUpdate() > 0;
    }

    public ProductDto searchProductById(String searchId) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT * FROM products WHERE product_id = ?";
        PreparedStatement ptsm = connection.prepareStatement(sql);
        ptsm.setString(1,searchId);
        ResultSet resultSet = ptsm.executeQuery();

        ProductDto dto = null;
        if (resultSet.next()){
            String Product_id = resultSet.getString(1);
            String Product_description = resultSet.getString(2);
            double Product_qty = Double.parseDouble(resultSet.getString(3));
            double Product_price = Double.parseDouble(resultSet.getString(4));

            dto = new ProductDto(Product_id, Product_description, Product_qty ,Product_price);
        }
        return dto;
    }

    public ProductDto searchProductByName(String searchName) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT * FROM products WHERE description = ?";
        PreparedStatement ptsm = connection.prepareStatement(sql);
        ptsm.setString(1,searchName);
        ResultSet resultSet = ptsm.executeQuery();

        ProductDto dto = null;
        if (resultSet.next()){
            String Product_Id = resultSet.getString(1);
            String Product_description = resultSet.getString(2);
            double Product_qty = Double.parseDouble(resultSet.getString(3));
            double Product_price = Double.parseDouble(resultSet.getString(4));

            dto = new ProductDto(Product_Id, Product_description, Product_qty, Product_price);
        }
        return  dto;
    }

    public boolean deleteProduct(String itemId) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "DELETE FROM products WHERE product_id = ?";
        PreparedStatement ptsm = connection.prepareStatement(sql);
        ptsm.setString(1,itemId);
        return ptsm.executeUpdate() > 0;
    }

    public List<ProductDto> getAllProducts() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT * FROM products";
        PreparedStatement ptsm = connection.prepareStatement(sql);
        ResultSet resultSet = ptsm.executeQuery();

        ArrayList<ProductDto> dtoList = new ArrayList<>();

        while (resultSet.next()){
            dtoList.add(
                    new ProductDto(
                            resultSet.getString(1),
                            resultSet.getString(2),
                            resultSet.getDouble(3),
                            resultSet.getDouble(4)
                    )
            );
        }
        return dtoList;
    }

    public boolean updateProduct(List<OrderTm> orderTmList) throws SQLException {
        for (OrderTm tm: orderTmList){
            if (!updateQty(tm.getProduct_id(),tm.getQty())){
                return false;
            }
        }
        return true;
    }

    private boolean updateQty(String productId, int qty) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "UPDATE products SET qty = qty - ? WHERE product_id = ?";
        PreparedStatement ptsm = connection.prepareStatement(sql);
        ptsm.setDouble(1,qty);
        ptsm.setString(2,productId);

        return ptsm.executeUpdate() > 0;
    }
}
