package lab.blps.security.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class JwtResponseDto {
    private String token;
    private String type = "Bearer";
    private String refreshToken;
    private Long id;
    private String username;
    private List<String> roles;

    public JwtResponseDto(String accessToken, String refreshToken, Long id, String username, List<String> roles) {
        this.token = accessToken;
        this.refreshToken = refreshToken;
        this.id = id;
        this.username = username;
        this.roles = roles;
    }
}
