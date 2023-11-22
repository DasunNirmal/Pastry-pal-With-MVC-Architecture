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
    private JFXComboBox<String> cmbCustomerID;

    @FXML
    private JFXComboBox<String> cmbProductID;

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
    private TableView<OrderTm> tblOrder;

    @FXML
    private Label lblCustomerName;

    @FXML
    private Label lblDate;

    @FXML
    private Label lblDate2;

    @FXML
    private Label lblDescription;

    @FXML
    private Label lblOrderID;

    @FXML
    private Label lblPrice;

    @FXML
    private Label lblQtyOnHand;

    @FXML
    private Label lblTime;

    @FXML
    private TextField txtQty;

    @FXML
    private Label lblNetTotal;

    @FXML
    private TextField txtSearch;

    private CustomerModel customerModel = new CustomerModel();
    private ProductDto dto = new ProductDto();
    private ProductModel productModel = new ProductModel();
    private OrderModel orderModel = new OrderModel();
    private ObservableList<OrderTm> obList = FXCollections.observableArrayList();

    public void initialize(){
        setCellValueFactory();
        setDateAndTime();
        generateNextOrderID();
        loadCustomerID();
        loadProductId();
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
        cmbCustomerID.setValue(null);
        lblCustomerName.setText("");
        cmbProductID.setValue(null);
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

    private void loadCustomerID() {
        ObservableList<String> obList = FXCollections.observableArrayList();

        try {
            List<CustomerDto> idList = customerModel.getAllCustomer();
            for (CustomerDto dto : idList){
                obList.add(dto.getCustomer_id());
            }
            cmbCustomerID.setItems(obList);
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }
    private void loadProductId() {
        ObservableList<String> obList = FXCollections.observableArrayList();

        try {
            List<ProductDto> productDto = productModel.getAllProducts();
            for (ProductDto dto : productDto){
                obList.add(dto.getProduct_id());
            }
            cmbProductID.setItems(obList);
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }

    @FXML
    void btnAddOnAction(ActionEvent event) {
        String product_id = cmbProductID.getValue();
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
        String customerID = cmbCustomerID.getValue();

        List<OrderTm> orderTmList = new ArrayList<>();
        for (int i = 0; i < tblOrder.getItems().size(); i++) {
            OrderTm orderTm = obList.get(i);
            orderTmList.add(orderTm);
        }

        var orderDto = new OrderDto(orderID, date ,customerID ,orderTmList);
        try {
            boolean isSuccess = orderModel.placeOrder(orderDto);
            if (isSuccess){
                new Alert(Alert.AlertType.CONFIRMATION,"Order is Saved").show();
                String productId = cmbProductID.getValue();
                ProductDto updatedProduct = productModel.searchProductById(productId);
                if (updatedProduct != null) {
                    lblQtyOnHand.setText(String.valueOf(updatedProduct.getQty()));
                }
                obList.clear();
                tblOrder.refresh();
                calculateTotal();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }

    @FXML
    void cmbCustomerIdOnAction(ActionEvent event) {
        String id = cmbCustomerID.getValue();
        try {
            CustomerDto customerDto = customerModel.searchCustomer(id);
            if (customerDto != null) {
                lblCustomerName.setText(customerDto.getName());
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }

    @FXML
    void cmbProductIdOnAction(ActionEvent event) {
        String id = cmbProductID.getValue();
        txtQty.requestFocus();
        try {
            ProductDto dto = productModel.searchProductById(id);
            if (dto != null){
                lblDescription.setText(dto.getDescription());
                lblPrice.setText(String.valueOf(dto.getPrice()));
                lblQtyOnHand.setText(String.valueOf(dto.getQty()));
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }

    @FXML
    void txtQtyOnAction(ActionEvent event) {

    }

    @FXML
    void txtSearchOnActon(ActionEvent event) {

    }
}