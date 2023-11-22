package lk.ijse.PastryPal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemDto {
    private String item_id;
    private String product_name;
    private double qty;
    private String supplier_id;
    private String supplier_name;
    private String supplier_phone_number;
}
