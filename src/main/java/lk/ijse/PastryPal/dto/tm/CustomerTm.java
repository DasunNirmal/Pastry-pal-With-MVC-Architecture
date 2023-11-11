package lk.ijse.PastryPal.dto.tm;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomerTm {
    private String Customer_id;
    private String name;
    private String address;
    private int phone_number;
}
