package lk.ijse.PastryPal.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class EmployeeFormController {

    @FXML
    private TableColumn<?, ?> colCustomerId;

    @FXML
    private TableColumn<?, ?> colCustomerId1;

    @FXML
    private TableColumn<?, ?> colDescription;

    @FXML
    private TableColumn<?, ?> colOrderID;

    @FXML
    private TableColumn<?, ?> colPrice;

    @FXML
    private Label lblDate;

    @FXML
    private Label lblTime;

    public void initialize(){
        setDateAndTime();
    }
    private void setDateAndTime(){
        lblDate.setText(String.valueOf(LocalDate.now()));

        SimpleDateFormat simpleTime = new SimpleDateFormat("hh.mm.aa");
        Date date = new Date();
        String time = simpleTime.format(date);
        lblTime.setText(time);
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {

    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {

    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {

    }

}
