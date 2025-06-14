package com.futurenet.cotree.item.service;

import com.futurenet.cotree.auth.security.dto.UserPrincipal;
import com.futurenet.cotree.item.domain.Item;
import com.futurenet.cotree.item.dto.response.ItemDetailResponse;
import com.futurenet.cotree.item.dto.response.ItemResponse;
import com.futurenet.cotree.item.repository.ItemRepository;
import com.futurenet.cotree.item.service.exception.ItemErrorCode;
import com.futurenet.cotree.item.service.exception.ItemException;
import com.futurenet.cotree.log.dto.request.MemberActionRequestEvent;
import com.futurenet.cotree.member.dto.response.MemberGenderAgeResponse;
import com.futurenet.cotree.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.futurenet.cotree.global.constant.PaginationConstants.PAGE_SIZE;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public List<ItemResponse> getItemsByCategory(Long categoryId, int page) {
        int start = (page - 1) * PAGE_SIZE;

        List<Item> itemList = itemRepository.getItemsByCategory(categoryId, start, PAGE_SIZE);
        return itemList.stream()
                .map(ItemResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void decreaseStock(Long itemId, int quantity) {
        int result = itemRepository.decreaseStock(itemId, quantity);

        if (result == 0) {
            throw new ItemException(ItemErrorCode.ITEM_QUANTITY_LACK);
        }
    }

    @Override
    @Transactional
    public ItemDetailResponse getItemDetail(Long memberId, Long itemId) {
        if (memberId != null) {
            saveMemberActionLog(memberId, itemId, null);
        }
        return ItemDetailResponse.from(itemRepository.getItemDetailById(itemId));
    }

    @Override
    @Transactional
    public List<ItemResponse> getEcoItems(int page) {
        int start = (page - 1) * PAGE_SIZE;
        List<Item> itemList = itemRepository.getEcoItems(start, PAGE_SIZE);
        return itemList.stream()
                .map(ItemResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<ItemResponse> searchItems(Long memberId, String keyword, Long categoryId, int page, String isGreen) {
        if (memberId != null) {
            saveMemberActionLog(memberId, null, keyword);
        }

        int start = (page - 1) * PAGE_SIZE;
        List<Item> itemList = itemRepository.searchItems(keyword, categoryId, start, PAGE_SIZE, isGreen);
        return itemList.stream()
                .map(ItemResponse::from)
                .collect(Collectors.toList());
    }

    private void saveMemberActionLog(Long memberId, Long itemId, String keyword) {
        MemberGenderAgeResponse info = memberRepository.getMemberGenderAge(memberId);
        MemberActionRequestEvent event;

        if (itemId != null) {
            event = new MemberActionRequestEvent(memberId, info.getGender(), info.getAge(), itemId);
        }
        else{
            event = new MemberActionRequestEvent(memberId, info.getGender(), info.getAge(), keyword);
        }
        eventPublisher.publishEvent(event);
    }
}
