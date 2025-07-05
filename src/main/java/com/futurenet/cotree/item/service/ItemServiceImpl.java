package com.futurenet.cotree.item.service;

import com.futurenet.cotree.auth.security.dto.UserPrincipal;
import com.futurenet.cotree.history.dto.request.MemberActionRequestEvent;
import com.futurenet.cotree.item.domain.Item;
import com.futurenet.cotree.item.dto.response.ItemDetailResponse;
import com.futurenet.cotree.item.dto.response.ItemResponse;
import com.futurenet.cotree.item.repository.ItemRepository;
import com.futurenet.cotree.item.service.exception.ItemErrorCode;
import com.futurenet.cotree.item.service.exception.ItemException;
import com.futurenet.cotree.member.dto.response.MemberGenderAgeResponse;
import com.futurenet.cotree.member.repository.MemberRepository;
import com.futurenet.cotree.order.dto.request.OrderItemRegisterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.futurenet.cotree.global.constant.PaginationConstants.PAGE_SIZE;

@Slf4j
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
    public ItemDetailResponse getItemDetail(UserPrincipal userPrincipal, Long itemId) {
        if (userPrincipal != null) {
            saveMemberActionLog(userPrincipal.getId(), itemId, null);
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
    public List<ItemResponse> searchItems(UserPrincipal userPrincipal, String keyword, Long categoryId, int page, String isGreen) {
        if (userPrincipal != null) {
            saveMemberActionLog(userPrincipal.getId(), null, keyword);
        }

        int start = (page - 1) * PAGE_SIZE;
        List<Item> itemList = itemRepository.searchItems(keyword, categoryId, start, PAGE_SIZE, isGreen);
        return itemList.stream()
                .map(ItemResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<ItemResponse> getTodayItems() {
        List<Item> itemList = itemRepository.getTodayItems();
        return itemList.stream()
                .map(ItemResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void bulkDecreaseStock(List<OrderItemRegisterRequest> orderItemRegisterRequests) {

        int result = itemRepository.bulkDecreaseStock(orderItemRegisterRequests);

        if (result != orderItemRegisterRequests.size()) {
            throw new ItemException(ItemErrorCode.ITEM_QUANTITY_LACK);
        }
    }

    private void saveMemberActionLog(Long memberId, Long itemId, String keyword) {
        MemberGenderAgeResponse info = memberRepository.getMemberGenderAge(memberId);
        MemberActionRequestEvent event;

        //age가 DB상에서 String로 되어있어서 int형식으로 변경
        int age = Integer.parseInt(info.getAge().replace("s", ""));

        if (itemId != null) {
            event = new MemberActionRequestEvent(memberId, info.getGender(), age, itemId);
        }
        else{
            event = new MemberActionRequestEvent(memberId, info.getGender(),age, keyword);
        }
        eventPublisher.publishEvent(event);
    }

    @Transactional
    public void decreaseQuantity(Long itemId, int quantity) {
        log.info("DB 재고 차감 시도: itemId={}, quantity={}", itemId, quantity);
        Item item = itemRepository.getItem(itemId);

        if (item == null) {
            throw new ItemException(ItemErrorCode.ITEM_NOT_FOUND);
        }

        if (item.getQuantity() < quantity) {
            log.warn("재고 부족 오류: itemId={}, 요청 수량={}, 현재 재고={}", itemId, quantity, item.getQuantity());
            throw new ItemException(ItemErrorCode.ITEM_QUANTITY_LACK);
        }

        int updatedRows = itemRepository.decreaseQuantity(itemId, quantity); // ★ 호출 메서드 이름 변경

        // 5. 업데이트 성공 여부를 확인합니다.
        if (updatedRows == 0) {
            log.error("재고 차감 실패: itemId={}, quantity={}. 데이터베이스 업데이트가 발생하지 않았습니다.", itemId, quantity);
            throw new ItemException(ItemErrorCode.ITEM_QUANTITY_LACK);
        }

        log.info("itemId={} 상품의 재고 {}개 차감 완료 (DB 반영)", itemId, quantity);
    }


}
