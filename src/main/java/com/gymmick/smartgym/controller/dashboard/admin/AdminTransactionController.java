package com.gymmick.smartgym.controller.dashboard.admin;

import java.util.List;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.gymmick.smartgym.dto.pagination.PaginationResponse;
import com.gymmick.smartgym.dto.transaction.*;
import com.gymmick.smartgym.services.transaction.TransactionService;
import com.gymmick.smartgym.utils.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/dashboard/transactions")
@RequiredArgsConstructor
public class AdminTransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> findAll(TransactionFilterRequest request) {
        PaginationResponse<TransactionResponse> response = transactionService.findAllTransaction(request);

        return ApiResponse.success(HttpStatus.OK, response.getData(), response.getPagination());
    }
}
