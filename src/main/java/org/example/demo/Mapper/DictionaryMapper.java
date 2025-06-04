package org.example.demo.Mapper;

import org.example.demo.Modal.DTO.Dictionary.DictionaryDTO;
import org.example.demo.Modal.DTO.Dictionary.DictionaryItemDTO;
import org.example.demo.Modal.Entity.Dictionary.Dictionary;
import org.example.demo.Modal.Entity.Dictionary.DictionaryItem;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DictionaryMapper {
    DictionaryDTO toDTO(Dictionary dictionary);

    Dictionary toEntity(DictionaryDTO dictionaryDTO);

    List<DictionaryDTO> toDTOList(List<Dictionary> dictionaries);

    DictionaryItemDTO toDTO(DictionaryItem dictionaryItem);

    DictionaryItem toEntity(DictionaryItemDTO dictionaryItemDTO);

    List<DictionaryItemDTO> toItemDTOList(List<DictionaryItem> dictionaryItems);
}
