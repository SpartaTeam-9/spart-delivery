package com.sparta.spartadelivery.auth.presentation.controller;

import com.sparta.spartadelivery.auth.application.service.AuthService;
import com.sparta.spartadelivery.auth.presentation.dto.request.ReqLoginDto;
import com.sparta.spartadelivery.auth.presentation.dto.request.ReqSignupDto;
import com.sparta.spartadelivery.auth.presentation.dto.response.ResLoginDto;
import com.sparta.spartadelivery.auth.presentation.dto.response.ResSignupDto;
import com.sparta.spartadelivery.global.infrastructure.config.security.UserPrincipal;
import com.sparta.spartadelivery.global.presentation.dto.ApiResponse;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<ResSignupDto>> signup(@Valid @RequestBody ReqSignupDto request) {
        ResSignupDto response = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "CREATED", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<ResLoginDto>> login(@Valid @RequestBody ReqLoginDto request) {
        ResLoginDto response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "SUCCESS", response));
    }

    // TODO : 컨트롤러에서 현재 사용자 꺼내는 예시, 팀원들 확인이 끝나면 추후 삭제 예정
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> me(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Map<String, Object> response = Map.of(
                "id", userPrincipal.getId(),
                "username", userPrincipal.getAccountName(),
                "nickname", userPrincipal.getNickname(),
                "email", userPrincipal.getEmail(),
                "role", userPrincipal.getRole()
        );
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "SUCCESS", response));
    }
}
