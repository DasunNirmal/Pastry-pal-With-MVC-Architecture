package lk.ijse.PastryPal.controller;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
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
import lk.ijse.PastryPal.dto.ComplainDto;
import lk.ijse.PastryPal.dto.tm.ComplainTm;
import lk.ijse.PastryPal.model.ComplainModel;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ComplainsFormController {
    @FXML
    private TableColumn<?, ?> colComplainID;

    @FXML
    private TableColumn<?, ?> colDate;

    @FXML
    private TableColumn<?, ?> colDescription;

    @FXML
    private TableView<ComplainTm> tblComplains;

    @FXML
    private Label lblComplainID;

    @FXML
    private Label lblDate;

    @FXML
    private Label lblTime;

    @FXML
    private TextArea txtComplain;

    @FXML
    private DatePicker txtDate;

    @FXML
    private TextField txtSearch;

    private ComplainModel complainModel = new ComplainModel();

    public void initialize(){
        setCellValueFactory();
        setDateAndTime();
        generateNextComplainID();
        loadAllComplains();
    }

    private void generateNextComplainID() {
        try {
            String previousComplainID = lblComplainID.getText();
            String complainID = complainModel.generateNextComplainID();
            lblComplainID.setText(complainID);
            clearFields();
            if (btnClearPressed){
                lblComplainID.setText(previousComplainID);
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }

    private boolean btnClearPressed = false;

    @FXML
    void btnClearOnAction(ActionEvent event) {
        clearFields();
        generateNextComplainID();
    }

    private void clearFields(){
        txtComplain.setText("");
        txtDate.setValue(null);
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
    private void setCellValueFactory() {
        colComplainID.setCellValueFactory(new PropertyValueFactory<>("complain_id"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("complain_date"));
    }

    private void loadAllComplains() {
        ObservableList<ComplainTm> obList = FXCollections.observableArrayList();
        try {
            List<ComplainDto> dtoList = complainModel.getAllComplains();
            for (ComplainDto dto : dtoList){
                obList.add(
                        new ComplainTm(
                                dto.getComplain_id(),
                                dto.getDescription(),
                                dto.getComplain_date()
                        )
                );
            }
            tblComplains.setItems(obList);
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        String id = lblComplainID.getText();
        String complain = txtComplain.getText();
        LocalDate date = txtDate.getValue();

        var dto = new ComplainDto(id , complain , date);
        try {
            boolean isSaved = complainModel.saveComplain(dto);
            if (isSaved){
                new Alert(Alert.AlertType.CONFIRMATION,"Complain is saved").show();
                clearFields();
                generateNextComplainID();
                loadAllComplains();
            }else {
                new Alert(Alert.AlertType.ERROR,"Complain is not saved").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        String id = lblComplainID.getText();
        String complain = txtComplain.getText();
        LocalDate date = txtDate.getValue();

        var dto = new ComplainDto(id , complain , date);
        try {
            boolean isUpdated = complainModel.updateComplains(dto);
            if (isUpdated){
                new Alert(Alert.AlertType.CONFIRMATION,"Complain is Updated").show();
                clearFields();
                generateNextComplainID();
                loadAllComplains();
            }else {
                new Alert(Alert.AlertType.ERROR,"Complain is Not Updated").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        String id = lblComplainID.getText();
        String complain = txtComplain.getText();
        LocalDate date = txtDate.getValue();

        try {
            boolean isDeleted = complainModel.deleteComplains(id);
            if (isDeleted){
                new Alert(Alert.AlertType.CONFIRMATION,"Complain is Deleted").show();
                clearFields();
                generateNextComplainID();
                loadAllComplains();
            }else {
                new Alert(Alert.AlertType.ERROR,"Complain is not Deleted").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }

    @FXML
    void txtSearchOnActon(ActionEvent event) {
        String searchInput = txtSearch.getText();

        try {
            ComplainDto complainDto;

            complainDto = complainModel.searchComplainByID(searchInput);

            if (complainDto != null){
                lblComplainID.setText(complainDto.getComplain_id());
                txtComplain.setText(complainDto.getDescription());
                txtDate.setValue(complainDto.getComplain_date());
                txtSearch.setText("");
            }else {
                lblComplainID.setText("");
                generateNextComplainID();
                new Alert(Alert.AlertType.INFORMATION,"Complain Not Found").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }
}
