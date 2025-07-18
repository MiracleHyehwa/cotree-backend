package com.futurenet.cotree.shoppingbasket.controller;

import com.futurenet.cotree.auth.security.dto.UserPrincipal;
import com.futurenet.cotree.global.dto.response.ApiResponse;
import com.futurenet.cotree.shoppingbasket.dto.request.ShoppingBasketAddRequest;
import com.futurenet.cotree.shoppingbasket.dto.response.ShoppingBasketCountResponse;
import com.futurenet.cotree.shoppingbasket.dto.response.ShoppingBasketItemsResponse;
import com.futurenet.cotree.shoppingbasket.service.ShoppingBasketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shopping-basket")
public class ShoppingBasketController {
    private final ShoppingBasketService shoppingBasketService;

    @GetMapping
    public ResponseEntity<?> getBasketItems(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long memberId = userPrincipal.getId();
        List<ShoppingBasketItemsResponse> shoppingBasketItems = shoppingBasketService.getAllShoppingBasketItemsByMemberId(memberId);
        return ResponseEntity.ok(new ApiResponse<>("SB100", shoppingBasketItems));
    }

    @PostMapping
    public ResponseEntity<?> saveBasketItem(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody @Valid ShoppingBasketAddRequest shoppingBasketAddRequest) {
        Long itemId = shoppingBasketAddRequest.getItemId();
        Integer quantity = shoppingBasketAddRequest.getQuantity();
        shoppingBasketService.saveBasketItem(userPrincipal, itemId, quantity);
        return ResponseEntity.ok(new ApiResponse<>("SB100", null));
    }

    @DeleteMapping("/{basketItemId}")
    public ResponseEntity<?> deleteBasketItem(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long basketItemId) {
        Long memberId = userPrincipal.getId();
        shoppingBasketService.deleteBasketItem(memberId, basketItemId);
        return ResponseEntity.ok(new ApiResponse<>("SB100", null));
    }

    @GetMapping("/count")
    public ResponseEntity<?> getBasketItemCount(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        int count = (userPrincipal == null) ? 0 : shoppingBasketService.countBasketItems(userPrincipal.getId());
        ShoppingBasketCountResponse shoppingBasketCountResponse = new ShoppingBasketCountResponse(count);
        return ResponseEntity.ok(new ApiResponse<>("SB101", shoppingBasketCountResponse));
    }
}
