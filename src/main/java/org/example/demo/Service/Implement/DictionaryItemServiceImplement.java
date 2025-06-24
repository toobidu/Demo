package org.example.demo.Service.Implement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.demo.Exception.UserFriendlyException;
import org.example.demo.Mapper.DictionaryItemMapper;
import org.example.demo.Modal.DTO.Dictionaries.DictionaryItemDTO;
import org.example.demo.Modal.Entity.Dictionaries.Dictionary;
import org.example.demo.Modal.Entity.Dictionaries.DictionaryItem;
import org.example.demo.Repository.DictionaryItemRepository;
import org.example.demo.Repository.DictionaryRepository;
import org.example.demo.Service.Interface.IDictionaryItemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DictionaryItemServiceImplement implements IDictionaryItemService {

    private final DictionaryItemRepository dictionaryItemRepository;
    private final DictionaryRepository dictionaryRepository;
    private final DictionaryItemMapper dictionaryItemMapper;

    @Override
    public DictionaryItemDTO createDictionaryItem(DictionaryItemDTO dto) {
        log.info("Creating dictionary item for dictionaryId: {}", dto.getDictionaryId());

        Dictionary dictionary = dictionaryRepository.findById(dto.getDictionaryId())
                .orElseThrow(() -> {
                    log.error("Dictionary not found with ID: {}", dto.getDictionaryId());
                    return new UserFriendlyException("Dictionary not found");
                });

        DictionaryItem item = dictionaryItemMapper.toEntity(dto);
        item.setDictionary(dictionary);

        DictionaryItem savedItem = dictionaryItemRepository.save(item);
        log.info("Dictionary item created with ID: {}", savedItem.getId());

        return dictionaryItemMapper.toDTO(savedItem);
    }

    @Override
    public DictionaryItemDTO updateDictionaryItem(Long id, DictionaryItemDTO dto) {
        log.info("Updating dictionary item ID: {}", id);

        DictionaryItem item = dictionaryItemRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Dictionary item not found with ID: {}", id);
                    return new UserFriendlyException("Dictionary item not found");
                });

        item.setCode(dto.getCode());
        item.setName(dto.getName());

        DictionaryItem updatedItem = dictionaryItemRepository.save(item);
        log.info("Dictionary item updated: ID {}", updatedItem.getId());

        return dictionaryItemMapper.toDTO(updatedItem);
    }

    @Override
    public void deleteDictionaryItem(Long id) {
        log.info("Deleting dictionary item ID: {}", id);

        if (!dictionaryItemRepository.existsById(id)) {
            log.error("Dictionary item not found with ID: {}", id);
            throw new UserFriendlyException("Dictionary item not found");
        }

        dictionaryItemRepository.deleteById(id);
        log.info("Dictionary item deleted: ID {}", id);
    }

    @Override
    public DictionaryItemDTO getDictionaryItem(Long id) {
        log.info("Retrieving dictionary item ID: {}", id);

        DictionaryItem item = dictionaryItemRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Dictionary item not found with ID: {}", id);
                    return new UserFriendlyException("Dictionary item not found");
                });

        return dictionaryItemMapper.toDTO(item);
    }

    @Override
    public Page<DictionaryItemDTO> getDictionaryItems(Long dictionaryId, int page, int size) {
        log.info("Retrieving dictionary items for dictionaryId: {} with paging", dictionaryId);
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<DictionaryItem> items = (dictionaryId != null)
                ? dictionaryItemRepository.findByDictionary_Id(dictionaryId, pageable)
                : dictionaryItemRepository.findAll(pageable);
        return items.map(dictionaryItemMapper::toDTO);
    }
}
