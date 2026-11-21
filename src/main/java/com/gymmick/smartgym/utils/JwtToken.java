package com.gymmick.smartgym.utils;

import java.time.*;
import java.util.UUID;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import com.gymmick.smartgym.exceptions.*;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtToken {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    public UUID getTokenId(String token) {
        return UUID.fromString(verifyToken(token).getId());
    }

    public String createToken(UUID id, Duration duration) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .id(id.toString())
                .expiresAt(Instant.now().plusSeconds(duration.toSeconds()))
                .build();

        JwtEncoderParameters params = JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(), claims);

        return jwtEncoder.encode(params).getTokenValue();
    }

    public String createCheckinToken(Duration duration) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .id(UUID.randomUUID().toString())
                .claim("purpose", "checkin")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(duration))
                .build();

        JwtEncoderParameters params = JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(), claims);

        return jwtEncoder.encode(params).getTokenValue();
    }

    public Jwt verifyToken(String token) {
        try {
            if (token.isBlank()) {
                throw new Exception();
            }

            return jwtDecoder.decode(token);
        } catch (Exception e) {
            throw new BadRequestException("Token JWT tidak valid atau telah kedaluwarsa!");
        }
    }

}
