package com.example.UniLabPass.service;

import com.example.UniLabPass.dto.request.LogoutRequest;
import com.example.UniLabPass.dto.request.RefreshTokenRequest;
import com.example.UniLabPass.entity.InvalidatedToken;
import com.example.UniLabPass.entity.MyUser;
import com.example.UniLabPass.entity.Role;
import com.example.UniLabPass.repository.InvalidatedTokenRepository;
import com.example.UniLabPass.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import com.example.UniLabPass.dto.request.AuthenticationRequest;
import com.example.UniLabPass.dto.request.IntrospectRequest;
import com.example.UniLabPass.dto.response.AuthenticationResponse;
import com.example.UniLabPass.dto.response.IntrospectResponse;
import com.example.UniLabPass.exception.AppException;
import com.example.UniLabPass.exception.ErrorCode;
import com.example.UniLabPass.repository.MyUserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationService {
    RoleRepository roleRepository;
    MyUserRepository myUserRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long REFRESH_DURATION;
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var myUser = myUserRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        var roleAdmin = roleRepository.findById("ADMIN").orElse(new Role());
        var roleUser = roleRepository.findById("USER").orElse(new Role());
        boolean authenticated = passwordEncoder.matches(request.getPassword(), myUser.getPassword());
        if (!authenticated ||
                !(myUser.getRoles().contains(roleAdmin) || myUser.getRoles().contains(roleUser))
        ) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        if (!myUser.isVerified()) {
            throw new AppException(ErrorCode.UNVERIFIED_EMAIL);
        }
        var token = generateToken(myUser);

        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }

    public void logout(LogoutRequest request)
            throws ParseException, JOSEException {
        try {
            // Nếu check theo false thì sẽ ra exception
            // ra exception thì token cũ kh được lưu
            // và khi đó token cũ có thể sử dụng để refresh
            var signToken = verifyToken(request.getToken(), true);

            String jit = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .id(jit)
                    .expiryTime(expiryTime)
                    .build();
            invalidatedTokenRepository.save(invalidatedToken);
        } catch (AppException exception) {
            log.info("Token already expired");
        }

    }

    public AuthenticationResponse refreshToken(RefreshTokenRequest request)
            throws ParseException, JOSEException {
        // Kiểm tra hiệu lực token
        var signedJWT = verifyToken(request.getToken(), true);
        // Lấy id của token
        var jit = signedJWT.getJWTClaimsSet().getJWTID();
        var expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        // Lưu token cũ vào InvalidatedToken
        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jit)
                .expiryTime(expiryTime)
                .build();
        invalidatedTokenRepository.save(invalidatedToken);

        //Tạo token mới
        var email = signedJWT.getJWTClaimsSet().getSubject();
        var myUser = myUserRepository.findByEmail(email).orElseThrow(
                () -> new AppException(ErrorCode.UNAUTHENTICATED)
        );
        var token = generateToken(myUser);

        return AuthenticationResponse.builder()
                .authenticated(true)
                .token(token)
                .build();

    }

    String generateToken(MyUser myUser) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(myUser.getEmail())
                .issuer("TuanNguyen.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()
                ))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(myUser))

                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        }
        catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }
    public IntrospectResponse introspect(IntrospectRequest request)
            throws JOSEException, ParseException {
        var token = request.getToken();
        boolean isValid = true;
        try {
            verifyToken(token, false);
        }
        catch (AppException e) {
            isValid = false;
        }

        return IntrospectResponse.builder()
                .valid(isValid)
                .build();
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = isRefresh
                ? new Date(signedJWT.getJWTClaimsSet().getIssueTime().toInstant()
                            .plus(REFRESH_DURATION, ChronoUnit.SECONDS).toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified =  signedJWT.verify(verifier);
        if (!(verified && expiryTime.after(new Date())))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        if (invalidatedTokenRepository
                .existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        return signedJWT;

    }



    // Hàm xây dựng thuộc tính scope và claim permisstion trong jwt
    private String buildScope(MyUser myUser) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(myUser.getRoles())) {
            myUser.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
//                if (!CollectionUtils.isEmpty(role.getPermissions()))
//                    role.getPermissions().forEach(permission -> stringJoiner.add(permission.getName()));
            }
                    );
        }
        return stringJoiner.toString();
    }
}
