package com.hardy.fawatir.provider;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.hardy.fawatir.model.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

@Component
public class TokenProvider {
    private static final String GET_HARDY = "";
    private static final String CUSTOMER_MANAGEMENT_SERVICE = "";
    public static final String AUTHORITIES = "Authorities";
    public static final long ACCESS_TOKEN_EXPIRATION_TIME = 1_800_000;
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 432_000_000;


    @Value("${jwt.secret}")
    private String secret;

    public String createAccessToken(UserPrincipal userPrincipal){
        String[] claims = getClaimsFromUser(userPrincipal);

        return JWT.create()
                .withIssuer(GET_HARDY)
                .withAudience(CUSTOMER_MANAGEMENT_SERVICE)
                .withIssuedAt(new Date())
                .withSubject(userPrincipal.getUsername())
                .withArrayClaim(AUTHORITIES, claims)
                .withExpiresAt(new Date(currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
                .sign(HMAC512(secret.getBytes()));

    }
    public String createRefreshToken(UserPrincipal userPrincipal){
        return JWT.create()
                .withIssuer(GET_HARDY)
                .withAudience(CUSTOMER_MANAGEMENT_SERVICE)
                .withIssuedAt(new Date())
                .withSubject(userPrincipal.getUsername())
                .withExpiresAt(new Date(currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
                .sign(HMAC512(secret.getBytes()));

    }

    public String getSubject(String token, HttpServletRequest request) {
        Optional<String> subject = Optional.empty();
        try {
            subject = Optional.ofNullable( getJWTVerifier().verify(token).getSubject());
        } catch (TokenExpiredException e) {
            request.setAttribute("expiredMessage", e.getMessage());
        } catch (InvalidClaimException e) {
            request.setAttribute("invalidClaim", e.getMessage());
        } catch (Exception e) {
            throw e;
        }
        return subject.orElse("");
    }

    private String[] getClaimsFromUser(UserPrincipal userPrincipal) {
        return userPrincipal
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toArray(String[]::new);
    }

    public List<GrantedAuthority> getAuthorities(String token) {
        String[] claims = getClaimsFromToken(token);
        return stream(claims).map(SimpleGrantedAuthority::new).collect(toList());
    }

    public Authentication getAuthentication(String email, List<GrantedAuthority> authorities, HttpServletRequest request){
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email,null ,authorities);
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        return authenticationToken;
    }

    public boolean isTokenValid(String email, String token){
        JWTVerifier verifier = getJWTVerifier();
        return StringUtils.isNoneEmpty(email) && !isTokenExpired(verifier, token);
    }

    private boolean isTokenExpired(JWTVerifier verifier, String token) {
        Date expiration = verifier.verify(token).getExpiresAt();
        return expiration.before(new Date());
    }

    private String[] getClaimsFromToken(String token) {
        JWTVerifier verifier = getJWTVerifier();
        return verifier.verify(token).getClaim(AUTHORITIES).asArray(String.class);
    }

    private JWTVerifier getJWTVerifier() {
        JWTVerifier verifier;
        try{
            Algorithm algorithm = HMAC512(secret.getBytes());
            verifier = JWT.require(algorithm).withIssuer(GET_HARDY).build();
        }catch (JWTVerificationException e){
            throw new JWTVerificationException("Token cannot be verified");
        }
        return verifier;
    }

}
