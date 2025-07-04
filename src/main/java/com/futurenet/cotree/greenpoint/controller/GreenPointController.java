package com.futurenet.cotree.greenpoint.controller;

import com.futurenet.cotree.auth.security.dto.UserPrincipal;
import com.futurenet.cotree.global.dto.response.ApiResponse;
import com.futurenet.cotree.greenpoint.dto.GreenPointHistoryResponse;
import com.futurenet.cotree.greenpoint.dto.GreenPointSummaryResponse;
import com.futurenet.cotree.greenpoint.service.GreenPointService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/greenpoint")
public class GreenPointController {
    private final GreenPointService greenPointService;

    @GetMapping
    public ResponseEntity<?> getPointHistory(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestParam int page) {
        Long memberId = userPrincipal.getId();
        List<GreenPointHistoryResponse> greenPointHistoryResponse = greenPointService.getPointHistory(memberId, page);
        return ResponseEntity.ok(new ApiResponse<>("GP100", greenPointHistoryResponse));
    }

    @GetMapping("/summary")
    public ResponseEntity<?> getGreenPointSummary(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long memberId = userPrincipal.getId();
        GreenPointSummaryResponse greenPointSummaryResponse = greenPointService.getGreenPointSummary(memberId);
        return ResponseEntity.ok(new ApiResponse<>("GP100", greenPointSummaryResponse));
    }


}
