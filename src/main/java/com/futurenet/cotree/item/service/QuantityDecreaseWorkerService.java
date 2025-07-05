package com.futurenet.cotree.item.service;

import com.futurenet.cotree.order.dto.request.QuantityDecreaseRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuantityDecreaseWorkerService implements ApplicationRunner, DisposableBean {
    private final BlockingQueue<QuantityDecreaseRequest> quantityDecreaseQueue;
    private final ItemService itemService;
    private ExecutorService executorService;

    @Override
    public void run(ApplicationArguments args) {
        executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            log.info("재고 차감 워커 스레드 시작.");
            while (!Thread.currentThread().isInterrupted()) {
                QuantityDecreaseRequest request = null;
                try {
                    request = quantityDecreaseQueue.take();
                    log.info("재고 차감 요청 처리 시작: itemId={}, quantity={}", request.getItemId(), request.getQuantity());

                    itemService.decreaseQuantity(request.getItemId(), request.getQuantity());
                    log.info("재고 차감 성공: itemId={}, quantity={}", request.getItemId(), request.getQuantity());

                    // TODO: 재고 차감 성공 후 필요한 추가 로직

                } catch (InterruptedException e) {
                    log.warn("재고 차감 워커 스레드 인터럽트 발생, 종료합니다.");
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    String itemId = (request != null) ? String.valueOf(request.getItemId()) : "N/A";
                    String quantity = (request != null) ? String.valueOf(request.getQuantity()) : "N/A";
                    log.error("재고 차감 실패: itemId={}, quantity={}, 에러: {}", itemId, quantity, e.getMessage(), e);
                    // TODO: 재고 차감 실패 시 보상 트랜잭션 또는 재시도 로직
                }
            }
        });
    }
    @Override
    public void destroy() {
        if (executorService != null) {
            executorService.shutdown();
            log.info("재고 차감 워커 스레드 풀 종료 요청됨.");
            try {
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                    log.warn("재고 차감 워커 스레드 강제 종료됨: 아직 처리되지 않은 요청이 있을 수 있습니다.");
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
                log.error("재고 차감 워커 스레드 종료 중 인터럽트 발생.", e);
            }
            log.info("재고 차감 워커 스레드 종료 완료.");
        }
    }
}
