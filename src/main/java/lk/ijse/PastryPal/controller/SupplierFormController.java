package lk.ijse.PastryPal.controller;

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
import lk.ijse.PastryPal.dto.SupplierDto;
import lk.ijse.PastryPal.dto.tm.SupplierTm;
import lk.ijse.PastryPal.model.SupplierModel;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
    private TableView<SupplierTm> tblSuppliers;

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
        setValueFactory();
        setDateAndTime();
        generateNextSupplierID();
        loadAllSuppliers();
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

    private void setValueFactory() {
        colSupplierID.setCellValueFactory(new PropertyValueFactory<>("supplier_id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("phone_number"));
        tblSuppliers.setId("my-table");
    }

    private void loadAllSuppliers() {
        var model = new SupplierModel();

        ObservableList<SupplierTm> obList = FXCollections.observableArrayList();
        try {
            List<SupplierDto> dtoList = model.getAllSuppliers();
            for (SupplierDto dto : dtoList){
                obList.add(
                        new SupplierTm(
                                dto.getSupplier_id(),
                                dto.getName(),
                                dto.getDate(),
                                dto.getPhone_number()
                        )
                );
            }
            tblSuppliers.setItems(obList);
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        String id = lblSupplierID.getText();
        String name = txtName.getText();
        LocalDate date = txtDate.getValue();
        String phoneNumber = txtPhoneNumber.getText();

        boolean isValidName = RegExPatterns.getValidName().matcher(name).matches();
        boolean isValidPhoneNumber = RegExPatterns.getValidPhoneNumber().matcher(phoneNumber).matches();

        if (!isValidName){
            new Alert(Alert.AlertType.ERROR,"Can not Save Supplier.Name is empty").showAndWait();
            return;
        }if (date == null){
            new Alert(Alert.AlertType.ERROR,"Can not Save Supplier.Date is empty").showAndWait();
            return;
        }if (!isValidPhoneNumber){
            new Alert(Alert.AlertType.ERROR,"Can not Save Supplier.Phone Number is empty").showAndWait();
        }else {
            var dto = new SupplierDto(id, name, date, phoneNumber);
            try {
                boolean isSaved = supplierModel.saveSupplier(dto);
                if (isSaved){
                    new Alert(Alert.AlertType.CONFIRMATION,"Supplier is saved").show();
                    clearFields();
                    generateNextSupplierID();
                    loadAllSuppliers();
                }else {
                    new Alert(Alert.AlertType.ERROR,"Supplier is not saved").show();
                }
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
            }
        }
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        String id = lblSupplierID.getText();
        String name = txtName.getText();
        LocalDate date = txtDate.getValue();
        String phoneNumber = txtPhoneNumber.getText();

        boolean isValidName = RegExPatterns.getValidName().matcher(name).matches();
        boolean isValidPhoneNumber = RegExPatterns.getValidPhoneNumber().matcher(phoneNumber).matches();

        if (!isValidName){
            new Alert(Alert.AlertType.ERROR,"Can not Delete Supplier.Name is empty").showAndWait();
            return;
        }if (date == null){
            new Alert(Alert.AlertType.ERROR,"Can not Delete Supplier.Date is empty").showAndWait();
            return;
        }if (!isValidPhoneNumber){
            new Alert(Alert.AlertType.ERROR,"Can not Delete Supplier.Phone Number is empty").showAndWait();
        }else {
            try {
                boolean isDeleted = supplierModel.deleteSuppliers(id);
                if (isDeleted){
                    new Alert(Alert.AlertType.CONFIRMATION,"Supplier id Deleted").show();
                    clearFields();
                    generateNextSupplierID();
                    loadAllSuppliers();
                }else {
                    new Alert(Alert.AlertType.ERROR,"Supplier is not Deleted").show();
                }
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
            }
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        String id = lblSupplierID.getText();
        String name = txtName.getText();
        LocalDate date = txtDate.getValue();
        String phoneNumber = txtPhoneNumber.getText();

        boolean isValidName = RegExPatterns.getValidName().matcher(name).matches();
        boolean isValidPhoneNumber = RegExPatterns.getValidPhoneNumber().matcher(phoneNumber).matches();

        if (!isValidName){
            new Alert(Alert.AlertType.ERROR,"Can not Update Supplier.Name is empty").showAndWait();
            return;
        }if (date == null){
            new Alert(Alert.AlertType.ERROR,"Can not Update Supplier.Date is empty").showAndWait();
            return;
        }if (!isValidPhoneNumber){
            new Alert(Alert.AlertType.ERROR,"Can not Update Supplier.Phone Number is empty").showAndWait();
        } else {
            var dto = new SupplierDto(id, name ,date ,phoneNumber);
            try {
                boolean isUpdated = supplierModel.updateSuppliers(dto);
                if (isUpdated){
                    new Alert(Alert.AlertType.CONFIRMATION,"Supplier is Updated").show();
                    clearFields();
                    generateNextSupplierID();
                    loadAllSuppliers();
                }else {
                    new Alert(Alert.AlertType.ERROR,"Supplier is Not Updated").show();
                }
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
            }
        }
    }

    @FXML
    void txtSearchOnActon(ActionEvent actionEvent) {
        String searchInput = txtSearch.getText();

        try {
            SupplierDto supplierDto;
            if (searchInput.matches("[S][0-9]{3,}")) {
                supplierDto = supplierModel.searchSupplierById(searchInput);
            } else {
                supplierDto = supplierModel.searchSupplierByPhoneNumber(searchInput);
            }
            if (supplierDto != null){
                lblSupplierID.setText(supplierDto.getSupplier_id());
                txtName.setText(supplierDto.getName());
                txtDate.setValue(supplierDto.getDate());
                txtPhoneNumber.setText(supplierDto.getPhone_number());
            } else {
                lblSupplierID.setText("");
                generateNextSupplierID();
                new Alert(Alert.AlertType.INFORMATION,"Supplier Not Found").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }

    @FXML
    void txtNameOnAction(ActionEvent event) {
        txtDate.requestFocus();
    }

    @FXML
    void txtPhoneNumberOnAction(ActionEvent event) {
        btnSaveOnAction(new ActionEvent());
    }
}
