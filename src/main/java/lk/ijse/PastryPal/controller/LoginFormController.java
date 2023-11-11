package lk.ijse.PastryPal.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginFormController {

    @FXML
    private TextField txtPassword;
    @FXML
    private TextField txtUser;
    @FXML
    private AnchorPane rootNode;

    @FXML
    void btnLoginOnAction(ActionEvent event) throws IOException {
        rootNode.getScene().getWindow().hide();
        Stage stage = new Stage();
        stage.setScene(new Scene(FXMLLoader.load(this.getClass().getResource("/view/main_form.fxml"))));
        stage.centerOnScreen();
        stage.show();
    }
    @FXML
    void hyperSignUpOnAction(ActionEvent event) throws IOException {
        AnchorPane anchorPane = FXMLLoader.load(this.getClass().getResource("/view/register_form.fxml"));
        Scene scene = new Scene(anchorPane);
        Stage stage = (Stage) this.rootNode.getScene().getWindow();
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setTitle("Register");
    }
}
