package com.medimarket.api.customer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordDto {
    @NotBlank(message = "email can't be empty")
    @Pattern(regexp = "^.{6,}$", message = "more than 6 character")
    private String password;
}
