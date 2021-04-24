package io.witcradg.shopifysquareapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.witcradg.shopifysquareapi.entity.RawJsonEntity;

@Repository
public interface IRawJsonRepository extends JpaRepository<RawJsonEntity, Long> {

}
