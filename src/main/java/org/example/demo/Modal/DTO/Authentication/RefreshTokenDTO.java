package org.example.demo.Modal.DTO.Authentication;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class RefreshTokenDTO {
    Long id;
    Long userId;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    String token;
    LocalDateTime expiryDate;
    Boolean revoked;
}
