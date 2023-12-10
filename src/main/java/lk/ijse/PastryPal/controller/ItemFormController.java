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
import lk.ijse.PastryPal.dto.ItemDto;
import lk.ijse.PastryPal.dto.SupplierDto;
import lk.ijse.PastryPal.dto.tm.CustomerTm;
import lk.ijse.PastryPal.dto.tm.ItemTm;
import lk.ijse.PastryPal.model.ItemModel;
import lk.ijse.PastryPal.model.SupplierModel;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

    @FXML
    private Label lblItemsCount;

    @FXML
    private Label lblItemSaveOrNot;

    @FXML
    private TableColumn<?, ?> colPhoneNumber;

    private ItemModel itemModel = new ItemModel();
    private SupplierModel supplierModel = new SupplierModel();
    private ObservableList<ItemTm> obList = FXCollections.observableArrayList();

    public void initialize() throws SQLException {
        setDateAndTime();
        generateNextItemID();
        loadAllItems();
        setValueFactory();
        tableListener();
        totalItem();
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
        lblItemSaveOrNot.setText("");
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
        colPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        tblItem.setId("my-table");
    }
    private void tableListener() {
        tblItem.getSelectionModel().selectedItemProperty().addListener((observable, oldValued, newValue) -> {
            setData(newValue);
        });
    }

    private void setData(ItemTm row) {
        if (row != null) {
            lblItemsID.setText(row.getItem_id());
            lblSupplierID.setText(row.getSupplier_id());
            lblSupplierName.setText(row.getSupplier_name());
            txtProductName.setText(row.getProduct_name());
            lblSupplierPhoneNumber.setText(row.getPhoneNumber());
            txtQty.setText(String.valueOf(row.getQty()));
        }
    }
    private void totalItem() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();
        Statement statement = connection.createStatement();

        String sql = "SELECT count(*) FROM items";
        ResultSet resultSet = statement.executeQuery(sql);
        resultSet.next();
        int count = resultSet.getInt(1);
        lblItemsCount.setText(String.valueOf(count));
    }
    private void loadAllItems() {
        try {
            obList.clear();
            List<ItemDto> dtoList = itemModel.getAllItems();
            for (ItemDto dto : dtoList){
                obList.add(
                        new ItemTm(
                                dto.getItem_id(),
                                dto.getSupplier_id(),
                                dto.getSupplier_name(),
                                dto.getSupplier_phone_number(),
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
                        obList.clear();
                        generateNextItemID();
                        loadAllItems();
                        totalItem();
                        lblItemSaveOrNot.setText("Item is Saved !");
                        PauseTransition pause = new PauseTransition(Duration.seconds(2));
                        pause.setOnFinished(pauseEvent -> {
                            clearFields();
                        });
                        pause.play();
                    }else {
                        lblItemSaveOrNot.setText("Item is Not Saved !");
                        PauseTransition pause = new PauseTransition(Duration.seconds(2));
                        pause.setOnFinished(pauseEvent -> {
                            lblItemSaveOrNot.setText("");
                        });
                        pause.play();
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
                    obList.clear();
                    generateNextItemID();
                    loadAllItems();
                    totalItem();
                    lblItemSaveOrNot.setText("Item is Deleted !");
                    PauseTransition pause = new PauseTransition(Duration.seconds(2));
                    pause.setOnFinished(pauseEvent -> {
                        clearFields();
                    });
                    pause.play();
                }else {
                    lblItemSaveOrNot.setText("Item is Not Deleted !");
                    PauseTransition pause = new PauseTransition(Duration.seconds(2));
                    pause.setOnFinished(pauseEvent -> {
                        lblItemSaveOrNot.setText("");
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
                        obList.clear();
                        generateNextItemID();
                        loadAllItems();
                        totalItem();
                        lblItemSaveOrNot.setText("Item is Updated !");
                        PauseTransition pause = new PauseTransition(Duration.seconds(2));
                        pause.setOnFinished(pauseEvent -> {
                            clearFields();
                        });
                        pause.play();
                    }else {
                        lblItemSaveOrNot.setText("Item is Not Updated !");
                        PauseTransition pause = new PauseTransition(Duration.seconds(2));
                        pause.setOnFinished(pauseEvent -> {
                            lblItemSaveOrNot.setText("");
                        });
                        pause.play();
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
    @FXML
    void txtSearchItemsOnAction(KeyEvent event) {
        searchTableFilter();
    }
    private void searchTableFilter() {
        FilteredList<ItemTm> filterItemTbl = new FilteredList<>(obList, b -> true);
        txtSearchItems.textProperty().addListener((observable, oldValue, newValue) -> {
            filterItemTbl.setPredicate(itemTm -> {
                if (newValue.isEmpty() || newValue.isBlank() || newValue == null) {
                    return true;
                }
                String search = newValue.toLowerCase();

                if (itemTm.getItem_id().toLowerCase().contains(search)) {
                    return true;
                } else if (itemTm.getItem_id().toLowerCase().contains(search)) {
                    return true;
                } else if (itemTm.getSupplier_id().toLowerCase().contains(search)) {
                    return true;
                } else if (itemTm.getSupplier_id().toLowerCase().contains(search)) {
                    return true;
                } else if (itemTm.getSupplier_name().toLowerCase().contains(search)) {
                    return true;
                } else if (itemTm.getSupplier_name().toLowerCase().contains(search)) {
                    return true;
                } else if (itemTm.getProduct_name().toLowerCase().contains(search)) {
                    return true;
                } else if (itemTm.getProduct_name().toLowerCase().contains(search)) {
                    return true;
                } else
                    return false;
            });
        });
        SortedList<ItemTm> sortCustomerTbl = new SortedList<>(filterItemTbl);
        sortCustomerTbl.comparatorProperty().bind(tblItem.comparatorProperty());
        tblItem.setItems(sortCustomerTbl);
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
