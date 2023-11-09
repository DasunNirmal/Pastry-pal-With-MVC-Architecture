package lk.ijse.PastryPal.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CustomerDto {

    private String customer_id;
    private String name;
    private String address;
    private int phone_number;
}