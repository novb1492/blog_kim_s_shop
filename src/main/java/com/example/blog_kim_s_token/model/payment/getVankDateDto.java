package com.example.blog_kim_s_token.model.payment;

import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class getVankDateDto {
    @Min(value = 2021,message = "년이 2021보다 작습니다")
    private int year;

    @Min(value = 1,message = "월이 1보다 작습니다")
    @Max(value = 12,message = "월이 12월보다 큽니다")
    private int month;
    
    @Min(value = 1,message = "일이 1보다 작습니다")
    @Max(value = 31,message = "월이 31보다 큽니다")
    private int date;
    private List<Integer>times;

    @NotBlank(message = "종류가 공백입니다")
    private String kind;

}
