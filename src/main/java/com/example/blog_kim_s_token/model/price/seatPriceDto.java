package com.example.blog_kim_s_token.model.price;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class seatPriceDto {
    private String seat;
    private List<Integer>times;
}
