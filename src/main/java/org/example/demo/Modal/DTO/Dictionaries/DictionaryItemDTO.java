package org.example.demo.Modal.DTO.Dictionaries;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DictionaryItemDTO {
    private Long id;
    private Long dictionaryId;
    private String code;
    private String name;
}
