package com.sparta.spartadelivery.menu.domain;

import com.sparta.spartadelivery.menu.domain.entity.*;
import com.sparta.spartadelivery.menu.domain.repository.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@SpringBootTest
// @Transactional // h2 data rollback 되지 않음, 로컬에 저장, inserted data 확인용
@ActiveProfiles("test-h2") // test-h2 yaml 파일 적용
class MenuEntityIntegrationTest {
    @Autowired
    private MenuRepository menuRepo;

    @Autowired
    private MenuCategoryRepository menuCategoryRepo;

    @Autowired
    private TagRepository tagRepo;

    @Autowired
    private MenuTagRepository menuTagRepo;

    @Autowired
    private OptionGroupSpecRepository optionGroupSpecRepo;

    @Autowired
    private OptionSpecRepository optionSpecRepo;

    @Test
    void 전체_엔티티_저장_조회_통합테스트() {

        // 1) MenuCategory 저장
        MenuCategory category = new MenuCategory(
                "카테고리",
                UUID.randomUUID()
        );
        MenuCategory savedCategory = menuCategoryRepo.save(category);
        assertNotNull(savedCategory.getId());

        // 2) Menu 저장
        Menu menu = new Menu(
                UUID.randomUUID(),
                savedCategory.getId(),
                "테스트메뉴",
                10000,
                "설명",
                "http://image.com",
                false,
                "ai desc",
                "ai prompt"
        );
        Menu savedMenu = menuRepo.save(menu);
        assertNotNull(savedMenu.getId());

        // 3) Tag 저장
        Tag tag = new Tag("매운맛");
        Tag savedTag = tagRepo.save(tag);
        assertNotNull(savedTag.getId());

        // 4) MenuTag 저장
        MenuTag menuTag = new MenuTag(savedMenu.getId(), savedTag.getId());
        MenuTag savedMenuTag = menuTagRepo.save(menuTag);
        assertNotNull(savedMenuTag.getId());

        // 5) OptionGroupSpec 저장
        OptionGroupSpec group = new OptionGroupSpec(
                savedMenu,
                "옵션그룹",
                null,
                null
        );
        OptionGroupSpec savedGroup = optionGroupSpecRepo.save(group);
        OptionGroupSpec foundGroup = optionGroupSpecRepo.findById(savedGroup.getId()).get();

        assertNotNull(savedGroup.getId());
        assertNotNull(foundGroup.getMinSelect());
        assertNotNull(foundGroup.getMaxSelect());

        System.out.println("min값 " + foundGroup.getMinSelect());
        System.out.println("max값 " + foundGroup.getMaxSelect());

        // 6) OptionSpec 저장
        OptionSpec option = new OptionSpec(
                savedGroup,
                "콜라",
                2000,
                null
        );
        OptionSpec savedOption = optionSpecRepo.save(option);
        assertNotNull(savedOption.getId());
    }
}
