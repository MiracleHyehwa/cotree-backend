package com.futurenet.cotree.order.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuantityDecreaseRequest {
    private Long itemId;
    private int quantity;
}
