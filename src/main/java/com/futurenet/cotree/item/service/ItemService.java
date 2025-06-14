package com.futurenet.cotree.item.service;

import com.futurenet.cotree.auth.security.dto.UserPrincipal;
import com.futurenet.cotree.item.dto.response.ItemDetailResponse;
import com.futurenet.cotree.item.dto.response.ItemResponse;

import java.util.List;

public interface ItemService {
    void decreaseStock(Long itemId, int quantity);
    List<ItemResponse> getItemsByCategory(Long categoryId, int page);
    ItemDetailResponse getItemDetail(Long memberId, Long id);
    List<ItemResponse> getEcoItems(int page);
    List<ItemResponse> searchItems(Long memberId, String keyword, Long categoryId, int page, String isGreen);
}
