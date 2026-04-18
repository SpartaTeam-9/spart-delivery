package com.sparta.spartadelivery.user.application.service;

import com.sparta.spartadelivery.auth.exception.AuthErrorCode;
import com.sparta.spartadelivery.global.exception.AppException;
import com.sparta.spartadelivery.global.infrastructure.config.security.UserPrincipal;
import com.sparta.spartadelivery.user.domain.entity.UserEntity;
import com.sparta.spartadelivery.user.domain.repository.UserRepository;
import com.sparta.spartadelivery.user.presentation.dto.request.ReqUpdateUserDto;
import com.sparta.spartadelivery.user.presentation.dto.response.ResUpdateUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 본인 정보 수정 API (CUSTOMER, OWNER, MANAGER, MASTER 모두 사용 가능)
    @Transactional
    public ResUpdateUserDto updateMe(ReqUpdateUserDto request, UserPrincipal requester) {
        // 대상 사용자가 없으면 USER_NOT_FOUND로 처리한다.
        UserEntity user = userRepository.findByIdAndDeletedAtIsNull(requester.getId())
                .orElseThrow(() -> new AppException(AuthErrorCode.USER_NOT_FOUND));

        validateDuplicateEmail(request, user);
        user.updateProfile(request.username(), request.nickname(), request.email(), request.isPublic());
        if (request.hasPasswordUpdate()) {
            user.updatePassword(passwordEncoder.encode(request.password()));
        }
        return ResUpdateUserDto.from(user);
    }

    // 이메일 변경 시 중복 이메일 검증을 수행한다.
    private void validateDuplicateEmail(ReqUpdateUserDto request, UserEntity targetUser) {
        // 회원 정보 수정 시 이메일을 변경하지 않거나, 이메일이 null인 경우에는 중복 체크를 하지 않는다.
        if (request.email() == null || request.email().equals(targetUser.getEmail())) {
            return;
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new AppException(AuthErrorCode.DUPLICATE_EMAIL);
        }
    }

}
