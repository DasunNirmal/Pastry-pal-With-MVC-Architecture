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
import lk.ijse.PastryPal.dto.ProductDto;
import lk.ijse.PastryPal.dto.tm.ProductTm;
import lk.ijse.PastryPal.model.ProductModel;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ProductFormController {
    @FXML
    private TableColumn<?, ?> colAction;

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

    private ProductModel productModel = new ProductModel();

    public void initialize(){
        setValueFactory();
        setDateAndTime();
        generateNextItemID();
        loadAllProducts();
    }

    private void generateNextItemID() {
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
        generateNextItemID();
    }
    private void clearFields(){
        txtDescription.setText("");
        txtQty.setText("");
        txtPrice.setText("");
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
    private void setValueFactory() {
        colProductID.setCellValueFactory(new PropertyValueFactory<>("product_id"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colQtyOnHand.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
    }
    private void loadAllProducts() {
        ObservableList<ProductTm> obList = FXCollections.observableArrayList();
        try {
            List<ProductDto> dtoList = productModel.getAllItems();
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
                //the reason for this is qty and price takes double and can't take String
                double qty = Double.parseDouble(qtyText);
                double price = Double.parseDouble(priceText);

                var dto = new ProductDto(id, description, qty, price);
                try {
                    boolean isSaved = productModel.saveItem(dto);
                    if (isSaved) {
                        new Alert(Alert.AlertType.CONFIRMATION, "Product Is Saved").show();
                        clearFields();
                        generateNextItemID();
                        loadAllProducts();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Product Is Not Saved").show();
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

        boolean isValidDescription = RegExPatterns.getValidName().matcher(desc).matches();
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
                double qty = Double.parseDouble(qtyText);
                double price = Double.parseDouble(priceText);

                var dto = new ProductDto(id,desc,qty,price);
                try {
                    boolean isUpdated = productModel.updateItems(dto);
                    if (isUpdated){
                        new Alert(Alert.AlertType.CONFIRMATION,"Item Is Updated").show();
                        clearFields();
                        generateNextItemID();
                    }
                } catch (SQLException e) {
                    new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
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
                boolean isDeleted = productModel.deleteItems(id);
                if (isDeleted){
                    new Alert(Alert.AlertType.CONFIRMATION,"Product is Deleted").show();
                    clearFields();
                    generateNextItemID();
                    loadAllProducts();
                }else {
                    new Alert(Alert.AlertType.ERROR,"Product is Not Deleted").show();
                }
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
            }
        }
    }

    @FXML
    void txtSearchOnActon(ActionEvent event) {
        String searchInput = txtSearch.getText();

        try {
            ProductDto itemDto;
            if (searchInput.matches("[I][0-9]{3,}")) {
                itemDto = productModel.searchProductById(searchInput);
            }else {
                itemDto = productModel.searchProductByName(searchInput);
            }
            if (itemDto != null ){
                lblProductID.setText(itemDto.getProduct_id());
                txtDescription.setText(itemDto.getDescription());
                txtQty.setText(String.valueOf(itemDto.getQty()));
                txtPrice.setText(String.valueOf(itemDto.getPrice()));
            }else {
                lblProductID.setText("");
                generateNextItemID();
                new Alert(Alert.AlertType.CONFIRMATION,"Product Not Found").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }
}
