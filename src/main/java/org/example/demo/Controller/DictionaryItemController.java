package org.example.demo.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.demo.Config.ApiResponse;
import org.example.demo.Config.PageResponseDTO;
import org.example.demo.Config.PageUtil;
import org.example.demo.Modal.DTO.Dictionaries.DictionaryItemDTO;
import org.example.demo.Service.Interface.IDictionaryItemService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dictionary-items")
@RequiredArgsConstructor
public class DictionaryItemController {

    private final IDictionaryItemService dictionaryItemService;

    @PostMapping
    public ResponseEntity<ApiResponse<DictionaryItemDTO>> createDictionaryItem(@Valid @RequestBody DictionaryItemDTO itemDTO) {
        DictionaryItemDTO created = dictionaryItemService.createDictionaryItem(itemDTO);
        return ResponseEntity.ok(ApiResponse.success("Tạo dictionary item thành công!", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DictionaryItemDTO>> updateDictionaryItem(@PathVariable Long id, @Valid @RequestBody DictionaryItemDTO itemDTO) {
        DictionaryItemDTO updated = dictionaryItemService.updateDictionaryItem(id, itemDTO);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật dictionary item thành công!", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDictionaryItem(@PathVariable Long id) {
        dictionaryItemService.deleteDictionaryItem(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa dictionary item thành công!", null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DictionaryItemDTO>> getDictionaryItem(@PathVariable Long id) {
        DictionaryItemDTO item = dictionaryItemService.getDictionaryItem(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy ra dictionary item!", item));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponseDTO<DictionaryItemDTO>>> getDictionaryItems(
            @RequestParam(name = "dictionaryId", required = false) Long dictionaryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<DictionaryItemDTO> itemsPage = dictionaryItemService.getDictionaryItems(dictionaryId, page, size);
        PageResponseDTO<DictionaryItemDTO> response = new PageUtil().toPageResponse(itemsPage);
        return ResponseEntity.ok(ApiResponse.success("Lấy ra danh sách dictionary item!", response));
    }
}
