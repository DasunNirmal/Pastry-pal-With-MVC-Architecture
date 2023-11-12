package lk.ijse.PastryPal.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import lk.ijse.PastryPal.dto.RegistrationDto;
import lk.ijse.PastryPal.model.RegistrationModel;
import org.checkerframework.checker.units.qual.A;

import java.io.IOException;
import java.sql.SQLException;

public class RegisterFormController {

    @FXML
    private TextField txtPassword;
    @FXML
    private PasswordField txtConfirmPassword;
    @FXML
    private TextField txtUser;
    @FXML
    private AnchorPane RegisterPane;
    private RegistrationModel registrationModel = new RegistrationModel();

    @FXML
    void btnBackOnAction(ActionEvent event) throws IOException {
        AnchorPane anchorPane = FXMLLoader.load(this.getClass().getResource("/view/login_form.fxml"));
        Scene scene = new Scene(anchorPane);
        Stage stage = (Stage) this.RegisterPane.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Login");
        stage.centerOnScreen();
    }

    @FXML
    void btnRegisterOnAction(ActionEvent event) {
        String user = txtUser.getText();
        String pw = txtPassword.getText();
        String ConfirmPW = txtConfirmPassword.getText();

        if (!ConfirmPW.equals(pw) || user.isEmpty() || pw.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Can Not Leave Password or User Name Empty!").showAndWait();
            return;
        }

        var dto = new RegistrationDto(user, pw);
        try {
            boolean checkDuplicates = registrationModel.check(user, pw);
            if (checkDuplicates) {
                new Alert(Alert.AlertType.ERROR, "Duplicate Entry").showAndWait();
                return;
            }

            boolean isRegistered = registrationModel.registerUser(dto);
            if (isRegistered) {
                new Alert(Alert.AlertType.CONFIRMATION, "Your Account Has been Created").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    @FXML
    void txtRegisterOnAction(ActionEvent event) {
        String user = txtUser.getText();
        String pw = txtPassword.getText();
        String ConfirmPW = txtConfirmPassword.getText();

        if (!ConfirmPW.equals(pw) || user.isEmpty() || pw.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Can Not Leave Password or User Name Empty!").showAndWait();
            return;
        }

        var dto = new RegistrationDto(user, pw);
        try {
            boolean checkDuplicates = registrationModel.check(user, pw);
            if (checkDuplicates) {
                new Alert(Alert.AlertType.ERROR, "Duplicate Entry").showAndWait();
                return;
            }

            boolean isRegistered = registrationModel.registerUser(dto);
            if (isRegistered) {
                new Alert(Alert.AlertType.CONFIRMATION, "Your Account Has been Created").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }
}
