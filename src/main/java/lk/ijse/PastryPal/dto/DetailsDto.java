package lk.ijse.PastryPal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DetailsDto {
    private String order_id;
    private String customer_id;
    private LocalDate date;
    private String product_id;
    private String description;
    private int qty;
    private double unit_price;
}
