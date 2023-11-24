package lk.ijse.PastryPal.model;

import lk.ijse.PastryPal.DB.DbConnection;
import lk.ijse.PastryPal.dto.EmployeeDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeModel {
    private String splitEmployeeID(String currentEmployeeID){
        if (currentEmployeeID != null){
            String [] split = currentEmployeeID.split("[E]");

            int id = Integer.parseInt(split[1]);
            id++;
            return String.format("E%03d",id);
        }else {
            return "E001";
        }
    }
    public String generateNextEmployeeID() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT employee_id FROM employee ORDER BY employee_id DESC LIMIT 1";
        PreparedStatement ptsm = connection.prepareStatement(sql);
        ResultSet resultSet = ptsm.executeQuery();
        if (resultSet.next()){
            return splitEmployeeID(resultSet.getString(1));
        }
        return splitEmployeeID(null);
    }

    public boolean saveEmployee(EmployeeDto dto) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "INSERT INTO employee VALUES (?,?,?,?,?)";
        PreparedStatement ptsm = connection.prepareStatement(sql);
        ptsm.setString(1, dto.getEmployee_id());
        ptsm.setString(2, dto.getFirst_name());
        ptsm.setString(3,dto.getLast_name());
        ptsm.setString(4,dto.getAddress());
        ptsm.setString(5, dto.getPhone_number());

        return ptsm.executeUpdate() > 0;
    }

    public EmployeeDto searchEmployeeByID(String searchID) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT * FROM employee WHERE employee_id = ?";
        PreparedStatement ptsm = connection.prepareStatement(sql);
        ptsm.setString(1,searchID);
        ResultSet resultSet = ptsm.executeQuery();

        EmployeeDto dto = null;
        if (resultSet.next()){
            String Employee_id = resultSet.getString(1);
            String Employee_first_name = resultSet.getString(2);
            String Employee_last_name = resultSet.getString(3);
            String Employee_address = resultSet.getString(4);
            String Employee_phone_number = resultSet.getString(5);

            dto = new EmployeeDto(Employee_id, Employee_first_name, Employee_last_name, Employee_address,
                    Employee_phone_number);
        }
        return dto;
    }

    public EmployeeDto searchEmployeeByPhoneNumber(String searchPhoneNumber) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT * FROM employee WHERE phone_number = ?";
        PreparedStatement ptsm = connection.prepareStatement(sql);
        ptsm.setString(1,searchPhoneNumber);
        ResultSet resultSet = ptsm.executeQuery();

        EmployeeDto dto = null;
        if (resultSet.next()){
            String Employee_id = resultSet.getString(1);
            String Employee_first_name = resultSet.getString(2);
            String Employee_last_name = resultSet.getString(3);
            String Employee_address = resultSet.getString(4);
            String Employee_phone_number = resultSet.getString(5);

            dto = new EmployeeDto(Employee_id, Employee_first_name, Employee_last_name, Employee_address,
                    Employee_phone_number);
        }
        return dto;
    }

    public boolean updateEmployee(EmployeeDto dto) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql ="UPDATE employee SET first_name = ?,last_name = ?,address = ?,phone_number = ? WHERE employee_id = ?";
        PreparedStatement ptsm = connection.prepareStatement(sql);
        ptsm.setString(1, dto.getFirst_name());
        ptsm.setString(2, dto.getLast_name());
        ptsm.setString(3, dto.getAddress());
        ptsm.setString(4,dto.getPhone_number());
        ptsm.setString(5, dto.getEmployee_id());

        return ptsm.executeUpdate() > 0;
    }

    public boolean deleteEmployee(String id) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "DELETE FROM employee WHERE employee_id = ?";
        PreparedStatement ptsm = connection.prepareStatement(sql);
        ptsm.setString(1,id);
        return ptsm.executeUpdate() > 0;
    }

    public List<EmployeeDto> getAllEmployees() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT * FROM employee";
        PreparedStatement ptsm = connection.prepareStatement(sql);
        ResultSet resultSet = ptsm.executeQuery();

        ArrayList<EmployeeDto> dtoList = new ArrayList<>();

        while (resultSet.next()){
            dtoList.add(
                    new EmployeeDto(
                            resultSet.getString(1),
                            resultSet.getString(2),
                            resultSet.getString(3),
                            resultSet.getString(4),
                            resultSet.getString(5)
                    )
            );
        }
        return dtoList;
    }
}
