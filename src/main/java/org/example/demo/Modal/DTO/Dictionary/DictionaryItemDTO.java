package org.example.demo.Modal.DTO.Dictionary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class DictionaryItemDTO {
    Long id;
    Long dictionaryId;
    String code;
    String value;
    String description;
    LocalDateTime createdDate;
    LocalDateTime updatedDate;
}
