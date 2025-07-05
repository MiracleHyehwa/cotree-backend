package com.futurenet.cotree.order.service.exception;

import com.futurenet.cotree.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum OrderErrorCode implements ErrorCode {

    ORDER_REGISTER_FAIL("OR000", HttpStatus.BAD_REQUEST),
    ORDER_ITEM_REGISTER_FAIL("OR001", HttpStatus.BAD_REQUEST),
    ORDER_STATUS_UPDATE_FAIL("OR002", HttpStatus.BAD_REQUEST),
    ORDER_NOT_FOUND("OR003", HttpStatus.BAD_REQUEST),
    ORDER_ITEM_NOT_FOUND("OR004", HttpStatus.BAD_REQUEST),
    ORDER_ACCESS_DENIED("OR005", HttpStatus.FORBIDDEN),
    ORDER_PROCESSING_FAILED("OR006", HttpStatus.BAD_REQUEST);

    private final String code;
    private final HttpStatus httpStatus;
}
