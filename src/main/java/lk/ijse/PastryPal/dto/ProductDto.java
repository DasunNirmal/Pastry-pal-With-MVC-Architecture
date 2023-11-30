package lk.ijse.PastryPal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductDto {
    private String product_id;
    private String description;
    private int qty;
    private double price;
}
