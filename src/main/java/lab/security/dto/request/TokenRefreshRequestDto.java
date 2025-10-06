package lab.security.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TokenRefreshRequestDto {
    @NotBlank
    private String refreshToken;
}
