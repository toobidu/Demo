package org.example.demo.Service.Interface;

import org.example.demo.Modal.DTO.Dictionaries.DictionaryItemDTO;
import org.springframework.data.domain.Page;

public interface IDictionaryItemService {
    DictionaryItemDTO createDictionaryItem(DictionaryItemDTO dictionaryItemDTO);

    DictionaryItemDTO updateDictionaryItem(Long id, DictionaryItemDTO dictionaryItemDTO);

    void deleteDictionaryItem(Long id);

    DictionaryItemDTO getDictionaryItem(Long id);

    Page<DictionaryItemDTO> getDictionaryItems(Long dictionaryId, int page, int size);
}
