package lk.ijse.PastryPal.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDto {

    private String customer_id;
    private String name;
    private String address;
    private String phone_number;
}