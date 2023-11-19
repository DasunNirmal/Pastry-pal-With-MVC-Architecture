package lk.ijse.PastryPal.dto.tm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemTm {
    private String item_id;
    private String description;
    private double qty;
    private double price;
}
