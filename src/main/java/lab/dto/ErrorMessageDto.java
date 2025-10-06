package lab.dto;

import java.util.Date;

import lombok.Data;

@Data
public class ErrorMessageDto {
    private Date timestamp;
    private String message;

    public ErrorMessageDto(Date timestamp, String message) {
        this.timestamp = timestamp;
        this.message = message;
    }
}
