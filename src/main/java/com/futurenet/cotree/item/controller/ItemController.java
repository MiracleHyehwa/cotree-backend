package com.futurenet.cotree.item.controller;

import com.futurenet.cotree.auth.security.dto.UserPrincipal;
import com.futurenet.cotree.global.dto.response.ApiResponse;
import com.futurenet.cotree.item.dto.response.ItemDetailResponse;
import com.futurenet.cotree.item.dto.response.ItemResponse;
import com.futurenet.cotree.item.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<?> getItemsByCategory(@RequestParam(name = "categoryId", required = false, defaultValue = "0") Long categoryId, //카테고리 0이면 전체 조회
                                                @RequestParam int page) {
        List<ItemResponse> result = itemService.getItemsByCategory(categoryId, page);
        return ResponseEntity.ok(new ApiResponse<>("IT100", result));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<?> getItemDetail (@Valid @AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long itemId) {
        ItemDetailResponse result = itemService.getItemDetail(userPrincipal.getId() ,itemId);
        return ResponseEntity.ok(new ApiResponse<>("IT101", result));
    }

    @GetMapping("/eco")
    public ResponseEntity<?> getEcoItems(@RequestParam int page){
        List<ItemResponse> result = itemService.getEcoItems(page);
        return ResponseEntity.ok(new ApiResponse<>("IT102", result));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchItems(@Valid @AuthenticationPrincipal UserPrincipal userPrincipal,
                                         @RequestParam String keyword,
                                         @RequestParam (name = "categoryId", required = false, defaultValue = "0") Long categoryId, //카테고리 0이면 전체 조회
                                         @RequestParam int page,
                                         @RequestParam(name = "isGreen", required = false) String isGreen) {
        List<ItemResponse> result = itemService.searchItems(userPrincipal.getId(), keyword, categoryId, page, isGreen);
        return ResponseEntity.ok(new ApiResponse<>("IT103", result));
    }
}
