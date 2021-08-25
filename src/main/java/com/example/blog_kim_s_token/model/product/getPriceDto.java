package com.example.blog_kim_s_token.model.product;

import java.util.List;

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
    private List<Integer>count;
}
