package com.futurenet.cotree.payment.service;

import com.futurenet.cotree.payment.dto.request.PaymentConfirmRequest;
import com.futurenet.cotree.payment.dto.request.PaymentRequest;

public interface PaymentService {
    void pay(PaymentRequest paymentRequest);
    void payConfirm(PaymentConfirmRequest paymentConfirmRequest, Long memberId);
}
