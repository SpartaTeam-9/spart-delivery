package com.sparta.spartadelivery.address.domain.repository;

import com.sparta.spartadelivery.address.domain.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {
}
