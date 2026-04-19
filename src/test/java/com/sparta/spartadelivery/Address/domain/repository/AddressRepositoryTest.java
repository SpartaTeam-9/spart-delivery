package com.sparta.spartadelivery.Address.domain.repository;

import com.sparta.spartadelivery.Address.config.TestJpaConfig;
import com.sparta.spartadelivery.address.domain.entity.Address;
import com.sparta.spartadelivery.address.domain.repository.AddressRepository;
import com.sparta.spartadelivery.user.domain.entity.Role;
import com.sparta.spartadelivery.user.domain.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("AddressRepository 테스트")
@Import(TestJpaConfig.class)
public class AddressRepositoryTest {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private TestEntityManager entityManager;

    private UserEntity user;

    @BeforeEach
    void setUp() {
        // 테스트용 사용자 생성 및 저장
        user = UserEntity.builder()
                .username("sparta_user")
                .nickname("스파르타")
                .email("test@sparta.com")
                .password("password")
                .role(Role.CUSTOMER)
                .isPublic(true)
                .build();
        entityManager.persist(user);
    }

    @Test
    @DisplayName("사용자의 삭제되지 않은 배송지 목록 조회 테스트")
    void findAllByUserDeletedAtIsNull_Success() {
        // given
        Address address1 = Address.builder().user(user).address("서울").isDefault(true).build();
        Address address2 = Address.builder().user(user).address("경기").isDefault(false).build();
        entityManager.persist(address1);
        entityManager.persist(address2);
        entityManager.flush();

        // when
        List<Address> results = addressRepository.findAllByUserAndDeletedAtIsNull(user);

        // then
        assertThat(results).hasSize(2);
        assertThat(results).extracting("address").containsExactlyInAnyOrder("서울", "경기");
    }

    @Test
    @DisplayName("기존 기본 배송지를 모두 해제(false) 처리하는 벌크 쿼리 테스트")
    void updateAllDefaultToFalse_Success() {
        // given
        Address address1 = Address.builder().user(user).address("기존 기본").isDefault(true).build();
        Address address2 = Address.builder().user(user).address("일반 주소").isDefault(false).build();
        entityManager.persist(address1);
        entityManager.persist(address2);
        entityManager.flush();

        // when
        addressRepository.updateAllDefaultToFalse(user.getUsername());

        // then
        // @Modifying(clearAutomatically = true) 덕분에 영속성 컨텍스트가 비워져 DB에서 새로 조회함
        Address updatedAddress1 = addressRepository.findById(address1.getId()).orElseThrow();
        Address updatedAddress2 = addressRepository.findById(address2.getId()).orElseThrow();

        assertThat(updatedAddress1.isDefault()).isFalse();
        assertThat(updatedAddress2.isDefault()).isFalse();
    }
}
