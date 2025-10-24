package it.auth.travelauth.domain.dto.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class UserResponseDto {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private UUID uuidUser;
}