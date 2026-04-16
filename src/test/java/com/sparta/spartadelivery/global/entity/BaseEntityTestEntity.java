package com.sparta.spartadelivery.global.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
class BaseEntityTestEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    protected BaseEntityTestEntity() {
    }

    BaseEntityTestEntity(String name) {
        this.name = name;
    }

    void updateName(String name) {
        this.name = name;
    }
}
