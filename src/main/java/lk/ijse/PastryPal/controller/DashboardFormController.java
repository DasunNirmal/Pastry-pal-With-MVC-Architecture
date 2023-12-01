package lk.ijse.PastryPal.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import lk.ijse.PastryPal.DB.DbConnection;
import lk.ijse.PastryPal.dto.DetailsDto;
import lk.ijse.PastryPal.dto.tm.DetailsTm;
import lk.ijse.PastryPal.model.DetailsModel;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DashboardFormController {

    @FXML
    private TableColumn<?, ?> colCustomer;

    @FXML
    private TableColumn<?, ?> colDate;

    @FXML
    private TableColumn<?, ?> colName;

    @FXML
    private TableColumn<?, ?> colOrderId;

    @FXML
    private TableColumn<?, ?> colProductID;

    @FXML
    private TableColumn<?, ?> colQty;

    @FXML
    private TableColumn<?, ?> colUnitPrice;

    @FXML
    private Label lblDate;

    @FXML
    private Label lblTime;

    @FXML
    private Label lblTotalOrders;

    @FXML
    private Label lblSales;

    @FXML
    private Label lblTotalComplains;

    @FXML
    private Label lblTotalCustomers;

    @FXML
    private Label lblOrdersByDate;

    @FXML
    private TableView<DetailsTm> tblOrderDetails;
    private DetailsModel detailsModel = new DetailsModel();
    private ObservableList<DetailsTm> obList = FXCollections.observableArrayList();

    public void initialize() throws SQLException {
        setDateAndTime();
        loadAllDetails();
        setCellValueFactory();
        countOrders();
        TotalSales();
        countOrdersByDate();
        totalCustomers();
        totalComplains();
    }

    private void totalComplains() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();
        Statement statement = connection.createStatement();

        String sql = "SELECT count(*) FROM complains";
        ResultSet resultSet = statement.executeQuery(sql);
        resultSet.next();
        int count = resultSet.getInt(1);
        lblTotalComplains.setText(String.valueOf(count));
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

    private void countOrdersByDate() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();
        Statement statement = connection.createStatement();

        String sql = "SELECT COUNT(*) FROM orders WHERE DATE(order_date) = CURDATE();";
        ResultSet resultSet = statement.executeQuery(sql);
        resultSet.next();
        int count = resultSet.getInt(1);
        lblOrdersByDate.setText(String.valueOf(count));
    }

    private void countOrders() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();
        Statement statement = connection.createStatement();

        String sql = "SELECT count(*) FROM orders";
        ResultSet resultSet = statement.executeQuery(sql);
        resultSet.next();
        int count = resultSet.getInt(1);
        lblTotalOrders.setText(String.valueOf(count));
    }
    private void TotalSales() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();
        Statement statement = connection.createStatement();

        String sql = "SELECT SUM(unit_price) AS total_sum FROM order_details;";
        ResultSet resultSet = statement.executeQuery(sql);
        resultSet.next();
        int count = resultSet.getInt(1);
        lblSales.setText(String.valueOf(count));
    }

    private void setCellValueFactory() {
        colOrderId.setCellValueFactory(new PropertyValueFactory<>("order_id"));
        colCustomer.setCellValueFactory(new PropertyValueFactory<>("customer_id"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colProductID.setCellValueFactory(new PropertyValueFactory<>("product_id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("description"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unit_price"));
        tblOrderDetails.setId("my-table");

    }

    private void loadAllDetails() {
        try {
            obList.clear();
            List<DetailsDto> dtoList = detailsModel.getAllDetails();
            for (DetailsDto dto : dtoList) {
                obList.add(
                        new DetailsTm(
                                dto.getOrder_id(),
                                dto.getCustomer_id(),
                                dto.getDate(),
                                dto.getProduct_id(),
                                dto.getDescription(),
                                dto.getQty(),
                                dto.getUnit_price()
                        )
                );
            }
            tblOrderDetails.setItems(obList);
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
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
}
