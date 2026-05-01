package com.sparta.spartadelivery.global.aop;

import com.sparta.spartadelivery.global.annotation.CheckMenuPermission;
import com.sparta.spartadelivery.global.exception.AppException;
import com.sparta.spartadelivery.global.infrastructure.config.security.UserPrincipal;
import com.sparta.spartadelivery.menu.application.service.MenuPermissionPolicy;
import com.sparta.spartadelivery.menu.domain.entity.Menu;
import com.sparta.spartadelivery.menu.domain.repository.MenuRepository;
import com.sparta.spartadelivery.menu.exception.MenuErrorCode;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Aspect
@Component
@RequiredArgsConstructor
public class MenuPermissionAspect {

    private final MenuPermissionPolicy menuPermissionPolicy;
    private final MenuRepository menuRepository;

    @Around("@annotation(checkMenuPermission)")
    public Object checkMenuPermission(
            ProceedingJoinPoint joinPoint,
            CheckMenuPermission checkMenuPermission
    ) throws Throwable {
        UUID menuId = null;
        UserPrincipal user = null;

        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof UUID id) {
                menuId = id;
            }

            if(arg instanceof UserPrincipal principal) {
                user = principal;
            }
        }
        if (menuId == null || user == null) {
            throw new AppException(MenuErrorCode.INVALID_PERMISSION_REQUEST);
        }

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new AppException(MenuErrorCode.MENU_NOT_FOUND));

        switch (checkMenuPermission.action()) {
            case HIDE -> menuPermissionPolicy.validateHideCondition(user, menu);
            case DELETE -> menuPermissionPolicy.validateDeleteCondition(user, menu);
            case UPDATE -> menuPermissionPolicy.validateUpdateCondition(user, menu);
            default -> throw new AppException(MenuErrorCode.INVALID_PERMISSION_REQUEST);
        }

        urn joinPoint.proceed();
    }
}