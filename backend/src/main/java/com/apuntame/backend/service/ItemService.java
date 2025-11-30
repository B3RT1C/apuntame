package com.apuntame.backend.service;

import com.apuntame.backend.constant.ErrorMessages;
import com.apuntame.backend.constant.FilterMode;
import com.apuntame.backend.exception.InvalidDataException;
import com.apuntame.backend.exception.ResourceNotFoundException;
import com.apuntame.backend.model.Category;
import com.apuntame.backend.model.Item;
import com.apuntame.backend.model.Section;
import com.apuntame.backend.repository.CategoryRepository;
import com.apuntame.backend.repository.ItemRepository;
import com.apuntame.backend.repository.SectionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final SectionRepository sectionRepository;

    public ItemService(ItemRepository itemRepository, CategoryRepository categoryRepository, SectionRepository sectionRepository) {
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
        this.sectionRepository = sectionRepository;
    }

    public List<Item> getAllItems(Integer limit) {
        if (limit != null && limit > 0) {
            return itemRepository.findAll(PageRequest.of(0, limit)).getContent();
        }
        return itemRepository.findAll();
    }

    public List<Item> getItemsByCategories(List<Integer> categoryIds, String filterMode) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return getAllItems(null);
        }

        if (FilterMode.AND.equalsIgnoreCase(filterMode)) {
            return itemRepository.findByCategoryIdsAnd(categoryIds, categoryIds.size());
        } else {
            return itemRepository.findByCategoryIdsOr(categoryIds);
        }
    }

    public List<Item> getItemsBySections(List<Integer> sectionIds, String filterMode) {
        if (sectionIds == null || sectionIds.isEmpty()) {
            return getAllItems(null);
        }

        if (FilterMode.AND.equalsIgnoreCase(filterMode)) {
            return itemRepository.findBySectionIdsAnd(sectionIds, sectionIds.size());
        } else {
            return itemRepository.findBySectionIdsOr(sectionIds);
        }
    }

    public Item createItem(Item item) {
        validateItem(item);

        if (item.getCategories() != null && !item.getCategories().isEmpty()) {
            List<Category> managedCategories = new ArrayList<>();
            for (Category category : item.getCategories()) {
                if (category.getId() != null) {
                    Category managedCategory = categoryRepository.findById(category.getId())
                            .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + category.getId()));
                    managedCategories.add(managedCategory);
                }
            }
            item.setCategories(managedCategories);
        }

        if (item.getSections() != null && !item.getSections().isEmpty()) {
            List<Section> managedSections = new ArrayList<>();
            for (Section section : item.getSections()) {
                if (section.getId() != null) {
                    Section managedSection = sectionRepository.findById(section.getId())
                            .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + section.getId()));
                    managedSections.add(managedSection);
                }
            }
            item.setSections(managedSections);
        }

        return itemRepository.save(item);
    }

    public Item getItemById(Integer id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.ITEM_NOT_FOUND, id)));
    }

    public Item updateItem(Integer id, Item itemDetails) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.ITEM_NOT_FOUND, id)));

        if (itemDetails.getName() != null && !itemDetails.getName().trim().isEmpty()) {
            item.setName(itemDetails.getName());
        }

        if (itemDetails.getPrice() != null && itemDetails.getPrice().compareTo(BigDecimal.ZERO) > 0) {
            item.setPrice(itemDetails.getPrice());
        }

        if (itemDetails.getCategories() != null) {
            List<Category> managedCategories = new ArrayList<>();
            for (Category category : itemDetails.getCategories()) {
                if (category.getId() != null) {
                    Category managedCategory = categoryRepository.findById(category.getId())
                            .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + category.getId()));
                    managedCategories.add(managedCategory);
                }
            }
            item.setCategories(managedCategories);
        }

        if (itemDetails.getSections() != null) {
            List<Section> managedSections = new ArrayList<>();
            for (Section section : itemDetails.getSections()) {
                if (section.getId() != null) {
                    Section managedSection = sectionRepository.findById(section.getId())
                            .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + section.getId()));
                    managedSections.add(managedSection);
                }
            }
            item.setSections(managedSections);
        }

        return itemRepository.save(item);
    }

    public void deleteItem(Integer id) {
        if (!itemRepository.existsById(id)) {
            throw new ResourceNotFoundException(String.format(ErrorMessages.ITEM_NOT_FOUND, id));
        }
        itemRepository.deleteById(id);
    }

    private void validateItem(Item item) {
        if (item.getName() == null || item.getName().trim().isEmpty()) {
            throw new InvalidDataException(ErrorMessages.ITEM_NAME_EMPTY);
        }
        if (item.getPrice() == null || item.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidDataException(ErrorMessages.ITEM_PRICE_INVALID);
        }
    }
}
