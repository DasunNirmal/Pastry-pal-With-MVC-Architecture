package lk.ijse.PastryPal.dto.tm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderTm {
    private String product_id;
    private String description;
    private double unit_price;
    private int qty;
    private double total;
}
