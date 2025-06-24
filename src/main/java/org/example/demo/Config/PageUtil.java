package org.example.demo.Config;

import org.springframework.data.domain.Page;

public class PageUtil {
    public <T> PageResponseDTO<T> toPageResponse(Page<T> page){
        return new PageResponseDTO<>(
                page.getNumber() + 1,
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getContent()
        );

                }
}
