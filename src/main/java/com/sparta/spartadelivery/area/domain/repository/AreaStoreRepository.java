package com.sparta.spartadelivery.area.domain.repository;

import com.sparta.spartadelivery.area.domain.entity.AreaStore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AreaStoreRepository extends JpaRepository<AreaStore, UUID> {
}
