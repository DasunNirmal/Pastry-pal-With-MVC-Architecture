package lk.ijse.PastryPal.model;

import lk.ijse.PastryPal.DB.DbConnection;
import lk.ijse.PastryPal.dto.tm.OrderTm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class OrderDetailModel {
    public boolean saveOrderDetails(String orderId, List<OrderTm> orderTmList) throws SQLException {
        for (OrderTm tm : orderTmList){
            if (!saveOrderDetails(orderId, tm)){
                return false;
            }
        }
        return true;
    }

    private boolean saveOrderDetails(String orderId,OrderTm orderTm) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "INSERT INTO order_details VALUES (?,?,?,?)";
        PreparedStatement ptsm = connection.prepareStatement(sql);

        ptsm.setString(1,orderId);
        ptsm.setString(2,orderTm.getProduct_id());
        ptsm.setString(3, String.valueOf(orderTm.getQty()));
        ptsm.setString(4, String.valueOf(orderTm.getUnit_price()));
        return ptsm.executeUpdate() > 0;
    }
}
