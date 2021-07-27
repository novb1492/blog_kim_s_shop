package com.example.blog_kim_s_token.model.confrim;

import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class phoneCofrimDto {

    @NotBlank
    private String phoneNum;
    @NotBlank
    private String tempNum;
}
