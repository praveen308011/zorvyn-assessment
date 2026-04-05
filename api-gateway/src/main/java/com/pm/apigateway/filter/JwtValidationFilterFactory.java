package com.pm.apigateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.constraints.Max;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import javax.crypto.SecretKey;
import java.util.List;

@Component

public class JwtValidationFilterFactory extends AbstractGatewayFilterFactory<Object> {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Override
    public GatewayFilter apply(Object config) {
        return ((exchange, chain) -> {
            String token = exchange.getRequest().getHeaders()
                    .getFirst(HttpHeaders.AUTHORIZATION);

            if(token==null || !token.startsWith("Bearer ")){
                exchange.getResponse().setRawStatusCode(HttpStatus.UNAUTHORIZED.value());
                return exchange.getResponse().setComplete();
            }

            String authToken = token.substring(7);

            Claims claims = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(SECRET_KEY)))
                    .build()
                    .parseSignedClaims(authToken)
                    .getPayload();

            String email = claims.getSubject();
            String userId = claims.get("userId", String.class);
            List<String> roles = claims.get("authorities", List.class);

            ServerHttpRequest modifiedRequest = exchange
                    .getRequest()
                    .mutate()
                    .header("X-User-Email", email)
                    .header("X-User-Roles", String.join(",", roles))
                    .header("X-User-Id", userId)
                    .build();

            return chain.filter(
                    exchange.mutate()
                            .request(modifiedRequest)
                            .build()
            );

        });

    }

}
