package com.sparta.spartadelivery.ai.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Table(name = "p_ai_request_log")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class AiRequestLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ai_log_id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "user_id", length = 10, nullable = false)
    private String userId;

    @Column(name = "request_text", length = 100, nullable = false)
    private String requestText;

    @Column(name = "response_text", columnDefinition = "TEXT", nullable = false)
    private String responseText;

    @Column(name = "request_type", length = 30, nullable = false)
    private String requestType;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @CreatedBy
    @Column(name = "created_by", nullable = false, length = 100, updatable = false)
    private String createdBy;

    @Builder
    private AiRequestLog(
            String userId,
            String requestText,
            String responseText,
            String requestType
    ) {
        this.userId = userId;
        this.requestText = requestText;
        this.responseText = responseText;
        this.requestType = requestType;
    }
}
