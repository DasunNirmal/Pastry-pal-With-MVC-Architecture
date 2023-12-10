package lk.ijse.PastryPal.dto.tm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductTm {
    private String product_id;
    private String description;
    private int qty;
    private double price;
}
