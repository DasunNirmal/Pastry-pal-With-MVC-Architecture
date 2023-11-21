package lk.ijse.PastryPal.controller;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import lk.ijse.PastryPal.RegExPatterns.RegExPatterns;
import lk.ijse.PastryPal.dto.EmployeeDto;
import lk.ijse.PastryPal.dto.tm.CustomerTm;
import lk.ijse.PastryPal.dto.tm.EmployeeTm;
import lk.ijse.PastryPal.model.EmployeeModel;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EmployeeFormController {
    @FXML
    private TableColumn<?, ?> colAddress;

    @FXML
    private TableColumn<?, ?> colEmployeeID;

    @FXML
    private TableColumn<?, ?> colFirstName;

    @FXML
    private TableColumn<?, ?> colLastName;

    @FXML
    private TableColumn<?, ?> colPhoneNumber;

    @FXML
    private TableView<EmployeeTm> tblEmployee;

    @FXML
    private TextField txtAddress;

    @FXML
    private Label lblDate;

    @FXML
    private Label lblTime;

    @FXML
    private Label lblEmployeeID;

    @FXML
    private TextField txtFirstName;

    @FXML
    private TextField txtLastName;

    @FXML
    private TextField txtPhoneNumber;

    @FXML
    private TextField txtSearch;

    private EmployeeModel employeeModel = new EmployeeModel();

    public void initialize(){
        setDateAndTime();
        generateNextEmployeeID();
        loadAllEmployees();
        setCellValueFactory();
    }
    private void generateNextEmployeeID() {
        try {
            String previousEmployeeID = lblEmployeeID.getId();
            String employeeID = employeeModel.generateNextEmployeeID();
            lblEmployeeID.setText(employeeID);
            if (btnClearPressed){
                lblEmployeeID.setText(previousEmployeeID);
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }
    private boolean btnClearPressed = false;
    @FXML
    void btnClearOnAction(ActionEvent event) {
        clearFields();
        generateNextEmployeeID();
    }
    private void clearFields(){
        txtFirstName.setText("");
        txtLastName.setText("");
        txtAddress.setText("");
        txtPhoneNumber.setText("");
        txtSearch.setText("");
    }
    private void setDateAndTime(){
        Platform.runLater(() -> {
            lblDate.setText(String.valueOf(LocalDate.now()));

            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1), event -> {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss");
                String timeNow = LocalTime.now().format(formatter);
                lblTime.setText(timeNow);
            }));
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();
        });
    }
    private void setCellValueFactory() {
        colEmployeeID.setCellValueFactory(new PropertyValueFactory<>("employee_id"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("first_name"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("last_name"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("phone_number"));
    }
    private void loadAllEmployees() {
        ObservableList<EmployeeTm> obList = FXCollections.observableArrayList();
        try {
            List<EmployeeDto> dtoList = employeeModel.getAllEmployees();
            for (EmployeeDto dto: dtoList ) {
                obList.add(
                        new EmployeeTm(
                                dto.getEmployee_id(),
                                dto.getFirst_name(),
                                dto.getLast_name(),
                                dto.getAddress(),
                                dto.getPhone_number()
                        )
                );
            }
            tblEmployee.setItems(obList);
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        String id = lblEmployeeID.getText();
        String first_name = txtFirstName.getText();
        String last_name = txtLastName.getText();
        String address = txtAddress.getText();
        String phone_number = txtPhoneNumber.getText();

        boolean isValidFirstName = RegExPatterns.getValidName().matcher(first_name).matches();
        boolean isValidLastName = RegExPatterns.getValidName().matcher(last_name).matches();
        boolean isValidAddress = RegExPatterns.getValidAddress().matcher(address).matches();
        boolean isValidPhone_Number = RegExPatterns.getValidPhoneNumber().matcher(phone_number).matches();

        if (!isValidFirstName){
            new Alert(Alert.AlertType.ERROR,"Can nor Save Employee.First Name is empty").showAndWait();
            return;
        }if (!isValidLastName){
            new Alert(Alert.AlertType.ERROR,"Can nor Save Employee.Last Name is empty").showAndWait();
            return;
        }if (!isValidAddress){
            new Alert(Alert.AlertType.ERROR,"Can nor Save Employee.Address is empty").showAndWait();
            return;
        }if (!isValidPhone_Number){
            new Alert(Alert.AlertType.ERROR,"Can nor Save Employee.Phone Number is empty").showAndWait();
        }else {
            var dto = new EmployeeDto(id, first_name, last_name, address ,phone_number);
            try {
                boolean isSaved = employeeModel.saveEmployee(dto);
                if (isSaved){
                    new Alert(Alert.AlertType.CONFIRMATION,"Employee is Saved").show();
                    clearFields();
                    generateNextEmployeeID();
                    loadAllEmployees();
                }else {
                    new Alert(Alert.AlertType.ERROR,"Employee is not Saved").show();
                }
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
            }
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        String id = lblEmployeeID.getText();
        String first_name = txtFirstName.getText();
        String last_name = txtLastName.getText();
        String address = txtAddress.getText();
        String phone_number = txtPhoneNumber.getText();

        boolean isValidFirstName = RegExPatterns.getValidName().matcher(first_name).matches();
        boolean isValidLastName = RegExPatterns.getValidName().matcher(last_name).matches();
        boolean isValidAddress = RegExPatterns.getValidAddress().matcher(address).matches();
        boolean isValidPhone_Number = RegExPatterns.getValidPhoneNumber().matcher(phone_number).matches();

        if (!isValidFirstName){
            new Alert(Alert.AlertType.ERROR,"Can nor Update Employee.First Name is empty").showAndWait();
            return;
        }if (!isValidLastName){
            new Alert(Alert.AlertType.ERROR,"Can nor Update Employee.Last Name is empty").showAndWait();
            return;
        }if (!isValidAddress){
            new Alert(Alert.AlertType.ERROR,"Can nor Update Employee.Address is empty").showAndWait();
            return;
        }if (!isValidPhone_Number){
            new Alert(Alert.AlertType.ERROR,"Can nor Update Employee.Phone Number is empty").showAndWait();
        } else {
            var dto = new EmployeeDto(id, first_name, last_name, address ,phone_number);
            try {
                boolean isUpdated = employeeModel.updateEmployee(dto);
                if (isUpdated){
                    new Alert(Alert.AlertType.CONFIRMATION,"Employee is Updated").show();
                    clearFields();
                    generateNextEmployeeID();
                    loadAllEmployees();
                }else {
                    new Alert(Alert.AlertType.ERROR,"Employee is not Updated").show();
                }
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
            }
        }
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        String id = lblEmployeeID.getText();
        String first_name = txtFirstName.getText();
        String last_name = txtLastName.getText();
        String address = txtAddress.getText();
        String phone_number = txtPhoneNumber.getText();

        boolean isValidFirstName = RegExPatterns.getValidName().matcher(first_name).matches();
        boolean isValidLastName = RegExPatterns.getValidName().matcher(last_name).matches();
        boolean isValidAddress = RegExPatterns.getValidAddress().matcher(address).matches();
        boolean isValidPhone_Number = RegExPatterns.getValidPhoneNumber().matcher(phone_number).matches();

        if (!isValidFirstName){
            new Alert(Alert.AlertType.ERROR,"Can nor Delete Employee.First Name is empty").showAndWait();
            return;
        }if (!isValidLastName){
            new Alert(Alert.AlertType.ERROR,"Can nor Delete Employee.Last Name is empty").showAndWait();
            return;
        }if (!isValidAddress){
            new Alert(Alert.AlertType.ERROR,"Can nor Delete Employee.Address is empty").showAndWait();
            return;
        }if (!isValidPhone_Number){
            new Alert(Alert.AlertType.ERROR,"Can nor Delete Employee.Phone Number is empty").showAndWait();
        } else {
            try {
                boolean isDeleted = employeeModel.deleteEmployee(id);
                if (isDeleted){
                    new Alert(Alert.AlertType.CONFIRMATION,"Employee is Deleted").show();
                    clearFields();
                    generateNextEmployeeID();
                    loadAllEmployees();
                }else {
                    new Alert(Alert.AlertType.ERROR,"Employee is Not Deleted").show();
                }
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
            }
        }
    }

    @FXML
    void txtSearchOnAction(ActionEvent event) {
        String searchInput = txtSearch.getText();

        try {
            EmployeeDto employeeDto;
            if (searchInput.matches("\\d+")){
                employeeDto = employeeModel.searchEmployeeByPhoneNumber(searchInput);
            }else {
                employeeDto = employeeModel.searchEmployeeByID(searchInput);
            }
            if (employeeDto != null){
                lblEmployeeID.setText(employeeDto.getEmployee_id());
                txtFirstName.setText(employeeDto.getFirst_name());
                txtLastName.setText(employeeDto.getLast_name());
                txtAddress.setText(employeeDto.getAddress());
                txtPhoneNumber.setText(employeeDto.getPhone_number());
                txtSearch.setText("");
            }else {
                lblEmployeeID.setText("");
                generateNextEmployeeID();
                new Alert(Alert.AlertType.INFORMATION,"Employee not found").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }
    @FXML
    void txtGoToLastNameOnAction(ActionEvent event) {
        txtLastName.requestFocus();
    }
    @FXML
    void GoToPhoneNumberOnAction(ActionEvent event) {
        txtPhoneNumber.requestFocus();
    }
    @FXML
    void btnGoToAddressOnAction(ActionEvent event) {
        txtAddress.requestFocus();
    }
    @FXML
    void txtSaveOnAction(ActionEvent event) {
        btnSaveOnAction(new ActionEvent());
    }
}
