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
import lk.ijse.PastryPal.dto.CustomerDto;
import lk.ijse.PastryPal.model.CustomerModel;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class CustomerFormController {
    @FXML
    private TableColumn<?, ?> colAddress;

    @FXML
    private TableColumn<?, ?> colCustomerID;

    @FXML
    private TableColumn<?, ?> colCustomerName;

    @FXML
    private TableColumn<?, ?> colPoneNumber;

    @FXML
    private Label lblCustomerId;

    @FXML
    private TextField txtCustomerAddress;

    @FXML
    private TextField txtCustomerName;

    @FXML
    private TextField txtPhoneNumber;

    @FXML
    private Label lblDate;

    @FXML
    private Label lblTime;

    private CustomerModel customerModel = new CustomerModel();

    public void initialize(){
        setDateAndTime();
        generateNextCustomerID();
    }

    private void  generateNextCustomerID(){
        try {
            String previousCustomerID = lblCustomerId.getText();
            String customerID = customerModel.generateNextCustomer();
            lblCustomerId.setText(customerID);
            clearFields();
            if (btnClearPressed){
                lblCustomerId.setText(previousCustomerID);
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    private boolean btnClearPressed = false;

    @FXML
    void btnClearOnAction(ActionEvent event) {
        clearFields();
        generateNextCustomerID();
    }

    private void clearFields(){
        txtCustomerName.setText("");
        txtCustomerAddress.setText("");
        txtPhoneNumber.setText("");
    }

    private void setDateAndTime() {
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
        String id = lblCustomerId.getText();
        String name = txtCustomerName.getText();
        String address = txtCustomerAddress.getText();
        int phoneNumber = Integer.parseInt(txtPhoneNumber.getText());

        var dto = new CustomerDto(id,name,address,phoneNumber);
        try {
            boolean isSaved = customerModel.save(dto);
            if (isSaved){
                new Alert(Alert.AlertType.CONFIRMATION,"Customer is Saved").show();
                clearFields();
                generateNextCustomerID();
            }else {
                new Alert(Alert.AlertType.ERROR,"Customer is Not Saved").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {

    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {

    }

    @FXML
    void txtGoToAddressOnAction(ActionEvent event) {
        txtCustomerAddress.requestFocus();
    }

    @FXML
    void txtGoToPhoneNumberOnAction(ActionEvent event) {
        txtPhoneNumber.requestFocus();
    }

    @FXML
    void txtSaveOnAction(ActionEvent event) {
        btnSaveOnAction(new ActionEvent());
    }
}
