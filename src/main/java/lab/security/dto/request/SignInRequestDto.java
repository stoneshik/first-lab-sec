package lab.security.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignInRequestDto {
    @NotBlank
    private String login;
    @NotBlank
    private String password;
}
