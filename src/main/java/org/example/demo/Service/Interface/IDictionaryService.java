package org.example.demo.Service.Interface;

import org.example.demo.Modal.DTO.Dictionaries.DictionaryDTO;

import java.util.List;

public interface IDictionaryService {
    DictionaryDTO createDictionary(DictionaryDTO dictionaryDTO);
    DictionaryDTO updateDictionary(Long id, DictionaryDTO dictionaryDTO);
    void deleteDictionary(Long id);
    DictionaryDTO getDictionary(Long id);
    List<DictionaryDTO> getAllDictionaries();
}
