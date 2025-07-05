package com.futurenet.cotree.global.config;

import com.futurenet.cotree.order.dto.request.QuantityDecreaseRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
public class AppConfig {

    /**
     * 재고 차감 요청을 담을 인메모리 큐를 스프링 빈으로 등록.
     * 이 큐는 BlockingQueue 인터페이스를 구현하며, LinkedBlockingQueue는 내부적으로
     * 스레드 안전하게 동작하므로 여러 스레드에서 동시에 접근해도 문제가 없다.
     *
     * @return BlockingQueue<QuantityDecreaseRequest> 타입의 인메모리 큐 인스턴스
     */
    @Bean
    public BlockingQueue<QuantityDecreaseRequest> quantityDecreaseQueue() {
        return new LinkedBlockingQueue<>();
    }
}
