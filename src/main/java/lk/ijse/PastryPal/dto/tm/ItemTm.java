package lk.ijse.PastryPal.dto.tm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ItemTm {
    private String item_id;
    private String supplier_id;
    private String supplier_name;
    private String product_name;
    private double qty;
}
