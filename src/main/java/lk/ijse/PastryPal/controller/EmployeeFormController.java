package lk.ijse.PastryPal.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import lk.ijse.PastryPal.dto.EmployeeDto;
import lk.ijse.PastryPal.model.EmployeeModel;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        String id = lblEmployeeID.getText();
        String first_name = txtFirstName.getText();
        String last_name = txtLastName.getText();
        String address = txtAddress.getText();
        String phone_number = txtPhoneNumber.getText();

        var dto = new EmployeeDto(id, first_name, last_name, address ,phone_number);
        try {
            boolean isSaved = employeeModel.saveEmployee(dto);
            if (isSaved){
                new Alert(Alert.AlertType.CONFIRMATION,"Employee is Saved").show();
                clearFields();
                generateNextEmployeeID();
            }else {
                new Alert(Alert.AlertType.ERROR,"Employee is not Saved").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {

    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {

    }

    @FXML
    void txtSearchOnAction(ActionEvent event) {

    }

}
