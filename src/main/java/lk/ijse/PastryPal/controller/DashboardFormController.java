package lk.ijse.PastryPal.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class DashboardFormController {

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
}
