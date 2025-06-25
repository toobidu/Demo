package org.example.demo.Mapper;

import org.example.demo.Modal.DTO.Dictionaries.DictionaryDTO;
import org.example.demo.Modal.Entity.Dictionaries.Dictionary;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DictionaryMapper {
    DictionaryDTO toDTO(Dictionary dictionary);

    Dictionary toEntity(DictionaryDTO dictionaryDTO);
}
