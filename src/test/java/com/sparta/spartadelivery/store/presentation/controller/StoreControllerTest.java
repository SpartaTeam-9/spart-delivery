package com.sparta.spartadelivery.store.presentation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.spartadelivery.area.exception.AreaErrorCode;
import com.sparta.spartadelivery.global.exception.AppException;
import com.sparta.spartadelivery.global.infrastructure.config.security.JwtAuthenticationFilter;
import com.sparta.spartadelivery.global.infrastructure.config.security.JwtTokenProvider;
import com.sparta.spartadelivery.global.infrastructure.config.security.UserPrincipal;
import com.sparta.spartadelivery.store.application.service.StoreService;
import com.sparta.spartadelivery.store.exception.StoreErrorCode;
import com.sparta.spartadelivery.store.presentation.dto.request.StoreCreateRequest;
import com.sparta.spartadelivery.store.presentation.dto.response.StoreDetailResponse;
import com.sparta.spartadelivery.user.domain.entity.Role;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(StoreController.class)
@Import(StoreControllerTest.TestSecurityConfig.class)
class StoreControllerTest {

    @TestConfiguration
    @EnableWebSecurity
    static class TestSecurityConfig {
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(AbstractHttpConfigurer::disable)
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                    .securityContext(context -> context.requireExplicitSave(false));
            return http.build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StoreService storeService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private UsernamePasswordAuthenticationToken ownerToken;

    @BeforeEach
    void setUp() throws Exception {
        ownerToken = authenticationToken(Role.OWNER);

        doAnswer(invocation -> {
            jakarta.servlet.FilterChain chain = invocation.getArgument(2);
            chain.doFilter(invocation.getArgument(0), invocation.getArgument(1));
            return null;
        }).when(jwtAuthenticationFilter).doFilter(any(), any(), any());
    }

    @Test
    @DisplayName("가게 등록 성공 시 201 CREATED를 반환한다")
    void createStore() throws Exception {
        StoreCreateRequest request = new StoreCreateRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "스파르타 분식",
                "서울특별시 강남구 테헤란로 123",
                "02-1234-5678"
        );
        StoreDetailResponse response = storeResponse(request);

        given(storeService.createStore(any(StoreCreateRequest.class), any(UserPrincipal.class)))
                .willReturn(response);

        mockMvc.perform(post("/api/v1/stores")
                        .with(authentication(ownerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("CREATED"))
                .andExpect(jsonPath("$.data.name").value("스파르타 분식"));
    }

    @Test
    @DisplayName("가게 등록 요청값이 유효하지 않으면 400을 반환한다")
    void createStoreWithInvalidRequest() throws Exception {
        StoreCreateRequest request = new StoreCreateRequest(
                null,
                UUID.randomUUID(),
                "",
                "",
                "02-1234-5678"
        );

        mockMvc.perform(post("/api/v1/stores")
                        .with(authentication(ownerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("VALIDATION_ERROR"));
    }

    @Test
    @DisplayName("OWNER가 아니면 가게 등록 시 403을 반환한다")
    void createStoreByNonOwnerDenied() throws Exception {
        StoreCreateRequest request = new StoreCreateRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "스파르타 분식",
                "서울특별시 강남구 테헤란로 123",
                "02-1234-5678"
        );

        given(storeService.createStore(any(StoreCreateRequest.class), any(UserPrincipal.class)))
                .willThrow(new AppException(StoreErrorCode.STORE_CREATE_OWNER_ROLE_REQUIRED));

        mockMvc.perform(post("/api/v1/stores")
                        .with(authentication(ownerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("가게는 OWNER 권한 사용자만 등록할 수 있습니다."));
    }

    @Test
    @DisplayName("지역이 없으면 가게 등록 시 404를 반환한다")
    void createStoreWhenAreaNotFound() throws Exception {
        StoreCreateRequest request = new StoreCreateRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "스파르타 분식",
                "서울특별시 강남구 테헤란로 123",
                "02-1234-5678"
        );

        given(storeService.createStore(any(StoreCreateRequest.class), any(UserPrincipal.class)))
                .willThrow(new AppException(AreaErrorCode.AREA_NOT_FOUND));

        mockMvc.perform(post("/api/v1/stores")
                        .with(authentication(ownerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("운영 지역을 찾을 수 없습니다."));
    }

    private StoreDetailResponse storeResponse(StoreCreateRequest request) {
        return new StoreDetailResponse(
                UUID.randomUUID(),
                1L,
                request.storeCategoryId(),
                request.areaId(),
                request.name(),
                request.address(),
                request.phone(),
                BigDecimal.ZERO,
                false,
                LocalDateTime.now()
        );
    }

    private UsernamePasswordAuthenticationToken authenticationToken(Role role) {
        UserPrincipal userPrincipal = UserPrincipal.builder()
                .id(1L)
                .accountName("owner01")
                .email("owner01@example.com")
                .password("password")
                .nickname("점주01")
                .role(role)
                .build();
        return new UsernamePasswordAuthenticationToken(
                userPrincipal,
                null,
                userPrincipal.getAuthorities()
        );
    }
}
