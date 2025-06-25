package org.example.demo.Mapper;

import org.example.demo.Modal.DTO.Dictionaries.DictionaryItemDTO;
import org.example.demo.Modal.Entity.Dictionaries.DictionaryItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {DictionaryItemMapper.class})
public interface DictionaryItemMapper {
    DictionaryItemDTO toDTO(DictionaryItem dictionaryItem);

    DictionaryItem toEntity(DictionaryItemDTO dictionaryItemDTO);
}
