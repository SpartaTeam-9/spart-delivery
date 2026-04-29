package com.sparta.spartadelivery.menu.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class MoneyVO {

    @Column(nullable = false)
    private Integer price;

    public MoneyVO(Integer price) {
        validate(price);
        this.price = price;
    }

    // 추후 공통 검증 코드로 변경
    private static void validate(Integer price) {
        if (price == null) {
            throw new IllegalArgumentException("가격은 null일 수 없습니다.");
        }
        if (price < 0) {
            throw new IllegalArgumentException("가격은 음수일 수 없습니다.");
        }
    }
}