package com.sparta.spartadelivery.order.domain.repository.filter;

import com.sparta.spartadelivery.user.domain.entity.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Configuration
public class SearchOrderConfig {

    @Bean
    public Map<Role, SearchOrderByRole> searchOrderStrategy(
            List<SearchOrderByRole> strategies
    ) {
        Map<Role, SearchOrderByRole> map = new EnumMap<>(Role.class);

        for (SearchOrderByRole strategy : strategies) {
            map.put(strategy.getAvaliableRole(), strategy);
        }

        return map;
    }
}
