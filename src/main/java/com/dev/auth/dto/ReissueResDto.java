package com.dev.auth.dto;

import com.dev.jwt.dto.TokenDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReissueResDto {
    private String username;
    private TokenDto tokenDto;
}
