package lk.ijse.PastryPal.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainFormController {
    @FXML
    private AnchorPane mainNode;

    public void initialize() throws IOException {
        AnchorPane anchorPane = FXMLLoader.load(getClass().getResource("/view/dashboard_form.fxml"));
        this.mainNode.getChildren().clear();
        this.mainNode.getChildren().add(anchorPane);
    }

    @FXML
    void btnDashBoardOnAction(ActionEvent event) throws IOException {
        AnchorPane anchorPane = FXMLLoader.load(this.getClass().getResource("/view/dashboard_form.fxml"));
        this.mainNode.getChildren().clear();
        this.mainNode.getChildren().add(anchorPane);
    }

    @FXML
    void btnCustomerOnAction(ActionEvent event) throws IOException {
        AnchorPane anchorPane = FXMLLoader.load(this.getClass().getResource("/view/customer_form.fxml"));
        this.mainNode.getChildren().clear();
        this.mainNode.getChildren().add(anchorPane);
    }

    @FXML
    void btnOrderOnAction(ActionEvent event) throws IOException {
        AnchorPane anchorPane = FXMLLoader.load(this.getClass().getResource("/view/Order_form.fxml"));
        this.mainNode.getChildren().clear();
        this.mainNode.getChildren().add(anchorPane);
    }

    @FXML
    void btnSupplierOnAction(ActionEvent event) throws IOException {
        AnchorPane anchorPane = FXMLLoader.load(this.getClass().getResource("/view/supplier_form.fxml"));
        this.mainNode.getChildren().clear();
        this.mainNode.getChildren().add(anchorPane);
    }

    @FXML
    void btnMaterialsOnAction(ActionEvent event) throws IOException {
        AnchorPane anchorPane = FXMLLoader.load(this.getClass().getResource("/view/materials_form.fxml"));
        this.mainNode.getChildren().clear();
        this.mainNode.getChildren().add(anchorPane);
    }

    @FXML
    void btnItemsOnAction(ActionEvent event) throws IOException {
        AnchorPane anchorPane = FXMLLoader.load(this.getClass().getResource("/view/item_form.fxml"));
        this.mainNode.getChildren().clear();
        this.mainNode.getChildren().add(anchorPane);
    }

    @FXML
    void btnEmployeeOnAction(ActionEvent event) throws IOException {
        AnchorPane anchorPane = FXMLLoader.load(this.getClass().getResource("/view/employee_form.fxml"));
        this.mainNode.getChildren().clear();
        this.mainNode.getChildren().add(anchorPane);
    }

    @FXML
    void btnComplainsOnAction(ActionEvent event) throws IOException {
        AnchorPane anchorPane = FXMLLoader.load(this.getClass().getResource("/view/complains_form.fxml"));
        this.mainNode.getChildren().clear();
        this.mainNode.getChildren().add(anchorPane);
    }

    @FXML
    void btnLogoutOnAction(ActionEvent event) throws IOException {
        mainNode.getScene().getWindow().hide();
        Stage stage = new Stage();
        stage.setScene(new Scene(FXMLLoader.load(this.getClass().getResource("/view/login_form.fxml"))));
        stage.centerOnScreen();
        stage.show();
    }
}
