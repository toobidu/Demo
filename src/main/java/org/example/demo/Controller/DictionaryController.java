package org.example.demo.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.demo.Config.ApiResponse;
import org.example.demo.Config.PageResponseDTO;
import org.example.demo.Config.PageUtil;
import org.example.demo.Modal.DTO.Dictionaries.DictionaryDTO;
import org.example.demo.Service.Interface.IDictionaryService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dictionaries")
@RequiredArgsConstructor
public class DictionaryController {

    private final IDictionaryService dictionaryService;

    @PostMapping
    public ResponseEntity<ApiResponse<DictionaryDTO>> createDictionary(@Valid @RequestBody DictionaryDTO dictionaryDTO) {
        DictionaryDTO created = dictionaryService.createDictionary(dictionaryDTO);
        return ResponseEntity.ok(ApiResponse.success("Tạo dictionary thành công!", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DictionaryDTO>> updateDictionary(@PathVariable Long id, @Valid @RequestBody DictionaryDTO dictionaryDTO) {
        DictionaryDTO dict = dictionaryService.updateDictionary(id, dictionaryDTO);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật dictionary thành công!", dict));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDictionary(@PathVariable Long id) {
        dictionaryService.deleteDictionary(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa dictionary thành công!", null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DictionaryDTO>> getDictionary(@PathVariable Long id) {
        DictionaryDTO dictionary = dictionaryService.getDictionary(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy ra dictionary!", dictionary));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponseDTO<DictionaryDTO>>> getAllDictionaries(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<DictionaryDTO> dictionaryPage = dictionaryService.getAllDictionaries(page, size);
        PageResponseDTO<DictionaryDTO> response = new PageUtil().toPageResponse(dictionaryPage);
        return ResponseEntity.ok(ApiResponse.success("Lấy ra danh sách dictionary!", response));
    }
}
