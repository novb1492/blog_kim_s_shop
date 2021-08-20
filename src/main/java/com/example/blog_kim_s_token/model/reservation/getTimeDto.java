package com.example.blog_kim_s_token.model.reservation;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class getTimeDto {
    
    @Min(value = 1,message = "일이 1보다 작습니다")
    @Max(value = 31,message = "일이 31보다 큽니다")
    private int date;

    @Min(value = 1,message = "월이 1보다 작습니다")
    @Max(value = 12,message = "월이 12보다 큽니다")
    private int month;

    @Min(value = 2021,message = "연도가 2021보다 작습니다")
    private int year;

    @NotBlank(message = "자리가 빈칸입니다")
    private String seat;
}
