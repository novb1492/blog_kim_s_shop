package com.example.blog_kim_s_token.service.reservation;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class tryTempDto {
    private String seat;
    private String month;
    private String date;
    private String year;
    private String status;
    private List<Integer> times;
    private String usedKind;
}
