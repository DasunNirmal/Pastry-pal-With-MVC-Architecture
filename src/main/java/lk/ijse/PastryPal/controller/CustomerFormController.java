package lk.ijse.PastryPal.controller;

import javafx.animation.KeyFrame;
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
import lk.ijse.PastryPal.dto.CustomerDto;
import lk.ijse.PastryPal.dto.tm.CustomerTm;
import lk.ijse.PastryPal.model.CustomerModel;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
    private TableView<CustomerTm> tblCustomer;

    @FXML
    private Label lblCustomerId;

    @FXML
    private TextField txtCustomerAddress;

    @FXML
    private TextField txtCustomerName;

    @FXML
    private TextField txtPhoneNumber;

    @FXML
    private TextField txtSearch;

    @FXML
    private Label lblDate;

    @FXML
    private Label lblTime;

    @FXML
    private Label lblCustomers;

    @FXML
    private Label lblTotalCustomers;

    private CustomerModel customerModel = new CustomerModel();
    private ObservableList<CustomerTm> obList = FXCollections.observableArrayList();

    public void initialize() throws SQLException {
        setCellValueFactory();
        setDateAndTime();
        generateNextCustomerID();
        loadAllCustomers();
        tableListener();
        totalCustomers();
        totalLoyalityCustomers();
    }

    private void totalLoyalityCustomers() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();
        Statement statement = connection.createStatement();

        String sql = "SELECT COUNT(*) FROM customer WHERE name IS NOT NULL;";
        ResultSet resultSet = statement.executeQuery(sql);
        resultSet.next();
        int count = resultSet.getInt(1);
        lblCustomers.setText(String.valueOf(count));
    }

    private void totalCustomers() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();
        Statement statement = connection.createStatement();

        String sql = "SELECT count(*) FROM customer";
        ResultSet resultSet = statement.executeQuery(sql);
        resultSet.next();
        int count = resultSet.getInt(1);
        lblTotalCustomers.setText(String.valueOf(count));
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
        txtSearch.setText("");
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
    private void setCellValueFactory() {
        colCustomerID.setCellValueFactory(new PropertyValueFactory<>("customer_id"));
        colCustomerName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colPoneNumber.setCellValueFactory(new PropertyValueFactory<>("phone_number"));
        tblCustomer.setId("my-table");
    }
    private void tableListener() {
        tblCustomer.getSelectionModel().selectedItemProperty().addListener((observable, oldValued, newValue) -> {
            setData(newValue);
        });
    }

    private void setData(CustomerTm row) {
        if (row != null) {
            lblCustomerId.setText(row.getCustomer_id());
            txtCustomerName.setText(row.getName());
            txtCustomerAddress.setText(row.getAddress());
            txtPhoneNumber.setText(row.getPhone_number());
        }
    }
    private void loadAllCustomers() {
        try {
            obList.clear();
            List<CustomerDto> dtoList = customerModel.getAllCustomer();
            for (CustomerDto dto : dtoList){
                obList.add(
                        new CustomerTm(
                                dto.getCustomer_id(),
                                dto.getName(),
                                dto.getAddress(),
                                dto.getPhone_number()
                        )
                );
            }
            tblCustomer.setItems(obList);
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        String id = lblCustomerId.getText();
        String name = txtCustomerName.getText();
        String address = txtCustomerAddress.getText();
        String phoneNumber = txtPhoneNumber.getText();

        boolean isValidName = RegExPatterns.getValidName().matcher(name).matches();
        boolean isValidAddress = RegExPatterns.getValidAddress().matcher(address).matches();
        boolean isValidPhoneNumber = RegExPatterns.getValidPhoneNumber().matcher(phoneNumber).matches();

        if (!isValidName){
            new Alert(Alert.AlertType.ERROR,"Can Not Leave Name Empty").showAndWait();
            return;
        }if (!isValidAddress){
            new Alert(Alert.AlertType.ERROR,"Not a Valid Address").showAndWait();
            return;
        }if (!isValidPhoneNumber){
            new Alert(Alert.AlertType.ERROR,"Not A Valid Phone Number").showAndWait();
        }else {

            var dto = new CustomerDto(id,name,address,phoneNumber);
            try {
                boolean isSaved = customerModel.save(dto);
                if (isSaved){
                    new Alert(Alert.AlertType.CONFIRMATION,"Customer is Saved").show();
                    clearFields();
                    generateNextCustomerID();
                    obList.clear();
                    loadAllCustomers();
                }else {
                    new Alert(Alert.AlertType.ERROR,"Customer is Not Saved").show();
                }
            }catch (SQLException e){
                new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
            }
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        String id = lblCustomerId.getText();
        String name = txtCustomerName.getText();
        String address = txtCustomerAddress.getText();
        String phoneNumber = txtPhoneNumber.getText();

        boolean isValidName = RegExPatterns.getValidName().matcher(name).matches();
        boolean isValidAddress = RegExPatterns.getValidAddress().matcher(address).matches();
        boolean isValidPhoneNumber = RegExPatterns.getValidPhoneNumber().matcher(phoneNumber).matches();

        if (!isValidName){
            new Alert(Alert.AlertType.ERROR,"Can Not Update Customers.Name is Empty").showAndWait();
            return;
        }if (!isValidAddress){
            new Alert(Alert.AlertType.ERROR,"Can not Update Customer.Address is Empty").showAndWait();
            return;
        }if (!isValidPhoneNumber){
            new Alert(Alert.AlertType.ERROR,"Can not Update Customer.Phone Number is Empty").showAndWait();
        }else {
            try {
                var dto = new CustomerDto(id,name,address,phoneNumber);
                try {
                    boolean isUpdated = customerModel.updateCustomer(dto);
                    if (isUpdated){
                        new Alert(Alert.AlertType.CONFIRMATION,"Customer is Updated").show();
                        obList.clear();
                        loadAllCustomers();
                        clearFields();
                        generateNextCustomerID();
                    }
                }catch (SQLException e){
                    new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
                }
            } catch (NumberFormatException e) {
                new Alert(Alert.AlertType.ERROR,"In Valid Phone Number Format").showAndWait();
            }
        }
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        String id = lblCustomerId.getText();
        String name = txtCustomerName.getText();
        String address = txtCustomerAddress.getText();
        String phoneNumber = txtPhoneNumber.getText();

        boolean isValidName = RegExPatterns.getValidName().matcher(name).matches();
        boolean isValidAddress = RegExPatterns.getValidAddress().matcher(address).matches();
        boolean isValidPhoneNumber = RegExPatterns.getValidPhoneNumber().matcher(phoneNumber).matches();

        if (!isValidName){
            new Alert(Alert.AlertType.ERROR,"Can Not Delete.Name is Empty").showAndWait();
            return;
        }if (!isValidAddress){
            new Alert(Alert.AlertType.ERROR,"Can Not Delete.Address is Empty").showAndWait();
            return;
        }if (!isValidPhoneNumber){
            new Alert(Alert.AlertType.ERROR,"Can Not Delete.Phone Number is Empty").showAndWait();
        }else {
            try {
                boolean isDeleted = customerModel.deleteCustomers(id);
                if (isDeleted){
                    new Alert(Alert.AlertType.CONFIRMATION,"Customer is Deleted").show();
                    obList.clear();
                    loadAllCustomers();
                    clearFields();
                    generateNextCustomerID();
                }else {
                    new Alert(Alert.AlertType.INFORMATION,"Customer is Not Deleted").show();
                }
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
            }
        }
    }

    @FXML
    void txtSearchFilterOnAction(KeyEvent event) {
        searchTableFilter();
    }
    private void searchTableFilter() {
        FilteredList<CustomerTm> filterCustomerTbl = new FilteredList<>(obList, b -> true);
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filterCustomerTbl.setPredicate(customerTm -> {
                if (newValue.isEmpty() || newValue.isBlank() || newValue == null) {
                    return true;
                }
                String search = newValue.toLowerCase();

                if (customerTm.getCustomer_id().toLowerCase().contains(search)) {
                    return true;
                } else if (customerTm.getCustomer_id().toLowerCase().contains(search)) {
                    return true;
                } else if (customerTm.getName().toLowerCase().contains(search)) {
                    return true;
                } else if (customerTm.getName().toLowerCase().contains(search)) {
                    return true;
                } else if (customerTm.getAddress().toLowerCase().contains(search)) {
                    return true;
                } else if (customerTm.getAddress().toLowerCase().contains(search)) {
                    return true;
                } else if (customerTm.getPhone_number().toLowerCase().contains(search)) {
                    return true;
                } else if (customerTm.getPhone_number().toLowerCase().contains(search)) {
                    return true;
                } else
                    return false;
            });
        });
        SortedList<CustomerTm> sortCustomerTbl = new SortedList<>(filterCustomerTbl);
        sortCustomerTbl.comparatorProperty().bind(tblCustomer.comparatorProperty());
        tblCustomer.setItems(sortCustomerTbl);
    }
    @FXML
    void btnReportOnAction(ActionEvent event) throws JRException, SQLException {
        InputStream resourceAsStream = getClass().getResourceAsStream("/reports/CustomerDetail.jrxml");
        JasperDesign load = JRXmlLoader.load(resourceAsStream);
        JasperReport jasperReport = JasperCompileManager.compileReport(load);
        JasperPrint jasperPrint = JasperFillManager.fillReport(
                jasperReport,
                null,
                DbConnection.getInstance().getConnection()
        );
        JasperViewer.viewReport(jasperPrint,false);
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
