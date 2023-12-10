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
import lk.ijse.PastryPal.dto.SupplierDto;
import lk.ijse.PastryPal.dto.tm.SupplierTm;
import lk.ijse.PastryPal.model.SupplierModel;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
    private Label lblSupplierCount;

    @FXML
    private Label lblTime;

    @FXML
    private Label lblDate;

    @FXML
    private Label lblSupplierSaveOrNot;
    private SupplierModel supplierModel = new SupplierModel();
    private ObservableList<SupplierTm> obList = FXCollections.observableArrayList();

    public void initialize() throws SQLException {
        setValueFactory();
        setDateAndTime();
        generateNextSupplierID();
        loadAllSuppliers();
        tableListener();
        totalSuppliers();
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
    private void tableListener() {
        tblSuppliers.getSelectionModel().selectedItemProperty().addListener((observable, oldValued, newValue) -> {
            setData(newValue);
        });
    }

    private void setData(SupplierTm row) {
        if (row != null) {
            lblSupplierID.setText(row.getSupplier_id());
            txtName.setText(row.getName());
            txtDate.setValue(row.getDate());
            txtPhoneNumber.setText(row.getPhone_number());
        }
    }
    private void totalSuppliers() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();
        Statement statement = connection.createStatement();

        String sql = "SELECT count(*) FROM suppliers";
        ResultSet resultSet = statement.executeQuery(sql);
        resultSet.next();
        int count = resultSet.getInt(1);
        lblSupplierCount.setText(String.valueOf(count));
    }
    private void loadAllSuppliers() {
        try {
            obList.clear();
            List<SupplierDto> dtoList = supplierModel.getAllSuppliers();
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
                    obList.clear();
                    generateNextSupplierID();
                    loadAllSuppliers();
                    totalSuppliers();
                    lblSupplierSaveOrNot.setText("Supplier is Saved !");
                    PauseTransition pause = new PauseTransition(Duration.seconds(2));
                    pause.setOnFinished(pauseEvent -> {
                        clearFields();
                    });
                    pause.play();
                }else {
                    lblSupplierSaveOrNot.setText("Supplier is Not Saved !");
                    PauseTransition pause = new PauseTransition(Duration.seconds(2));
                    pause.setOnFinished(pauseEvent -> {
                        lblSupplierSaveOrNot.setText("");
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
                    obList.clear();
                    generateNextSupplierID();
                    loadAllSuppliers();
                    totalSuppliers();
                    lblSupplierSaveOrNot.setText("Supplier is Deleted !");
                    PauseTransition pause = new PauseTransition(Duration.seconds(2));
                    pause.setOnFinished(pauseEvent -> {
                        clearFields();
                    });
                    pause.play();
                }else {
                    lblSupplierSaveOrNot.setText("Supplier is  Not Deleted !");
                    PauseTransition pause = new PauseTransition(Duration.seconds(2));
                    pause.setOnFinished(pauseEvent -> {
                        lblSupplierSaveOrNot.setText("");
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
                    obList.clear();
                    generateNextSupplierID();
                    loadAllSuppliers();
                    totalSuppliers();
                    lblSupplierSaveOrNot.setText("Supplier is Updated !");
                    PauseTransition pause = new PauseTransition(Duration.seconds(2));
                    pause.setOnFinished(pauseEvent -> {
                        clearFields();
                    });
                    pause.play();
                }else {
                    lblSupplierSaveOrNot.setText("Supplier is Not Updated !");
                    PauseTransition pause = new PauseTransition(Duration.seconds(2));
                    pause.setOnFinished(pauseEvent -> {
                        lblSupplierSaveOrNot.setText("");
                    });
                    pause.play();
                }
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
            }
        }
    }
    @FXML
    void txtSearchOnActon(KeyEvent event) {
        searchTableFilter();
    }
    private void searchTableFilter() {
        FilteredList<SupplierTm> filterSupplierTbl = new FilteredList<>(obList, b -> true);
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filterSupplierTbl.setPredicate(supplierTm -> {
                if (newValue.isEmpty() || newValue.isBlank() || newValue == null) {
                    return true;
                }
                String search = newValue.toLowerCase();

                if (supplierTm.getSupplier_id().toLowerCase().contains(search)) {
                    return true;
                } else if (supplierTm.getSupplier_id().toLowerCase().contains(search)) {
                    return true;
                } else if (supplierTm.getName().toLowerCase().contains(search)) {
                    return true;
                } else if (supplierTm.getName().toLowerCase().contains(search)) {
                    return true;
                } else if (supplierTm.getDate().toString().contains(search)) {
                    return true;
                } else if (supplierTm.getDate().toString().contains(search)) {
                    return true;
                } else if (supplierTm.getPhone_number().toLowerCase().contains(search)) {
                    return true;
                } else if (supplierTm.getPhone_number().toLowerCase().contains(search)) {
                    return true;
                } else
                    return false;
            });
        });
        SortedList<SupplierTm> sortCustomerTbl = new SortedList<>(filterSupplierTbl);
        sortCustomerTbl.comparatorProperty().bind(tblSuppliers.comparatorProperty());
        tblSuppliers.setItems(sortCustomerTbl);
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
