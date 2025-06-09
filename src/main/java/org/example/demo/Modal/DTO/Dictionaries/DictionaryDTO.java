package org.example.demo.Modal.DTO.Dictionaries;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DictionaryDTO {
    private String code;
    private String name;
    private List<DictionaryItemDTO> dictionaryItems;
}
