package tab.fitness.gateway;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import tab.fitness.gateway.user.UserRequest;
import tab.fitness.gateway.user.UserService;

import java.text.ParseException;


@Component
@Slf4j
@RequiredArgsConstructor
public class KeycloakUserSyncFilter implements WebFilter {
    private final UserService userService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String userId = exchange.getRequest().getHeaders().getFirst("X-User-ID");
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        UserRequest registerRequest = getUserDetails(token);

        if(userId == null) {
            userId = registerRequest.getKeycloakId();

        }
        if(userId != null && token != null) {
            String finalUserId = userId;
            return userService.validateUser(userId).flatMap(exist->{
                if(!exist){
                   if(registerRequest != null) {
                       return userService.registerUser(registerRequest)
                               .then(Mono.empty());
                   }
                   else{
                       return Mono.empty();
                   }
                }
                else{
                    log.info("User already exists, skipping sync");
                    return Mono.empty();
                }
            })
                    .then(Mono.defer(()->{
                        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                                .header("X-User-ID", finalUserId)
                                .build();
                        return chain.filter(exchange.mutate().request(mutatedRequest).build());
                    }));

        }


        return null;
    }

    private UserRequest getUserDetails(String token) {
        try{
            String tokenWithoutBearer = token.substring(7);
            SignedJWT signedJWT = SignedJWT.parse(tokenWithoutBearer);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            UserRequest request = UserRequest.builder()
                    .email(claims.getStringClaim("email"))
                    .password("dummy@123")
                    .keycloakId(claims.getStringClaim("sub"))
                    .firstName(claims.getStringClaim("given_name"))
                    .lastName(claims.getStringClaim("family_name"))
                    .build();
            return request;

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
