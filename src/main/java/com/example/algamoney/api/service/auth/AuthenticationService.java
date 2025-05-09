package com.example.algamoney.api.service.auth;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.algamoney.api.resource.AuthResource.TokenResponseDto;
import com.example.algamoney.api.security.AppUserDetailsService;
import com.example.algamoney.api.security.UsuarioSistema;

@Service
public class AuthenticationService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private Long expirationTime;

    @Value("${jwt.refresh.expiration}")
    private Long refreshExpirationTime;
    
    @Autowired
    private AppUserDetailsService appUserDetailsService;
    

    public TokenResponseDto gerarToken(String login) {
        String token = obterAccessToken(login);

        String refreshToken = obterRefreshToken(login);

        return new TokenResponseDto(token, refreshToken);
    }

    public TokenResponseDto gerarRefreshToken(String refreshToken) {
    	String login = JWT.require(Algorithm.HMAC256(secretKey))
                .build()
                .verify(refreshToken)
                .getSubject();
        return new TokenResponseDto(obterAccessToken(login), obterRefreshToken(login));
    }

    public String obterRefreshToken(String login) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        String refreshToken = JWT.create()
        		.withIssuer("algamoney-api")
                .withSubject(login)
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshExpirationTime))
                .sign(algorithm);
        return refreshToken;
    }
    
    public String obterAccessToken(String login) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        
        UsuarioSistema usuarioSistema = (UsuarioSistema) appUserDetailsService.loadUserByUsername(login);

        List<String> permissoes = appUserDetailsService.loadUserByUsername(login)
                .getAuthorities()
                .stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .toList();
        
        String nome = usuarioSistema.getUsuario().getNome();
        
        
        

        String accessToken = JWT.create()
        		.withIssuer("algamoney-api")
                .withSubject(login)
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
                .withClaim("authorities", permissoes)
                .withClaim("nome", nome)
                .sign(algorithm);
        return accessToken;
    }
}
