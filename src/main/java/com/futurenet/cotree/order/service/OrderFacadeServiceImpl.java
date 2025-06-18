package com.futurenet.cotree.order.service;

import com.futurenet.cotree.item.service.ItemService;
import com.futurenet.cotree.order.constant.OrderStatus;
import com.futurenet.cotree.order.domain.Order;
import com.futurenet.cotree.order.dto.OrderItemDto;
import com.futurenet.cotree.order.dto.request.OrderItemRegisterRequest;
import com.futurenet.cotree.order.dto.request.OrderRegisterRequest;
import com.futurenet.cotree.order.dto.request.OrderRequest;
import com.futurenet.cotree.order.dto.response.OrderDetailResponse;
import com.futurenet.cotree.order.dto.response.OrderItemResponse;
import com.futurenet.cotree.order.dto.response.OrderResponse;
import com.futurenet.cotree.order.dto.response.RegisterOrderResponse;
import com.futurenet.cotree.order.service.exception.OrderErrorCode;
import com.futurenet.cotree.order.service.exception.OrderException;
import com.futurenet.cotree.payment.dto.request.PaymentRequestEvent;
import com.futurenet.cotree.payment.service.exception.PaymentErrorCode;
import com.futurenet.cotree.payment.service.exception.PaymentException;
import com.futurenet.cotree.shoppingbasket.dto.request.ShoppingBasketDeleteRequestEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderFacadeServiceImpl implements OrderFacadeService {

    private final OrderService orderService;
    private final ItemService itemService;
    private final OrderItemService orderItemService;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 사용자의 주문 요청을 처리합니다.
     * 1. 각 주문 상품의 재고를 차감합니다.
     * 2. 주문 정보를 저장합니다.
     * 3. 주문 상품 정보를 저장합니다.
     * 주문 완료 이벤트를 발행하여 결제 처리를 트리거합니다.
     *
     * @param orderRequest 사용자의 주문 요청 정보
     * */
    @Override
    @Transactional
    public String registerOrder(OrderRequest orderRequest, Long memberId) {

        // TODO: 반복문 내의 단발성 쿼리 Bulk 처리하기

        for (OrderItemRegisterRequest item : orderRequest.getOrderItems()) {
            itemService.decreaseStock(item.getItemId(), item.getQuantity());
        }

        OrderRegisterRequest orderRegisterRequest = OrderRegisterRequest.from(orderRequest);
        orderRegisterRequest.setMemberId(memberId);

        RegisterOrderResponse response = orderService.registerOrderRequest(orderRegisterRequest);

        for (OrderItemRegisterRequest item : orderRequest.getOrderItems()) {
            item.setOrderId(response.getOrderId());
            orderItemService.registerOrderItem(item);
        }

        eventPublisher.publishEvent(PaymentRequestEvent.of(response.getOrderId(), memberId, orderRequest));

        if (orderRequest.isCart()) {
            eventPublisher.publishEvent(new ShoppingBasketDeleteRequestEvent(memberId, orderRequest.getOrderItems()));
        }

        return response.getOrderNumber();
    }


    /**
     * 주문 상태별 주문 목록을 조회합니다.
     * 1. 회원의 정보와 조회하고자 하는 주문 상태를 통해 주문 식별자들을 탐색합니다. 이 때 status가 없으면 전체 조회입니다.
     * 2. 주문 식별자들을 이용하여 해당 식별자에 해당하는 주문 상품들을 찾습니다.
     * 3. Map을 이용하여 주문과 주문 상품들을 묶습니다.
     * 4. Java Stream을 이용하여 결과를 반환합니다.
     *
     * @param status PAID: 결제 완료, PENDING: 결제 대기, null: 전체 주문
     * */
    @Override
    @Transactional
    public List<OrderResponse> getOrdersByMember(Long memberId, String status) {
        List<Order> orders = orderService.getAllOrderByMemberIdAndStatus(memberId, status);
        orders.sort(Comparator.comparing(Order::getOrderDate).reversed());

        List<Long> orderIds = orders.stream()
                .map(Order::getId)
                .toList();

        if (orderIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<OrderItemDto> orderItems = orderItemService.getAllOrderItemsByOrderIds(orderIds);

        Map<Long, List<OrderItemDto>> orderMaps = orderItems.stream()
                .collect(Collectors.groupingBy(OrderItemDto::getOrderId));

        return orders.stream().map(order -> {
            List<OrderItemResponse> itemResponses = orderMaps.getOrDefault(order.getId(), List.of())
                    .stream()
                    .map(OrderItemDto::toResponse)
                    .toList();

            return OrderResponse.of(order, itemResponses);
        })
                .toList();
    }

    @Override
    @Transactional
    public OrderDetailResponse getOrderDetail(String orderNumber) {

        OrderDetailResponse orderDetailResponse = orderService.getOrderByOrderNumber(orderNumber);

        if (orderDetailResponse == null) {
            throw new OrderException(OrderErrorCode.ORDER_NOT_FOUND);
        }

        if (!OrderStatus.SUCCESS.getStatus().equals(orderDetailResponse.getStatus())) {
            throw new PaymentException(PaymentErrorCode.PAYMENT_FAIL);
        }

        List<OrderItemResponse> orderItems = orderItemService.getOrderItemsByOrderId(orderDetailResponse.getOrderId());

        if (orderItems.isEmpty()) {
            throw new OrderException(OrderErrorCode.ORDER_ITEM_NOT_FOUND);
        }

        orderDetailResponse.setOrderItems(orderItems);

        return orderDetailResponse;
    }
}
