package org.example.demo.Service.Implement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.demo.Exception.UserFriendlyException;
import org.example.demo.Mapper.DictionaryItemMapper;
import org.example.demo.Mapper.DictionaryMapper;
import org.example.demo.Modal.DTO.Dictionaries.DictionaryDTO;
import org.example.demo.Modal.Entity.Dictionaries.Dictionary;
import org.example.demo.Modal.Entity.Dictionaries.DictionaryItem;
import org.example.demo.Repository.DictionaryRepository;
import org.example.demo.Service.Interface.IDictionaryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DictionaryServiceImplement implements IDictionaryService {

    private final DictionaryRepository dictionaryRepository;
    private final DictionaryMapper dictionaryMapper;
    private final DictionaryItemMapper dictionaryItemMapper;

    @Override
    public DictionaryDTO createDictionary(DictionaryDTO dictionaryDTO) {
        log.info("Creating dictionary: {}", dictionaryDTO.getCode());

        if (dictionaryRepository.findByCode(dictionaryDTO.getCode()).isPresent()) {
            log.error("Dictionary code already exists: {}", dictionaryDTO.getCode());
            throw new UserFriendlyException("Dictionary code already exists");
        }

        // Tạo entity từ DTO
        Dictionary tempDictionary = dictionaryMapper.toEntity(dictionaryDTO);

        // Gán quan hệ ngược cho từng item (tránh lỗi effectively final)
        if (tempDictionary.getDictionaryItems() != null) {
            tempDictionary.getDictionaryItems().forEach(item -> item.setDictionary(tempDictionary));
        }

        Dictionary savedDictionary = dictionaryRepository.save(tempDictionary);
        log.info("Dictionary created with ID: {}", savedDictionary.getId());

        return dictionaryMapper.toDTO(savedDictionary);
    }


    @Override
    public DictionaryDTO updateDictionary(Long id, DictionaryDTO dictionaryDTO) {
        log.info("Updating dictionary ID: {}", id);
        Dictionary dictionary = getDictionaryById(id);

        // Check trùng code nếu code được thay đổi
        if (!dictionary.getCode().equals(dictionaryDTO.getCode())) {
            if (dictionaryRepository.findByCode(dictionaryDTO.getCode()).isPresent()) {
                log.error("Dictionary code already exists: {}", dictionaryDTO.getCode());
                throw new UserFriendlyException("Dictionary code already exists");
            }
        }

        dictionary.setCode(dictionaryDTO.getCode());
        dictionary.setName(dictionaryDTO.getName());

        // Cập nhật danh sách item một cách an toàn
        dictionary.getDictionaryItems().clear();
        if (dictionaryDTO.getDictionaryItems() != null) {
            for (var itemDTO : dictionaryDTO.getDictionaryItems()) {
                DictionaryItem dictionaryItem = dictionaryItemMapper.toEntity(itemDTO);
                dictionaryItem.setDictionary(dictionary); // giữ liên kết ngược
                dictionary.getDictionaryItems().add(dictionaryItem);
            }
        }

        dictionary = dictionaryRepository.save(dictionary);
        log.info("Dictionary updated: ID {}", id);
        return dictionaryMapper.toDTO(dictionary);
    }

    @Override
    public void deleteDictionary(Long id) {
        log.info("Deleting dictionary ID: {}", id);
        Dictionary dictionary = getDictionaryById(id);
        dictionaryRepository.delete(dictionary);
        log.info("Dictionary deleted: ID {}", id);
    }

    @Override
    public DictionaryDTO getDictionary(Long id) {
        log.info("Retrieving dictionary ID: {}", id);
        Dictionary dictionary = getDictionaryById(id);
        return dictionaryMapper.toDTO(dictionary);
    }

    @Override
    public List<DictionaryDTO> getAllDictionaries() {
        log.info("Retrieving all dictionaries");
        return dictionaryRepository.findAll().stream()
                .map(dictionaryMapper::toDTO)
                .collect(Collectors.toList());
    }

    // DRY: dùng chung method để get dictionary với logging + exception
    private Dictionary getDictionaryById(Long id) {
        return dictionaryRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Dictionary not found: ID {}", id);
                    return new UserFriendlyException("Dictionary not found");
                });
    }
}