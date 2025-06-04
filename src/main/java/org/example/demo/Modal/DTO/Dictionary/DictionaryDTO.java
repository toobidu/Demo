package org.example.demo.Modal.DTO.Dictionary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class DictionaryDTO {
    Long id;
    String code;
    String name;
    String description;
    LocalDateTime createdDate;
    LocalDateTime updatedDate;
    List<DictionaryItemDTO> dictionaryItems;
}
