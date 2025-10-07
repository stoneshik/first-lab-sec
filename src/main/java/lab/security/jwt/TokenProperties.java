package lab.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "token")
public class TokenProperties {
    private String secretKey;
    private long expireTime;
    private long refreshTime;
}
