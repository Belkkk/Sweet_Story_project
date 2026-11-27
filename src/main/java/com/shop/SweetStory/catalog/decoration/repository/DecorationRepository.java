package com.shop.SweetStory.catalog.decoration.repository;

import com.shop.SweetStory.catalog.decoration.entity.Decoration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DecorationRepository extends JpaRepository<Decoration, Long> {
    public List<Decoration> findAllByIdIn();

    public Optional<Decoration> existsByName(String name);

}
