package com.apuntame.backend.service;

import com.apuntame.backend.constant.ErrorMessages;
import com.apuntame.backend.exception.InvalidDataException;
import com.apuntame.backend.exception.ResourceNotFoundException;
import com.apuntame.backend.model.Item;
import com.apuntame.backend.repository.ItemRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<Item> getAllItems(Integer limit) {
        if (limit != null && limit > 0) {
            return itemRepository.findAll(PageRequest.of(0, limit)).getContent();
        }
        return itemRepository.findAll();
    }

    public Item createItem(Item item) {
        validateItem(item);
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