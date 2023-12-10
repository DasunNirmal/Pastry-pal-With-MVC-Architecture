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
import lk.ijse.PastryPal.dto.ProductDto;
import lk.ijse.PastryPal.dto.tm.CustomerTm;
import lk.ijse.PastryPal.dto.tm.ProductTm;
import lk.ijse.PastryPal.model.ProductModel;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ProductFormController {

    @FXML
    private TableColumn<?, ?> colDescription;

    @FXML
    private TableColumn<?, ?> colProductID;

    @FXML
    private TableColumn<?, ?> colPrice;

    @FXML
    private TableColumn<?, ?> colQtyOnHand;

    @FXML
    private TableView<ProductTm> tblItems;

    @FXML
    private Label lblDate;

    @FXML
    private Label lblProductID;

    @FXML
    private Label lblTime;

    @FXML
    private TextField txtDescription;

    @FXML
    private TextField txtPrice;

    @FXML
    private TextField txtQty;

    @FXML
    private TextField txtSearch;

    @FXML
    private Label lblProductCount;

    @FXML
    private Label lblProductsSaveOrNot;
    private ProductModel productModel = new ProductModel();
    private ObservableList<ProductTm> obList = FXCollections.observableArrayList();

    public void initialize() throws SQLException {
        setValueFactory();
        setDateAndTime();
        generateNextProductID();
        loadAllProducts();
        tableListener();
        totalProducts();
    }

    private void generateNextProductID() {
        try {
            String previousItemID = lblProductID.getId();
            String itemID = productModel.generateNextItemID();
            lblProductID.setText(itemID);
            clearFields();
            if (btnClearPressed){
                lblProductID.setText(previousItemID);
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }
    private boolean btnClearPressed = false;
    @FXML
    void btnClearOnAction(ActionEvent event) {
        clearFields();
        generateNextProductID();
    }
    private void clearFields(){
        txtDescription.setText("");
        txtQty.setText("");
        txtPrice.setText("");
        txtSearch.setText("");
        lblProductsSaveOrNot.setText("");
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
        colProductID.setCellValueFactory(new PropertyValueFactory<>("product_id"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colQtyOnHand.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        tblItems.setId("my-table");
    }
    private void tableListener() {
        tblItems.getSelectionModel().selectedItemProperty().addListener((observable, oldValued, newValue) -> {
            setData(newValue);
        });
    }

    private void setData(ProductTm row) {
        if (row != null) {
            lblProductID.setText(row.getProduct_id());
            txtDescription.setText(row.getDescription());
            txtQty.setText(String.valueOf(row.getQty()));
            txtPrice.setText(String.valueOf(row.getPrice()));
        }
    }
    private void loadAllProducts() {
        try {
            obList.clear();
            List<ProductDto> dtoList = productModel.getAllProducts();
            for (ProductDto dto : dtoList){
                obList.add(
                        new ProductTm(
                                dto.getProduct_id(),
                                dto.getDescription(),
                                dto.getQty(),
                                dto.getPrice()
                        )
                );
            }
            tblItems.setItems(obList);
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }
    private void totalProducts() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();
        Statement statement = connection.createStatement();

        String sql = "SELECT count(*) FROM products";
        ResultSet resultSet = statement.executeQuery(sql);
        resultSet.next();
        int count = resultSet.getInt(1);
        lblProductCount.setText(String.valueOf(count));
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        String id = lblProductID.getText();
        String description = txtDescription.getText();
        String qtyText = txtQty.getText();
        String priceText = txtPrice.getText();

        boolean isValidDescription = RegExPatterns.getValidDescription().matcher(description).matches();
        boolean isValidQty = RegExPatterns.getValidDouble().matcher(qtyText).matches();
        boolean isValidPrice = RegExPatterns.getValidDouble().matcher(priceText).matches();

        if (!isValidDescription) {
            new Alert(Alert.AlertType.ERROR, "Can not Save Product.Description is Empty").showAndWait();
            return;
        }if (!isValidQty){
            new Alert(Alert.AlertType.ERROR,"Can not Save Product.Quantity is Empty").showAndWait();
            return;
        }if (!isValidPrice){
            new Alert(Alert.AlertType.ERROR,"Can not Save Product.Price is Empty").showAndWait();
        }else {
            try {
                int qty = Integer.parseInt(qtyText);
                double price = Double.parseDouble(priceText);

                var dto = new ProductDto(id, description, qty, price);
                try {
                    boolean isSaved = productModel.saveProduct(dto);
                    if (isSaved) {
                        obList.clear();
                        generateNextProductID();
                        loadAllProducts();
                        totalProducts();
                        lblProductsSaveOrNot.setText("Product is Saved !");
                        PauseTransition pause = new PauseTransition(Duration.seconds(2));
                        pause.setOnFinished(pauseEvent -> {
                            clearFields();
                        });
                        pause.play();
                    } else {
                        lblProductsSaveOrNot.setText("Product is Not Saved !");
                        PauseTransition pause = new PauseTransition(Duration.seconds(2));
                        pause.setOnFinished(pauseEvent -> {
                            lblProductsSaveOrNot.setText("");
                        });
                        pause.play();
                    }
                } catch (SQLException e) {
                    new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                }
            } catch (NumberFormatException e) {
                new Alert(Alert.AlertType.ERROR, "Invalid quantity or price format").showAndWait();
            }
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        String id = lblProductID.getText();
        String desc = txtDescription.getText();
        String qtyText = txtQty.getText();
        String priceText = txtPrice.getText();

        boolean isValidDescription = RegExPatterns.getValidDescription().matcher(desc).matches();
        boolean isValidQty = RegExPatterns.getValidDouble().matcher(qtyText).matches();
        boolean isValidPrice = RegExPatterns.getValidDouble().matcher(priceText).matches();

        if (!isValidDescription){
            new Alert(Alert.AlertType.ERROR, "Can not Update Product.Description is Empty").showAndWait();
            return;
        }if (!isValidQty){
            new Alert(Alert.AlertType.ERROR, "Can not Update Product.Quantity is Empty").showAndWait();
            return;
        }if (!isValidPrice){
            new Alert(Alert.AlertType.ERROR, "Can not Update Product.Price is Empty").showAndWait();
        }else {
            try {
                int qty = Integer.parseInt(qtyText);
                double price = Double.parseDouble(priceText);

                var dto = new ProductDto(id,desc,qty,price);
                try {
                    boolean isUpdated = productModel.updateProducts(dto);
                    if (isUpdated){
                        obList.clear();
                        generateNextProductID();
                        totalProducts();
                        loadAllProducts();
                        lblProductsSaveOrNot.setText("Product is Updated !");
                        PauseTransition pause = new PauseTransition(Duration.seconds(2));
                        pause.setOnFinished(pauseEvent -> {
                            clearFields();
                        });
                        pause.play();
                    }
                } catch (SQLException e) {
                    lblProductsSaveOrNot.setText("Product is Not Updated !");
                    PauseTransition pause = new PauseTransition(Duration.seconds(2));
                    pause.setOnFinished(pauseEvent -> {
                        lblProductsSaveOrNot.setText("");
                    });
                    pause.play();
                }
            }catch (NumberFormatException e){
                new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
            }
        }
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        String id = lblProductID.getText();
        String desc = txtDescription.getText();
        String qtyText = txtQty.getText();
        String priceText = txtPrice.getText();

        boolean isValidDescription = RegExPatterns.getValidName().matcher(desc).matches();
        boolean isValidQty = RegExPatterns.getValidDouble().matcher(qtyText).matches();
        boolean isValidPrice = RegExPatterns.getValidDouble().matcher(priceText).matches();

        if (!isValidDescription){
            new Alert(Alert.AlertType.ERROR, "Can not Delete Product.Description is Empty").showAndWait();
            return;
        }if (!isValidQty){
            new Alert(Alert.AlertType.ERROR, "Can not Delete Product.Quantity is Empty").showAndWait();
            return;
        }if (!isValidPrice){
            new Alert(Alert.AlertType.ERROR, "Can not Delete Product.Price is Empty").showAndWait();
        }else {
            try {
                boolean isDeleted = productModel.deleteProduct(id);
                if (isDeleted){
                    obList.clear();
                    generateNextProductID();
                    loadAllProducts();
                    totalProducts();
                    lblProductsSaveOrNot.setText("Product is Deleted !");
                    PauseTransition pause = new PauseTransition(Duration.seconds(2));
                    pause.setOnFinished(pauseEvent -> {
                        clearFields();
                    });
                    pause.play();
                }else {
                    lblProductsSaveOrNot.setText("Product is Not Deleted !");
                    PauseTransition pause = new PauseTransition(Duration.seconds(2));
                    pause.setOnFinished(pauseEvent -> {
                        lblProductsSaveOrNot.setText("");
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
        FilteredList<ProductTm> filterProductTbl = new FilteredList<>(obList, b -> true);
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filterProductTbl.setPredicate(productTm -> {
                if (newValue.isEmpty() || newValue.isBlank() || newValue == null) {
                    return true;
                }
                String search = newValue.toLowerCase();

                if (productTm.getProduct_id().toLowerCase().contains(search)) {
                    return true;
                } else if (productTm.getDescription().toLowerCase().contains(search)) {
                    return true;
                } else
                    return false;
            });
        });
        SortedList<ProductTm> sortCustomerTbl = new SortedList<>(filterProductTbl);
        sortCustomerTbl.comparatorProperty().bind(tblItems.comparatorProperty());
        tblItems.setItems(sortCustomerTbl);
    }
    @FXML
    void txtPriceOnAction(ActionEvent event) {
        txtPrice.requestFocus();
    }

    @FXML
    void txtQtyOnAction(ActionEvent event) {
        txtQty.requestFocus();
    }

    @FXML
    void txtSaveOnAction(ActionEvent event) {
        btnSaveOnAction(new ActionEvent());
    }
}
