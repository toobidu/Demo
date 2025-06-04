package org.example.demo.Modal.DTO.Products;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class AttributeDTO {
    Long id;
    String code;
    String name;
    String description;
    LocalDateTime createdDate;
    LocalDateTime updatedDate;
}
