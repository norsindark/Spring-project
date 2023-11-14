package com.medimarket.api.users;

//import com.medimarket.api.advice.UniqueEmail;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
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
public class UserDto {

    @NotBlank(message = "Username can't be empty")
    @Column(unique = true)
    private String username;

    @NotBlank(message = "password can't be empty")
    @Pattern(regexp = "^.{6,}$", message = "password need more than 6 character")
    private String password;

    @Email
    @Column(unique = true)
//    @UniqueEmail
    @NotBlank(message = "email can't be empty")
    private String email;

    @NotBlank(message = "name can't be empty")
    private String name;

    private Boolean enabled;

    @NotBlank(message = "address can't be empty")
    private String address;

    @NotBlank(message = "phone number can't be empty")
    private String phone;

}
