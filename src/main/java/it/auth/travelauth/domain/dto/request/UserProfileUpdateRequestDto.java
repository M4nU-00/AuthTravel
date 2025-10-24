package it.auth.travelauth.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@EqualsAndHashCode
public class UserProfileUpdateRequestDto {

    private String firstName;
    private String lastName;
    private String email;
    private String username;

}
