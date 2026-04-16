package com.sparta.spartadelivery.global.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.sparta.spartadelivery.global.infrastructure.config.JpaAuditingConfig;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.AuditorAware;
import org.springframework.test.context.TestPropertySource;

// JPA auditing 설정을 함께 로드해서 BaseEntity 필드가 영속화 과정에서 채워지는지 확인한다.
@DataJpaTest
@Import({JpaAuditingConfig.class, BaseEntityTest.AuditingTestConfig.class})
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class BaseEntityTest {

    private static final String TEST_AUDITOR = "base-entity-test-user";

    @Autowired
    private BaseEntityTestRepository repository;

    @Test
    @DisplayName("엔티티 저장 시 생성/수정 감사 정보가 자동으로 기록된다")
    void save() {
        BaseEntityTestEntity entity = new BaseEntityTestEntity("first-name");

        BaseEntityTestEntity savedEntity = repository.saveAndFlush(entity);

        assertThat(savedEntity.getCreatedAt()).isNotNull();
        assertThat(savedEntity.getUpdatedAt()).isNotNull();
        assertThat(savedEntity.getCreatedBy()).isEqualTo(TEST_AUDITOR);
        assertThat(savedEntity.getUpdatedBy()).isEqualTo(TEST_AUDITOR);
        assertThat(savedEntity.isDeleted()).isFalse();
    }

    @Test
    @DisplayName("엔티티 수정 시 수정 시간이 갱신된다")
    void update() {
        BaseEntityTestEntity savedEntity = repository.saveAndFlush(new BaseEntityTestEntity("before-name"));
        var createdAt = savedEntity.getCreatedAt();
        var beforeUpdatedAt = savedEntity.getUpdatedAt();

        savedEntity.updateName("after-name");
        BaseEntityTestEntity updatedEntity = repository.saveAndFlush(savedEntity);

        assertThat(updatedEntity.getCreatedAt()).isEqualTo(createdAt);
        assertThat(updatedEntity.getUpdatedAt()).isAfterOrEqualTo(beforeUpdatedAt);
        assertThat(updatedEntity.getUpdatedBy()).isEqualTo(TEST_AUDITOR);
    }

    @Test
    @DisplayName("markDeleted 호출 시 삭제 상태와 삭제자가 기록된다")
    void markDeleted() {
        BaseEntityTestEntity savedEntity = repository.saveAndFlush(new BaseEntityTestEntity("delete-name"));

        savedEntity.markDeleted("delete-user");

        assertThat(savedEntity.isDeleted()).isTrue();
        assertThat(savedEntity.getDeletedAt()).isNotNull();
        assertThat(savedEntity.getDeletedBy()).isEqualTo("delete-user");
    }

    @TestConfiguration
    static class AuditingTestConfig {

        @Bean
        AuditorAware<String> auditorAware() {
            return () -> Optional.of(TEST_AUDITOR);
        }
    }
}
