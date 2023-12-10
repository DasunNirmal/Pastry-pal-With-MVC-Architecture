package lk.ijse.PastryPal.controller;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;
import lk.ijse.PastryPal.DB.DbConnection;
import lk.ijse.PastryPal.RegExPatterns.RegExPatterns;
import lk.ijse.PastryPal.dto.EmployeeDto;
import lk.ijse.PastryPal.dto.tm.EmployeeTm;
import lk.ijse.PastryPal.model.EmployeeModel;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
    private Label lblEmployeeCount;

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

    @FXML
    private Label lblEmployeeSaveOrNot;

    private EmployeeModel employeeModel = new EmployeeModel();
    private ObservableList<EmployeeTm> obList = FXCollections.observableArrayList();

    public void initialize() throws SQLException {
        setDateAndTime();
        generateNextEmployeeID();
        loadAllEmployees();
        setCellValueFactory();
        tableListener();
        totalEmployee();
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
        lblEmployeeSaveOrNot.setText("");
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
    private void totalEmployee() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();
        Statement statement = connection.createStatement();

        String sql = "SELECT count(*) FROM employee";
        ResultSet resultSet = statement.executeQuery(sql);
        resultSet.next();
        int count = resultSet.getInt(1);
        lblEmployeeCount.setText(String.valueOf(count));
    }
    private void tableListener() {
        tblEmployee.getSelectionModel().selectedItemProperty().addListener((observable, oldValued, newValue) -> {
            setData(newValue);
        });
    }
    private void setData(EmployeeTm row) {
        if (row != null) {
            lblEmployeeID.setText(row.getEmployee_id());
            txtFirstName.setText(row.getFirst_name());
            txtLastName.setText(row.getFirst_name());
            txtAddress.setText(row.getAddress());
            txtPhoneNumber.setText(row.getPhone_number());
        }
    }
    private void setCellValueFactory() {
        colEmployeeID.setCellValueFactory(new PropertyValueFactory<>("employee_id"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("first_name"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("last_name"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("phone_number"));
        tblEmployee.setId("my-table");
    }
    private void loadAllEmployees() {
        try {
            obList.clear();
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
                    obList.clear();
                    generateNextEmployeeID();
                    totalEmployee();
                    loadAllEmployees();
                    lblEmployeeSaveOrNot.setText("Employee is Saved !");
                    PauseTransition pause = new PauseTransition(Duration.seconds(2));
                    pause.setOnFinished(pauseEvent -> {
                        clearFields();
                    });
                    pause.play();
                }else {
                    lblEmployeeSaveOrNot.setText("Employee is Not Saved !");
                    PauseTransition pause = new PauseTransition(Duration.seconds(2));
                    pause.setOnFinished(pauseEvent -> {
                        lblEmployeeSaveOrNot.setText("");
                    });
                    pause.play();
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
            new Alert(Alert.AlertType.ERROR,"Can not Update Employee.First Name is empty").showAndWait();
            return;
        }if (!isValidLastName){
            new Alert(Alert.AlertType.ERROR,"Can not Update Employee.Last Name is empty").showAndWait();
            return;
        }if (!isValidAddress){
            new Alert(Alert.AlertType.ERROR,"Can not Update Employee.Address is empty").showAndWait();
            return;
        }if (!isValidPhone_Number){
            new Alert(Alert.AlertType.ERROR,"Can not Update Employee.Phone Number is empty").showAndWait();
        } else {
            var dto = new EmployeeDto(id, first_name, last_name, address ,phone_number);
            try {
                boolean isUpdated = employeeModel.updateEmployee(dto);
                if (isUpdated){
                    obList.clear();
                    totalEmployee();
                    generateNextEmployeeID();
                    loadAllEmployees();
                    lblEmployeeSaveOrNot.setText("Employee is Updated !");
                    PauseTransition pause = new PauseTransition(Duration.seconds(2));
                    pause.setOnFinished(pauseEvent -> {
                        clearFields();
                    });
                    pause.play();
                }else {
                    lblEmployeeSaveOrNot.setText("Employee is Not Updated !");
                    PauseTransition pause = new PauseTransition(Duration.seconds(2));
                    pause.setOnFinished(pauseEvent -> {
                        lblEmployeeSaveOrNot.setText("");
                    });
                    pause.play();
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
            new Alert(Alert.AlertType.ERROR,"Can not Delete Employee.First Name is empty").showAndWait();
            return;
        }if (!isValidLastName){
            new Alert(Alert.AlertType.ERROR,"Can not Delete Employee.Last Name is empty").showAndWait();
            return;
        }if (!isValidAddress){
            new Alert(Alert.AlertType.ERROR,"Can not Delete Employee.Address is empty").showAndWait();
            return;
        }if (!isValidPhone_Number){
            new Alert(Alert.AlertType.ERROR,"Can not Delete Employee.Phone Number is empty").showAndWait();
        } else {
            try {
                boolean isDeleted = employeeModel.deleteEmployee(id);
                if (isDeleted){
                    obList.clear();
                    generateNextEmployeeID();
                    loadAllEmployees();
                    totalEmployee();
                    lblEmployeeSaveOrNot.setText("Employee is Deleted !");
                    PauseTransition pause = new PauseTransition(Duration.seconds(2));
                    pause.setOnFinished(pauseEvent -> {
                        clearFields();
                    });
                    pause.play();
                }else {
                    lblEmployeeSaveOrNot.setText("Employee Not is Deleted !");
                    PauseTransition pause = new PauseTransition(Duration.seconds(2));
                    pause.setOnFinished(pauseEvent -> {
                        lblEmployeeSaveOrNot.setText("");
                    });
                    pause.play();
                }
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
            }
        }
    }
    @FXML
    void txtSearchOnAction(KeyEvent event) {
        searchTableFilter();
    }
    private void searchTableFilter() {
        FilteredList<EmployeeTm> filterEmployeeTbl = new FilteredList<>(obList, b -> true);
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filterEmployeeTbl.setPredicate(employeeTm -> {
                if (newValue.isEmpty() || newValue.isBlank() || newValue == null) {
                    return true;
                }
                String search = newValue.toLowerCase();

                if (employeeTm.getEmployee_id().toLowerCase().contains(search)) {
                    return true;
                } else if (employeeTm.getEmployee_id().toLowerCase().contains(search)) {
                    return true;
                } else if (employeeTm.getFirst_name().toLowerCase().contains(search)) {
                    return true;
                } else if (employeeTm.getFirst_name().toLowerCase().contains(search)) {
                    return true;
                } else if (employeeTm.getAddress().toLowerCase().contains(search)) {
                    return true;
                } else if (employeeTm.getAddress().toLowerCase().contains(search)) {
                    return true;
                } else if (employeeTm.getPhone_number().toLowerCase().contains(search)) {
                    return true;
                } else if (employeeTm.getPhone_number().toLowerCase().contains(search)) {
                    return true;
                } else
                    return false;
            });
        });
        SortedList<EmployeeTm> sortCustomerTbl = new SortedList<>(filterEmployeeTbl);
        sortCustomerTbl.comparatorProperty().bind(tblEmployee.comparatorProperty());
        tblEmployee.setItems(sortCustomerTbl);
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
