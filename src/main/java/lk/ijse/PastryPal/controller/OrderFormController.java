package lk.ijse.PastryPal.controller;

import com.jfoenix.controls.JFXComboBox;
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
import lk.ijse.PastryPal.dto.CustomerDto;
import lk.ijse.PastryPal.dto.OrderDto;
import lk.ijse.PastryPal.dto.ProductDto;
import lk.ijse.PastryPal.dto.tm.OrderTm;
import lk.ijse.PastryPal.model.CustomerModel;
import lk.ijse.PastryPal.model.OrderModel;
import lk.ijse.PastryPal.model.ProductModel;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderFormController {

    @FXML
    private TableColumn<?, ?> colDescription;

    @FXML
    private TableColumn<?, ?> colItemCode;

    @FXML
    private TableColumn<?, ?> colPrice;

    @FXML
    private TableColumn<?, ?> colQty;

    @FXML
    private TableColumn<?, ?> colTotal;

    @FXML
    private Label lblCustomerID;

    @FXML
    private Label lblCustomerName;

    @FXML
    private Label lblDate;

    @FXML
    private Label lblDate2;

    @FXML
    private Label lblDescription;

    @FXML
    private Label lblNetTotal;

    @FXML
    private Label lblOrderID;

    @FXML
    private Label lblPrice;

    @FXML
    private Label lblProductID;

    @FXML
    private Label lblQtyOnHand;

    @FXML
    private Label lblTime;

    @FXML
    private TableView<OrderTm> tblOrder;

    @FXML
    private TextField txtQty;

    @FXML
    private TextField txtSearch;

    @FXML
    private TextField txtSearchCustomer;

    private CustomerModel customerModel = new CustomerModel();
    private ProductModel productModel = new ProductModel();
    private OrderModel orderModel = new OrderModel();
    private ObservableList<OrderTm> obList = FXCollections.observableArrayList();

    public void initialize(){
        setCellValueFactory();
        setDateAndTime();
        generateNextOrderID();
        generateNextCustomerID();

    }

    private void generateNextCustomerID() {
        try {
            String previousOrderID = lblCustomerID.getText();
            String orderID = customerModel.generateNextCustomer();
            lblCustomerID.setText(orderID);
            if (orderID != null) {
                lblCustomerID.setText(orderID);
            }
            clearFields();
            if (btnClearPressed){
                lblCustomerID.setText(previousOrderID);
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }

    private void generateNextOrderID() {
        try {
            String previousOrderID = lblOrderID.getText();
            String orderID = orderModel.generateNextOrderID();
            lblOrderID.setText(orderID);
            if (orderID != null) {
                lblOrderID.setText(orderID);
            }
            clearFields();
            if (btnClearPressed){
                lblOrderID.setText(previousOrderID);
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }

    private boolean btnClearPressed = false;

    @FXML
    void btnClearOnAction(ActionEvent event) {
        clearFields();
        generateNextOrderID();
    }

    private void clearFields() {
        lblCustomerName.setText("");
        lblDescription.setText("");
        lblPrice.setText("");
        lblQtyOnHand.setText("");
        txtQty.setText("");
    }

    private void setDateAndTime(){
        Platform.runLater(() -> {
            lblDate.setText(String.valueOf(LocalDate.now()));
            lblDate2.setText(String.valueOf(LocalDate.now()));

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
        colItemCode.setCellValueFactory(new PropertyValueFactory<>("product_id"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("unit_price"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
    }

    @FXML
    void btnAddOnAction(ActionEvent event) {
        String product_id = lblProductID.getText();
        String desc = lblDescription.getText();
        double unit_price = Double.parseDouble(lblPrice.getText());
        int qty = Integer.parseInt(txtQty.getText());
        double total = unit_price * qty;

        if (!obList.isEmpty()){
            for (int i = 0; i < tblOrder.getItems().size(); i++) {
                if (colItemCode.getCellData(i).equals(product_id)){
                    int col_qty = (int) colQty.getCellData(i);
                    qty += col_qty;
                    total = unit_price * qty;

                    obList.get(i).setQty(qty);
                    obList.get(i).setTotal(total);

                    calculateTotal();
                    tblOrder.refresh();
                    return;
                }
            }
        }
        var OrderTm = new OrderTm(product_id,desc,unit_price,qty,total);
        obList.add(OrderTm);

        tblOrder.setItems(obList);
        calculateTotal();
    }

    private void calculateTotal() {
        double total = 0;
        for (int i = 0; i < tblOrder.getItems().size(); i++) {
            total += (double) colTotal.getCellData(i);
        }
        lblNetTotal.setText(String.valueOf(total));
    }

    @FXML
    void btnPlaceOrderOnAction(ActionEvent event) {
        String orderID = lblOrderID.getText();
        LocalDate date = LocalDate.parse(lblDate2.getText());
        String customerID = lblCustomerID.getText();

        List<OrderTm> orderTmList = new ArrayList<>();
        for (int i = 0; i < tblOrder.getItems().size(); i++) {
            OrderTm orderTm = obList.get(i);
            orderTmList.add(orderTm);
        }
        var customerDto = new CustomerDto(customerID,null,null,null);
        var orderDto = new OrderDto(orderID, date ,customerID ,orderTmList);
        try {
            boolean checkCustomerID = customerModel.isValidCustomer(customerDto);
            if (!checkCustomerID){
                customerModel.save(customerDto);
            }
            boolean isSuccess = orderModel.placeOrder(orderDto);
            if (isSuccess){
                new Alert(Alert.AlertType.CONFIRMATION,"Order is Saved").show();
                String productId = lblProductID.getText();
                ProductDto updatedProduct = productModel.searchProductById(productId);
                generateNextOrderID();
                generateNextCustomerID();
                if (updatedProduct != null) {
                    lblQtyOnHand.setText(String.valueOf(updatedProduct.getQty()));
                }
                obList.clear();
                tblOrder.refresh();
                calculateTotal();
                generateNextOrderID();
                generateNextCustomerID();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }


    @FXML
    void txtQtyOnAction(ActionEvent event) {
        btnAddOnAction(new ActionEvent());
    }

    @FXML
    void txtSearchOnActon(ActionEvent event) {
        String searchInput = txtSearch.getText();

        try {
            ProductDto productDto;
            if (searchInput.matches("[P][0-9]{3,}")) {
                productDto = productModel.searchProductById(searchInput);
            }else {
                productDto = productModel.searchProductByName(searchInput);
            }
            if (productDto != null ){
                lblProductID.setText(productDto.getProduct_id());
                lblDescription.setText(productDto.getDescription());
                lblPrice.setText(String.valueOf(productDto.getPrice()));
                lblQtyOnHand.setText(String.valueOf(productDto.getQty()));
            }else {
                lblProductID.setText("");
                new Alert(Alert.AlertType.CONFIRMATION,"Product Not Found").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }
    @FXML
    void txtSearchCustomerOnActon(ActionEvent event) {
        String searchCustomer = txtSearchCustomer.getText();

        try {
            CustomerDto customerDto;
            //validating the input method assuming it is a digit
            if (searchCustomer.matches("\\d+")) {
                customerDto = customerModel.searchCustomerByPhoneNumber(searchCustomer);
            } else {
                customerDto = customerModel.searchCustomer(searchCustomer);
            }
            if (customerDto != null) {
                lblCustomerID.setText(customerDto.getCustomer_id());
                lblCustomerName.setText(customerDto.getName());
                txtSearch.setText("");
            } else {
                generateNextCustomerID();
                new Alert(Alert.AlertType.INFORMATION, "Customer not Found").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }
}