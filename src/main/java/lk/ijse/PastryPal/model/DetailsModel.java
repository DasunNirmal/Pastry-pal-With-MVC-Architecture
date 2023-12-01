package lk.ijse.PastryPal.model;

import lk.ijse.PastryPal.DB.DbConnection;
import lk.ijse.PastryPal.dto.DetailsDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DetailsModel {
    public List<DetailsDto> getAllDetails() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT \n" +
                "    o.order_id,\n" +
                "    o.customer_id,\n" +
                "    o.order_date,\n" +
                "    d.product_id,\n" +
                "    p.description,\n" +
                "    d.qty,\n" +
                "    d.unit_price \n" +
                "FROM \n" +
                "    orders o\n" +
                "JOIN \n" +
                "    order_details d ON o.order_id = d.order_id\n" +
                "JOIN \n" +
                "    products p ON d.product_id = p.product_id\n" +
                "ORDER BY \n" +
                "    o.order_id ASC;";
        PreparedStatement ptsm = connection.prepareStatement(sql);
        ResultSet resultSet = ptsm.executeQuery();

        ArrayList<DetailsDto> dtoList = new ArrayList<>();

        while (resultSet.next()) {
            dtoList.add(
                    new DetailsDto(
                        resultSet.getString(1),
                        resultSet.getString(2),
                            resultSet.getDate(3).toLocalDate(),
                        resultSet.getString(4),
                        resultSet.getString(5),
                        resultSet.getInt(6),
                        resultSet.getDouble(7)
                    )
            );
        }
        return dtoList;
    }
}
