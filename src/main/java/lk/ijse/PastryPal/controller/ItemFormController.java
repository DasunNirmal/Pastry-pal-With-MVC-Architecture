package lk.ijse.PastryPal.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import lk.ijse.PastryPal.RegExPatterns.RegExPatterns;
import lk.ijse.PastryPal.dto.ItemDto;
import lk.ijse.PastryPal.model.ItemModel;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ItemFormController {


    @FXML
    private TableColumn<?, ?> colDescription;

    @FXML
    private TableColumn<?, ?> colItemID;

    @FXML
    private TableColumn<?, ?> colPrice;

    @FXML
    private TableColumn<?, ?> colQtyOnHand;

    @FXML
    private Label lblDate;

    @FXML
    private Label lblTime;

    @FXML
    private Label lblItemID;

    @FXML
    private TextField txtDescription;

    @FXML
    private TextField txtPrice;

    @FXML
    private TextField txtQty;

    @FXML
    private TextField txtSearch;

    private ItemModel itemModel = new ItemModel();

    public void initialize(){
        setDateAndTime();
        generateNextItemID();
    }

    private void generateNextItemID() {
        try {
            String previousItemID = lblItemID.getId();
            String itemID = itemModel.generateNextItemID();
            lblItemID.setText(itemID);
            clearFields();
            if (btnClearPressed){
                lblItemID.setText(previousItemID);
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

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        String id = lblItemID.getText();
        String description = txtDescription.getText();
        //the reason for this is matcher only accepts String not double
        String qtyText = txtQty.getText();
        String priceText = txtPrice.getText();

        boolean isValidDescription = RegExPatterns.getValidNameAndDescriptions().matcher(description).matches();
        boolean isValidQty = RegExPatterns.getValidDouble().matcher(qtyText).matches();
        boolean isValidPrice = RegExPatterns.getValidDouble().matcher(priceText).matches();

        if (!isValidDescription) {
            new Alert(Alert.AlertType.ERROR, "Can not Save Item.Description is Empty").showAndWait();
            return;
        }if (!isValidQty){
            new Alert(Alert.AlertType.ERROR,"Can not Save Item.Quantity is Empty").showAndWait();
            return;
        }if (!isValidPrice){
            new Alert(Alert.AlertType.ERROR,"Can not Save Item.Price is Empty").showAndWait();
        }else {
            try {
                //the reason for this is qty and price takes double and can't take String
                double qty = Double.parseDouble(qtyText);
                double price = Double.parseDouble(priceText);

                var dto = new ItemDto(id, description, qty, price);
                try {
                    boolean isSaved = itemModel.saveItem(dto);
                    if (isSaved) {
                        new Alert(Alert.AlertType.CONFIRMATION, "Item Is Saved").show();
                        clearFields();
                        generateNextItemID();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Item Is Not Saved").show();
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
        String itemId = lblItemID.getText();
        String desc = txtDescription.getText();
        String qtyText = txtQty.getText();
        String priceText = txtPrice.getText();

        try {
            double qty = Double.parseDouble(qtyText);
            double price = Double.parseDouble(priceText);

            var dto = new ItemDto(itemId,desc,qty,price);
            try {
                boolean isUpdated = itemModel.updateItems(dto);
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

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        String itemId = lblItemID.getText();
        String desc = txtDescription.getText();
        String qtyText = txtQty.getText();
        String priceText = txtPrice.getText();


        try {
            boolean isDeleted = itemModel.deleteItems(itemId);
            if (isDeleted){
                new Alert(Alert.AlertType.CONFIRMATION,"Item is Deleted").show();
                clearFields();
                generateNextItemID();
            }else {
                new Alert(Alert.AlertType.ERROR,"Item is Not Deleted").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }

    @FXML
    void txtSearchOnActon(ActionEvent event) {
        String searchInput = txtSearch.getText();

        try {
            ItemDto itemDto;
            if (searchInput.matches("[I][0-9]{3,}")) {
                itemDto = itemModel.searchItemById(searchInput);
            }else {
                itemDto = itemModel.searchItemByName(searchInput);
            }
            if (itemDto != null ){
                lblItemID.setText(itemDto.getItem_id());
                txtDescription.setText(itemDto.getDescription());
                txtQty.setText(String.valueOf(itemDto.getQty()));
                txtPrice.setText(String.valueOf(itemDto.getPrice()));
            }else {
                lblItemID.setText("");
                generateNextItemID();
                new Alert(Alert.AlertType.CONFIRMATION,"Item Not Found").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }
}
