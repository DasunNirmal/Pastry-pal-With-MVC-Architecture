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
import lk.ijse.PastryPal.dto.ItemDto;
import lk.ijse.PastryPal.dto.SupplierDto;
import lk.ijse.PastryPal.dto.tm.ItemTm;
import lk.ijse.PastryPal.model.ItemModel;
import lk.ijse.PastryPal.model.SupplierModel;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ItemFormController {
    @FXML
    private TableColumn<?, ?> colDescription;

    @FXML
    private TableColumn<?, ?> colItemID;

    @FXML
    private TableColumn<?, ?> colQty;

    @FXML
    private TableColumn<?, ?> colSupplierID;

    @FXML
    private TableColumn<?, ?> colSupplierName;

    @FXML
    private Label lblDate;

    @FXML
    private Label lblItemsID;

    @FXML
    private Label lblSupplierID;

    @FXML
    private Label lblSupplierName;

    @FXML
    private Label lblSupplierPhoneNumber;

    @FXML
    private Label lblTime;

    @FXML
    private TableView<ItemTm> tblItem;

    @FXML
    private TextField txtProductName;

    @FXML
    private TextField txtQty;

    @FXML
    private TextField txtSearchItems;

    @FXML
    private TextField txtSearchSupplier;

    private ItemModel itemModel = new ItemModel();
    private SupplierModel supplierModel = new SupplierModel();

    public void initialize(){
        setDateAndTime();
        generateNextItemID();
        loadAllItems();
        setValueFactory();
    }

    private void generateNextItemID() {
        try {
            String previousItemID = lblItemsID.getText();
            String itemID = itemModel.generateNextItemID();
            lblItemsID.setText(itemID);
            if (btnClearPressed){
                lblItemsID.setText(previousItemID);
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    private boolean btnClearPressed = false;

    @FXML
    public void btnClearOnAction(ActionEvent actionEvent) {
        clearFields();
        generateNextItemID();
    }

    private void clearFields(){
        txtProductName.setText("");
        txtQty.setText("");
        txtSearchSupplier.setText("");
        txtSearchItems.setText("");
        lblSupplierID.setText("");
        lblSupplierName.setText("");
        lblSupplierPhoneNumber.setText("");
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
        colItemID.setCellValueFactory(new PropertyValueFactory<>("item_id"));
        colSupplierID.setCellValueFactory(new PropertyValueFactory<>("supplier_id"));
        colSupplierName.setCellValueFactory(new PropertyValueFactory<>("supplier_name"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("product_name"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
    }
    private void loadAllItems() {
        ObservableList<ItemTm> obList = FXCollections.observableArrayList();
        try {
            List<ItemDto> dtoList = itemModel.getAllItems();
            for (ItemDto dto : dtoList){
                obList.add(
                        new ItemTm(
                                dto.getItem_id(),
                                dto.getSupplier_id(),
                                dto.getSupplier_name(),
                                dto.getProduct_name(),
                                dto.getQty()
                        )
                );
            }
            tblItem.setItems(obList);
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        String id = lblItemsID.getText();
        String product_name = txtProductName.getText();
        String qtyText = txtQty.getText();
        String s_id = lblSupplierID.getText();
        String name = lblSupplierName.getText();
        String tele = lblSupplierPhoneNumber.getText();

        boolean isValidName = RegExPatterns.getValidName().matcher(product_name).matches();
        boolean isValidQty = RegExPatterns.getValidDouble().matcher(qtyText).matches();

        if (!isValidName){
            new Alert(Alert.AlertType.ERROR,"Not a Valid Product Name").showAndWait();
            return;
        }if (!isValidQty){
            new Alert(Alert.AlertType.ERROR,"Not a Valid Quantity").showAndWait();
        }else {
            try {
                double qty = Double.parseDouble(qtyText);

                var dto = new ItemDto(id,product_name,qty,s_id,name,tele);
                try {
                    boolean isSaved = itemModel.saveItems(dto);
                    if (isSaved){
                        new Alert(Alert.AlertType.CONFIRMATION,"Item is Saved").show();
                        clearFields();
                        generateNextItemID();
                        loadAllItems();
                    }else {
                        new Alert(Alert.AlertType.ERROR,"Item is not Saved").show();
                    }
                } catch (SQLException e) {
                    new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
                }
            }catch (NumberFormatException e) {
                new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
            }
        }
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        String id = lblItemsID.getText();
        String product_name = txtProductName.getText();
        String qtyText = txtQty.getText();

        boolean isValidName = RegExPatterns.getValidName().matcher(product_name).matches();
        boolean isValidQty = RegExPatterns.getValidDouble().matcher(qtyText).matches();

        if (!isValidName){
            new Alert(Alert.AlertType.ERROR,"Product Name Cn not be Empty").showAndWait();
            return;
        }if (!isValidQty){
            new Alert(Alert.AlertType.ERROR,"Not a Valid Quantity").showAndWait();
        }else {
            try {
                boolean isDeleted = itemModel.deleteItems(id);
                if (isDeleted){
                    new Alert(Alert.AlertType.CONFIRMATION,"Item is Deleted").show();
                    clearFields();
                    generateNextItemID();
                    loadAllItems();
                }else {
                    new Alert(Alert.AlertType.ERROR,"Item is not Deleted").show();
                }
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
            }
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        String id = lblItemsID.getText();
        String product_name = txtProductName.getText();
        String qtyText = txtQty.getText();
        String s_id = lblSupplierID.getText();
        String name = lblSupplierName.getText();
        String tele = lblSupplierPhoneNumber.getText();

        boolean isValidName = RegExPatterns.getValidName().matcher(product_name).matches();
        boolean isValidQty = RegExPatterns.getValidDouble().matcher(qtyText).matches();

        if (!isValidName){
            new Alert(Alert.AlertType.ERROR,"Not a Valid Product Name").showAndWait();
            return;
        }if (!isValidQty){
            new Alert(Alert.AlertType.ERROR,"Not a Valid Quantity").showAndWait();
        }else {
            try {
                double qty = Double.parseDouble(qtyText);

                var dto = new ItemDto(id,product_name,qty,s_id,name,tele);
                try {
                    boolean isUpdated = itemModel.updateItems(dto);
                    if (isUpdated){
                        new Alert(Alert.AlertType.CONFIRMATION,"Item is Updated").show();
                        clearFields();
                        generateNextItemID();
                        loadAllItems();
                    }else {
                        new Alert(Alert.AlertType.ERROR,"Item is not Updated").show();
                    }
                } catch (SQLException e) {
                    new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
                }
            }catch (NumberFormatException e) {
                new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
            }
        }
    }

    @FXML
    void txtSearchItemsOnAction(ActionEvent event) {
        String searchInput = txtSearchItems.getText();

        try {
            ItemDto itemDto;
            if (searchInput.matches("[I][0-9]{3,}")) {
                itemDto = itemModel.searchProductById(searchInput);
            }else {
                itemDto = itemModel.searchProductByName(searchInput);
            }
            if (itemDto != null ){
                lblItemsID.setText(itemDto.getItem_id());
                txtProductName.setText(itemDto.getProduct_name());
                txtQty.setText(String.valueOf(itemDto.getQty()));
                lblSupplierID.setText(itemDto.getSupplier_id());
                lblSupplierName.setText(itemDto.getSupplier_name());
                lblSupplierPhoneNumber.setText(String.valueOf(itemDto.getSupplier_phone_number()));
            }else {
                lblItemsID.setText("");
                generateNextItemID();
                new Alert(Alert.AlertType.CONFIRMATION,"Product Not Found").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }
    @FXML
    void txtSearchSupplierOnAction(ActionEvent event) {
        String searchInput = txtSearchSupplier.getText();

        try {
            SupplierDto supplierDto;
            if (searchInput.matches("[S][0-9]{3,}")) {
                supplierDto = supplierModel.searchSupplierById(searchInput);
            } else {
                supplierDto = supplierModel.searchSupplierByPhoneNumber(searchInput);
            }
            if (supplierDto != null){
                lblSupplierID.setText(supplierDto.getSupplier_id());
                lblSupplierName.setText(supplierDto.getName());
                lblSupplierPhoneNumber.setText(supplierDto.getPhone_number());
                txtSearchSupplier.setText("");
            } else {
                lblSupplierID.setText("");
                lblSupplierName.setText("");
                lblSupplierPhoneNumber.setText("");
                new Alert(Alert.AlertType.INFORMATION,"Supplier Not Found").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }
}
