package org.example.demo.Service.Implement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.demo.Exception.UserFriendlyException;
import org.example.demo.Mapper.DictionaryItemMapper;
import org.example.demo.Mapper.DictionaryMapper;
import org.example.demo.Modal.DTO.Dictionaries.DictionaryDTO;
import org.example.demo.Modal.DTO.Dictionaries.DictionaryItemDTO;
import org.example.demo.Modal.Entity.Dictionaries.Dictionary;
import org.example.demo.Modal.Entity.Dictionaries.DictionaryItem;
import org.example.demo.Repository.DictionaryItemRepository;
import org.example.demo.Repository.DictionaryRepository;
import org.example.demo.Service.Interface.IDictionaryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DictionaryServiceImplement implements IDictionaryService {

    private final DictionaryRepository dictionaryRepository;
    private final DictionaryMapper dictionaryMapper;
    private final DictionaryItemMapper dictionaryItemMapper;
    private final DictionaryItemRepository dictionaryItemRepository;

    @Override
    @Transactional
    public DictionaryDTO createDictionary(DictionaryDTO dictionaryDTO) {
        log.info("Creating dictionary: {}", dictionaryDTO.getCode());

        if (dictionaryRepository.findByCode(dictionaryDTO.getCode()).isPresent()) {
            log.error("Dictionary code already exists: {}", dictionaryDTO.getCode());
            throw new UserFriendlyException("Dictionary code already exists");
        }

        Dictionary dictionary = dictionaryMapper.toEntity(dictionaryDTO);
        dictionary = dictionaryRepository.save(dictionary);

        if (dictionaryDTO.getDictionaryItems() != null) {
            for (DictionaryItemDTO itemDTO : dictionaryDTO.getDictionaryItems()) {
                DictionaryItem item = dictionaryItemMapper.toEntity(itemDTO);
                item.setDictionaryId(dictionary.getId());
                dictionaryItemRepository.save(item);
            }
        }

        log.info("Dictionary created with ID: {}", dictionary.getId());
        return getDictionary(dictionary.getId());
    }

    @Override
    @Transactional
    public DictionaryDTO updateDictionary(Long id, DictionaryDTO dictionaryDTO) {
        log.info("Updating dictionary ID: {}", id);
        Dictionary dictionary = getDictionaryById(id);

        if (!dictionary.getCode().equals(dictionaryDTO.getCode()) &&
                dictionaryRepository.findByCode(dictionaryDTO.getCode()).isPresent()) {
            log.error("Dictionary code already exists: {}", dictionaryDTO.getCode());
            throw new UserFriendlyException("Dictionary code already exists");
        }

        dictionary.setCode(dictionaryDTO.getCode());
        dictionary.setName(dictionaryDTO.getName());
        dictionary = dictionaryRepository.save(dictionary);

        // Delete existing items
        Page<DictionaryItem> existingItems = dictionaryItemRepository.findByDictionaryId(id, Pageable.unpaged());
        existingItems.forEach(item -> dictionaryItemRepository.deleteById(item.getId()));

        // Create new items
        if (dictionaryDTO.getDictionaryItems() != null) {
            for (DictionaryItemDTO itemDTO : dictionaryDTO.getDictionaryItems()) {
                DictionaryItem item = dictionaryItemMapper.toEntity(itemDTO);
                item.setDictionaryId(dictionary.getId());
                dictionaryItemRepository.save(item);
            }
        }

        log.info("Dictionary updated: ID {}", id);
        return getDictionary(id);
    }

    @Override
    @Transactional
    public void deleteDictionary(Long id) {
        log.info("Deleting dictionary ID: {}", id);
        Dictionary dictionary = getDictionaryById(id);

        // Delete associated items first
        Page<DictionaryItem> items = dictionaryItemRepository.findByDictionaryId(id, Pageable.unpaged());
        items.forEach(item -> dictionaryItemRepository.deleteById(item.getId()));

        // Delete dictionary
        dictionaryRepository.delete(dictionary);
        log.info("Dictionary deleted: ID {}", id);
    }

    @Override
    public DictionaryDTO getDictionary(Long id) {
        log.info("Retrieving dictionary ID: {}", id);
        Dictionary dictionary = getDictionaryById(id);
        DictionaryDTO dto = dictionaryMapper.toDTO(dictionary);

        // Fetch and set dictionary items
        Page<DictionaryItem> itemsPage = dictionaryItemRepository.findByDictionaryId(dictionary.getId(), Pageable.unpaged());
        dto.setDictionaryItems(itemsPage.getContent().stream()
                .map(dictionaryItemMapper::toDTO)
                .collect(Collectors.toList()));

        return dto;
    }

    @Override
    public Page<DictionaryDTO> getAllDictionaries(int page, int size) {
        log.info("Retrieving all dictionaries with paging - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Dictionary> dictionaryPage = dictionaryRepository.findAll(pageable);

        return dictionaryPage.map(dictionary -> {
            DictionaryDTO dto = dictionaryMapper.toDTO(dictionary);
            Page<DictionaryItem> itemsPage = dictionaryItemRepository.findByDictionaryId(dictionary.getId(), Pageable.unpaged());
            dto.setDictionaryItems(itemsPage.getContent().stream()
                    .map(dictionaryItemMapper::toDTO)
                    .collect(Collectors.toList()));
            return dto;
        });
    }

    private Dictionary getDictionaryById(Long id) {
        return dictionaryRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Dictionary not found: ID {}", id);
                    return new UserFriendlyException("Dictionary not found");
                });
    }
}