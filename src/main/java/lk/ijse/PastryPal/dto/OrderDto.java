package lk.ijse.PastryPal.dto;

import lk.ijse.PastryPal.dto.tm.OrderTm;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderDto {
    private String order_id;
    private LocalDate date;
    private String customer_id;
    private List<OrderTm> orderTmList = new ArrayList<>();
}
