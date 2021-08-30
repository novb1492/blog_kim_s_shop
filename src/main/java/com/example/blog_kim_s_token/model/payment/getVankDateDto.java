package com.example.blog_kim_s_token.model.payment;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class getVankDateDto {
    private int year;
    private int month;
    private int date;
    private List<Integer>times;

}
