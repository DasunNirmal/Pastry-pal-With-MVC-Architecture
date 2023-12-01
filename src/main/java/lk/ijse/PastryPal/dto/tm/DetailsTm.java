package lk.ijse.PastryPal.dto.tm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DetailsTm {
    private String order_id;
    private String customer_id;
    private LocalDate date;
    private String product_id;
    private String description;
    private int qty;
    private double unit_price;
}
