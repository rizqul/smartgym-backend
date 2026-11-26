package com.gymmick.smartgym.controller.dashboard.admin;

import java.time.*;
import java.util.List;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.gymmick.smartgym.dto.pagination.PaginationResponse;
import com.gymmick.smartgym.dto.statistic.TransactionCountResponse;
import com.gymmick.smartgym.dto.transaction.*;
import com.gymmick.smartgym.entity.enums.TransactionStatus;
import com.gymmick.smartgym.services.transaction.TransactionService;
import com.gymmick.smartgym.utils.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/dashboard/transactions")
@RequiredArgsConstructor
public class AdminTransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> findAll(TransactionSearchRequest request) {
        PaginationResponse<TransactionResponse> response = transactionService.findAllTransaction(request);

        return ApiResponse.success(HttpStatus.OK, response.getData(), response.getPagination());
    }

    @GetMapping(path = "/count")
    public ResponseEntity<ApiResponse<TransactionCountResponse>> countTransactionStats(
            @RequestParam(required = false) Integer year) {

        int targetYear = (year != null) ? year : LocalDate.now(ZoneId.systemDefault()).getYear();

        Long incomeYearly = transactionService.sumTotalIncomeYearly(targetYear, ZoneId.systemDefault());
        Long countTransactionSuccess = transactionService.countTransactionByStatus(TransactionStatus.SUCCESS);
        Long countTransactionFailed = transactionService.countTransactionByStatus(TransactionStatus.FAILURE)
                + transactionService.countTransactionByStatus(TransactionStatus.CANCELLED);
        Long countTransactionProcess = transactionService.countTransactionByStatus(TransactionStatus.PENDING);

        TransactionCountResponse response = TransactionCountResponse.builder()
                .sumTransactionYearly(incomeYearly)
                .transactionSuccess(countTransactionSuccess)
                .transactionFailed(countTransactionFailed)
                .transactionProcess(countTransactionProcess)
                .build();

        return ApiResponse.success(HttpStatus.OK, response);
    }

    @GetMapping(path = "/csv-data")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> dataCsv() {

        List<TransactionResponse> transactions = transactionService.getAllTransactionData();

        String message = "success retrieve response";

        return ApiResponse.success(HttpStatus.OK, message, transactions);
    }

}
