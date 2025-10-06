package lab.blps.security.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class SignUpRequestDto {
    @NotBlank
    private String login;
    private Set<String> role;
    @NotBlank
    @Size(min = 4, max = 40)
    private String password;
}
