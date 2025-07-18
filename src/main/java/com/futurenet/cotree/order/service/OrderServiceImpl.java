package com.futurenet.cotree.order.service;

import com.futurenet.cotree.order.domain.Order;
import com.futurenet.cotree.order.dto.request.OrderRegisterRequest;
import com.futurenet.cotree.order.dto.response.OrderDetailResponse;
import com.futurenet.cotree.order.dto.response.RegisterOrderResponse;
import com.futurenet.cotree.order.repository.OrderRepository;
import com.futurenet.cotree.order.service.exception.OrderErrorCode;
import com.futurenet.cotree.order.service.exception.OrderException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static com.futurenet.cotree.global.constant.PaginationConstants.PAGE_SIZE;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public RegisterOrderResponse registerOrderRequest(OrderRegisterRequest orderRegisterRequest) {

        String orderNumber = createOrderNumber(orderRegisterRequest.getMemberId());
        orderRegisterRequest.setOrderNumber(orderNumber);
        int result = orderRepository.saveOrder(orderRegisterRequest);

        if (result == 0) {
            throw new OrderException(OrderErrorCode.ORDER_REGISTER_FAIL);
        }

        return new RegisterOrderResponse(orderRegisterRequest.getOrderId(), orderNumber);
    }

    @Override
    @Transactional
    public List<Order> getAllOrderByMemberIdAndStatus(Long memberId, String status, int start) {
        return orderRepository.getOrderByMemberIdAndStatus(memberId, status, start, PAGE_SIZE);
    }

    @Override
    @Transactional
    public OrderDetailResponse getOrderByOrderNumber(String orderNumber) {
        return orderRepository.getOrderByOrderNumber(orderNumber);
    }

    private String createOrderNumber(Long memberId) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int rand = (int)(System.nanoTime() % 900) + 100;
        long value = memberId * rand % 999999;

        String uuidSuffix = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();

        return String.format("%s-%06d-%s", timestamp, value, uuidSuffix);
    }
}
