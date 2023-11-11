package com.medimarket.api.verifyEmails;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyRequest {
    @NotBlank(message = "email can't be empty")
    @Email(message = "invalid email address")
    private String email;
}
