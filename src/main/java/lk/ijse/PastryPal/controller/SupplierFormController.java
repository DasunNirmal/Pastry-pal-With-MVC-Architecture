package lk.ijse.PastryPal.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import lk.ijse.PastryPal.model.SupplierModel;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class SupplierFormController {

    @FXML
    private TableColumn<?, ?> colDate;

    @FXML
    private TableColumn<?, ?> colName;

    @FXML
    private TableColumn<?, ?> colPhoneNumber;

    @FXML
    private TableColumn<?, ?> colSupplierID;

    @FXML
    private TableView<?> tblSuppliers;

    @FXML
    private Label lblSupplierID;

    @FXML
    private DatePicker txtDate;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtPhoneNumber;

    @FXML
    private TextField txtSearch;

    @FXML
    private Label lblTime;

    @FXML
    private Label lblDate;

    private SupplierModel supplierModel = new SupplierModel();

    public void initialize(){
        setDateAndTime();
        generateNextSupplierID();
    }

    private void generateNextSupplierID() {
        try {
            String previousSupplierID = lblSupplierID.getText();
            String supplierID = supplierModel.generateNextSupplierID();
            lblSupplierID.setText(supplierID);
            clearFields();
            if (btnClearIsPressed){
                lblSupplierID.setText(previousSupplierID);
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }

    private boolean btnClearIsPressed = false;
    @FXML
    void btnClearOnAction(ActionEvent event) {
        clearFields();
        generateNextSupplierID();
    }
    private void clearFields() {
        txtName.setText("");
        txtDate.setValue(null);
        txtSearch.setText("");
        txtPhoneNumber.setText("");
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

    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {

    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {

    }

    @FXML
    void txtSearchOnActon(ActionEvent actionEvent) {

    }

    @FXML
    void txtNameOnAction(ActionEvent event) {

    }

    @FXML
    void txtPhoneNumberOnAction(ActionEvent event) {

    }
}
