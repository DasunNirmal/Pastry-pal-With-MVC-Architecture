package lk.ijse.PastryPal.model;

import lk.ijse.PastryPal.DB.DbConnection;
import lk.ijse.PastryPal.dto.ComplainDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ComplainModel {
    private String splitComplainID(String currentComplainID){
        if (currentComplainID != null){
            String [] split = currentComplainID.split("00");

            int id = Integer.parseInt(split[1]);
            id++;
            return "CM00" + id;
        }else {
            return "CM001";
        }
    }
    public String generateNextComplainID() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT complain_id FROM complains ORDER BY complain_id DESC LIMIT 1";
        PreparedStatement ptsm = connection.prepareStatement(sql);
        ResultSet resultSet = ptsm.executeQuery();
        if (resultSet.next()){
            return splitComplainID(resultSet.getString(1));
        }
        return splitComplainID(null);
    }

    public boolean saveComplain(ComplainDto dto) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "INSERT INTO complains VALUES (?,?,?)";
        PreparedStatement ptsm = connection.prepareStatement(sql);
        ptsm.setString(1,dto.getComplain_id());
        ptsm.setString(2,dto.getDescription());
        ptsm.setString(3, String.valueOf(dto.getComplain_date()));

        return ptsm.executeUpdate() > 0;
    }

    public boolean updateComplains(ComplainDto dto) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "UPDATE complains SET description = ?,complain_date = ? WHERE complain_id = ?";
        PreparedStatement ptsm = connection.prepareStatement(sql);
        ptsm.setString(1, dto.getDescription());
        ptsm.setString(2, String.valueOf(dto.getComplain_date()));
        ptsm.setString(3, dto.getComplain_id());

        return  ptsm.executeUpdate() > 0;
    }

    public ComplainDto searchComplainByID(String searchId) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT * FROM complains WHERE complain_id = ?";
        PreparedStatement ptsm = connection.prepareStatement(sql);
        ptsm.setString(1,searchId);
        ResultSet resultSet = ptsm.executeQuery();

        ComplainDto dto = null;
        if (resultSet.next()){
            String Complain_id = resultSet.getString(1);
            String Complain_description = resultSet.getString(2);
            LocalDate Complain_date = LocalDate.parse(resultSet.getString(3));

            dto = new ComplainDto(Complain_id, Complain_description, Complain_date);
        }
        return dto;
    }

    public boolean deleteComplains(String id) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "DELETE FROM complains WHERE complain_id = ?";
        PreparedStatement ptsm = connection.prepareStatement(sql);
        ptsm.setString(1,id);
        return ptsm.executeUpdate() > 0;
    }

    public List<ComplainDto> getAllComplains() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT * FROM complains";
        PreparedStatement ptsm = connection.prepareStatement(sql);
        ResultSet resultSet = ptsm.executeQuery();

        ArrayList<ComplainDto> dtoList = new ArrayList<>();
        while (resultSet.next()){
            dtoList.add(
                    new ComplainDto(
                            resultSet.getString(1),
                            resultSet.getString(2),
                            resultSet.getDate(3).toLocalDate()
                    )
            );
        }
        return dtoList;
    }
}
