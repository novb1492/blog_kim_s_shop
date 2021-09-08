package com.example.blog_kim_s_token.model.product;



import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class getPriceDto {
    
    @NotBlank
    private String productName;
    @Min(value = 0,message = "수량이 0보다 작습니다")
    private int count;
}
