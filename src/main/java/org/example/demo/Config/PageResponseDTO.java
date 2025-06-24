package org.example.demo.Config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponseDTO<T> {
    private int page;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private List<T> content;
}
