package com.sparta.spartadelivery.area.domain.entity;

import com.sparta.spartadelivery.store.domain.entity.Store;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(
        name = "p_area_store",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"area_id", "store_id"})
        }
)
public class AreaStore {

    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id", nullable = false)
    private Area area;

    protected AreaStore() {
    }

    public AreaStore(Area area, Store store) {
        this.area = area;
        this.store = store;
    }
}
