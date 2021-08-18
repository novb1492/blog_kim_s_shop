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
    private List<Integer>times;
}
