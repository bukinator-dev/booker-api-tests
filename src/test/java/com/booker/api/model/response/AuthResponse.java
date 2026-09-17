package com.booker.api.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response payload for {@code POST /auth}: a token on success, or a reason on failure
 * (e.g. {"reason": "Bad credentials"}).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String reason;
}
