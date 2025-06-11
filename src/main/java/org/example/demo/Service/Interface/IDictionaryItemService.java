package org.example.demo.Service.Interface;

import org.example.demo.Modal.DTO.Dictionaries.DictionaryItemDTO;

import java.util.List;

public interface IDictionaryItemService {
    DictionaryItemDTO createDictionaryItem(DictionaryItemDTO dictionaryItemDTO);

    DictionaryItemDTO updateDictionaryItem(Long id, DictionaryItemDTO dictionaryItemDTO);

    void deleteDictionaryItem(Long id);

    DictionaryItemDTO getDictionaryItem(Long id);

    List<DictionaryItemDTO> getDictionaryItems(Long dictionaryId);
}
