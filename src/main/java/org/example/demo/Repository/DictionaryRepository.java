package org.example.demo.Repository;

import org.example.demo.Modal.Entity.Dictionaries.Dictionary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DictionaryRepository extends JpaRepository<Dictionary, Long> {
    Optional<Dictionary> findByCode(String code);
    Page<Dictionary> findAll(Pageable pageable);
}
