package lab.blps.security.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class LogoutRequestDto {
    @Min(1)
    private Long userId;
}
