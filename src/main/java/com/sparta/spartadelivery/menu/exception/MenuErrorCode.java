package com.sparta.spartadelivery.menu.exception;

import com.sparta.spartadelivery.global.exception.BaseErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
//@RequiredArgsConstructor
public enum MenuErrorCode implements BaseErrorCode {
    // 조회
    MENU_NOT_FOUND(HttpStatus.NOT_FOUND, "메뉴를 찾을 수 없습니다."),
    // 그치만 order에서 삭제된 메뉴 스냅샷은 어케 처리?
    // customer이자, owner인 경우는?
    MENU_HIDDEN(HttpStatus.NOT_FOUND, "숨겨진 메뉴입니다."),
    MENU_DELETED(HttpStatus.NOT_FOUND, "삭제된 메뉴입니다."),


    // 권한
    MENU_ACCESS_DENIED(HttpStatus.FORBIDDEN, "해당 메뉴에 접근할 권한이 없습니다."),
    MENU_CREATE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "메뉴 생성 권한이 없습니다."),
    MENU_UPDATE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "메뉴 수정 권한이 없습니다."),
    // 추후 삭제
    MENU_NOT_STORE_OWNER(HttpStatus.FORBIDDEN, "본인 가게 메뉴만 생성 가능합니다."),

    // 삭제
    MENU_DELETE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "메뉴 삭제 권한이 없습니다."),
    MENU_DELETE_STORE_DELETED(HttpStatus.GONE, "삭제된 가게의 메뉴는 삭제할 수 없습니다."),
    MENU_ALREADY_DELETED(HttpStatus.CONFLICT, "이미 삭제된 메뉴입니다."),

    MENU_ALREADY_HIDDEN(HttpStatus.CONFLICT, "이미 숨겨진 메뉴입니다."),
    MENU_ALREADY_SHOW(HttpStatus.CONFLICT, "이미 판매중인 메뉴입니다."),

    // 요청 데이터
    MENU_INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 메뉴 데이터입니다.");

    private final HttpStatus status;
    private final String message;

    MenuErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
