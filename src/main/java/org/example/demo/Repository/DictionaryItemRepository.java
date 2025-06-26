package org.example.demo.Repository;

import org.example.demo.Modal.Entity.Dictionaries.DictionaryItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DictionaryItemRepository extends JpaRepository<DictionaryItem, Long> {
    Page<DictionaryItem> findByDictionaryId(Long dictionaryId, Pageable pageable);
}
