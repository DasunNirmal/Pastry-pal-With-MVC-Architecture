package lk.ijse.PastryPal.dto.tm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SupplierTm {
    private String supplier_id;
    private String name;
    private LocalDate date;
    private String phone_number;
}
