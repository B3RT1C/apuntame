package com.apuntame.backend.controller;

import com.apuntame.backend.constant.FilterMode;
import com.apuntame.backend.model.Item;
import com.apuntame.backend.service.ItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public ResponseEntity<List<Item>> getAllItems(
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) List<Integer> categories,
            @RequestParam(required = false) List<Integer> sections,
            @RequestParam(required = false, defaultValue = "OR") String filterMode) {
        List<Item> items;

        if (categories != null && !categories.isEmpty()) {
            items = itemService.getItemsByCategories(categories, filterMode);
        } else if (sections != null && !sections.isEmpty()) {
            items = itemService.getItemsBySections(sections, filterMode);
        } else {
            items = itemService.getAllItems(limit);
        }

        return ResponseEntity.ok(items);
    }

    @PostMapping
    public ResponseEntity<Item> createItem(@RequestBody Item item) {
        Item createdItem = itemService.createItem(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Integer id) {
        Item item = itemService.getItemById(id);
        return ResponseEntity.ok(item);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Item> updateItem(@PathVariable Integer id, @RequestBody Item itemDetails) {
        Item updatedItem = itemService.updateItem(id, itemDetails);
        return ResponseEntity.ok(updatedItem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Integer id) {
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}