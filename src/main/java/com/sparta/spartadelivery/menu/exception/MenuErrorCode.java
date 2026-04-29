package com.sparta.spartadelivery.menu.exception;

import com.sparta.spartadelivery.global.exception.BaseErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum MenuErrorCode implements BaseErrorCode {
    // 조회
    MENU_NOT_FOUND(HttpStatus.NOT_FOUND, "메뉴를 찾을 수 없습니다."),
    // 그치만 order에서 삭제된 메뉴 스냅샷은 어케 처리?
    // customer이자, owner인 경우는?
    MENU_HIDDEN(HttpStatus.NOT_FOUND, "숨겨진 메뉴입니다."),
    MENU_DELETED(HttpStatus.NOT_FOUND, "삭제된 메뉴입니다."),

    // 메뉴 생성 관련 에러 코드
    //1. Customer 이하인 경우
    //2. Owner 지만 자기 가게가 아닌 경우
    //3. Owner고 자기 가게지만 데이터가 이상한 경우
    //4. MANAGER 이상인 경우
    // 권한
    MENU_ACCESS_DENIED(HttpStatus.FORBIDDEN, "해당 메뉴에 접근할 권한이 없습니다."),
    MENU_CREATE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "메뉴 생성 권한이 없습니다."),
    MENU_NOT_STORE_OWNER(HttpStatus.FORBIDDEN, "본인 가게 메뉴만 생성 가능합니다."),

    // 요청 데이터
    MENU_INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 메뉴 데이터입니다.");

    private final HttpStatus status;
    private final String message;

    MenuErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
