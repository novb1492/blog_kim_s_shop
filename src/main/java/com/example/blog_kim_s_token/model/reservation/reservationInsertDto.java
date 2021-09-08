package com.example.blog_kim_s_token.model.reservation;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class reservationInsertDto {

    private String seat;
    private int month;
    private int date;
    private int year;
    private String paymentId;
    private String name;
    private String email;
    private String status;
    private List<Integer>times;
    private String usedKind;
}
