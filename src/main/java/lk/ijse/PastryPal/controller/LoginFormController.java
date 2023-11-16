package lk.ijse.PastryPal.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.PastryPal.dto.RegistrationDto;
import lk.ijse.PastryPal.model.RegistrationModel;

import java.io.IOException;
import java.sql.SQLException;

public class LoginFormController {

    @FXML
    private PasswordField txtPassword;
    @FXML
    private TextField txtUser;
    @FXML
    private AnchorPane rootNode;
    private RegistrationModel registrationModel = new RegistrationModel();

    @FXML
    void btnLoginOnAction(ActionEvent event) throws IOException {
        String userName = txtUser.getText();
        String pw = txtPassword.getText();

        try {
            boolean isValid = registrationModel.isValidUser(userName,pw);
            if (isValid){

                registrationModel.getUserInfo(userName); //Create a method in the model where the query is executed
                rootNode.getScene().getWindow().hide();
                Stage stage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main_form.fxml"));
                Parent root = loader.load();
                MainFormController mainFormController  = loader.getController(); //passing the values (login form text field values) to the main controller

                RegistrationDto userDto = registrationModel.getUserInfo(userName); //setting the getUserInfo methods values
                mainFormController.setUser(userDto); //finally, passing the user info to setUser method
                stage.setScene(new Scene(root));
                stage.centerOnScreen();
                stage.setResizable(false);
                stage.show();
            }else {
                new Alert(Alert.AlertType.ERROR,"User Name And Password Did Not Matched try again").showAndWait();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    @FXML
    void txtLoginOnAction(ActionEvent event) throws IOException {
        btnLoginOnAction(new ActionEvent());
    }

    @FXML
    void hyperSignUpOnAction(ActionEvent event) throws IOException {
        AnchorPane anchorPane = FXMLLoader.load(this.getClass().getResource("/view/register_form.fxml"));
        Scene scene = new Scene(anchorPane);
        Stage stage = (Stage) this.rootNode.getScene().getWindow();
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.setTitle("Register");
    }

    @FXML
    void txtGoToPasswordOnAction(ActionEvent event) {
        txtPassword.requestFocus();
    }
}
