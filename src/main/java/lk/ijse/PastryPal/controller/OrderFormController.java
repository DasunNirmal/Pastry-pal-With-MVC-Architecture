package lk.ijse.PastryPal.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import lk.ijse.PastryPal.DB.DbConnection;
import lk.ijse.PastryPal.dto.CustomerDto;
import lk.ijse.PastryPal.dto.OrderDto;
import lk.ijse.PastryPal.dto.ProductDto;
import lk.ijse.PastryPal.dto.tm.OrderTm;
import lk.ijse.PastryPal.model.CustomerModel;
import lk.ijse.PastryPal.model.OrderModel;
import lk.ijse.PastryPal.model.ProductModel;
import lombok.SneakyThrows;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.controlsfx.control.textfield.TextFields;

import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderFormController {
    @FXML
    private TableColumn<?, ?> colAction;

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

    public void initialize() throws SQLException {
        setCellValueFactory();
        setDateAndTime();
        generateNextOrderID();
        generateNextCustomerID();
        autoCompleteProduct();
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
        colAction.setCellValueFactory(new PropertyValueFactory<>("btn"));
        tblOrder.setId("my-table");
    }

    @FXML
    void btnAddOnAction(ActionEvent event) {
        String product_id = lblProductID.getText();
        String desc = lblDescription.getText();
        double unit_price = Double.parseDouble(lblPrice.getText());
        int qty = Integer.parseInt(txtQty.getText());
        double total = unit_price * qty;
        Button btn = new Button("Delete");

        setRemoveButtonAction(btn);
        btn.setCursor(Cursor.HAND);

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
        var OrderTm = new OrderTm(product_id,desc,unit_price,qty,total,btn);
        obList.add(OrderTm);

        tblOrder.setItems(obList);
        calculateTotal();
    }

    private void setRemoveButtonAction(Button btn) {
        btn.setStyle(
                "-fx-background-color: rgba(231, 76, 60, 1.0);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-min-width: 10;" +
                        "-fx-pref-width: 152;"
        );
        btn.setOnAction((e) -> {
            ButtonType yes = new ButtonType("yes", ButtonBar.ButtonData.OK_DONE);
            ButtonType no = new ButtonType("no", ButtonBar.ButtonData.CANCEL_CLOSE);
            Optional<ButtonType> type = new Alert(Alert.AlertType.INFORMATION, "Are you sure want to remove this order", yes, no).showAndWait();

            if (type.orElse(no) == yes) {
                int focusedIndex = tblOrder.getSelectionModel().getFocusedIndex();

                obList.remove(focusedIndex);
                tblOrder.refresh();
                calculateTotal();
            }
        });
    }

    private void calculateTotal() {
        double total = 0;
        for (int i = 0; i < tblOrder.getItems().size(); i++) {
            total += (double) colTotal.getCellData(i);
        }
        lblNetTotal.setText(String.valueOf(total));
    }

    @SneakyThrows
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
                Report();
                generateNextOrderID();
                generateNextCustomerID();
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

    private void Report() throws JRException, SQLException {
        String orderId = lblOrderID.getText();
        InputStream resourceAsStream = getClass().getResourceAsStream("/reports/OrderInvoice.jrxml");
        JasperDesign load = JRXmlLoader.load(resourceAsStream);

        JRDesignQuery jrDesignQuery = new JRDesignQuery();
        jrDesignQuery.setText("SELECT \n" +
                "    o.order_id,\n" +
                "    o.customer_id,\n" +
                "    o.order_date,\n" +
                "    d.product_id,\n" +
                "    p.description,\n" +
                "    d.qty,\n" +
                "    d.unit_price,\n" +
                "    (d.qty * d.unit_price) AS total_price\n" +
                "FROM \n" +
                "    orders o\n" +
                "JOIN \n" +
                "    order_details d ON o.order_id = d.order_id\n" +
                "JOIN \n" +
                "    products p ON d.product_id = p.product_id\n" +
                "WHERE \n" +
                "    o.order_id = " + orderId + ";");

        JasperReport jasperReport = JasperCompileManager.compileReport(load);
        JasperPrint jasperPrint = JasperFillManager.fillReport(
                jasperReport,
                null,
                DbConnection.getInstance().getConnection()
        );
        JasperViewer.viewReport(jasperPrint, false);
    }



    @FXML
    void txtQtyOnAction(ActionEvent event) {
        btnAddOnAction(new ActionEvent());
    }

    private void autoCompleteProduct() throws SQLException {
        String[] desc = productModel.getProductsByName(txtSearch.getText());
        TextFields.bindAutoCompletion(txtSearch, desc);
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