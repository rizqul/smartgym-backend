package com.gymmick.smartgym.services.transaction.impl;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.gymmick.smartgym.builder.PaginationLinkBuilder;
import com.gymmick.smartgym.dto.pagination.*;
import com.gymmick.smartgym.dto.transaction.*;
import com.gymmick.smartgym.entity.*;
import com.gymmick.smartgym.entity.enums.*;
import com.gymmick.smartgym.exceptions.BadRequestException;
import com.gymmick.smartgym.mappers.TransactionMapping;
import com.gymmick.smartgym.repositories.TransactionRespository;
import com.gymmick.smartgym.services.transaction.TransactionService;
import com.gymmick.smartgym.specification.TransactionSpecification;
import com.midtrans.httpclient.error.MidtransError;
import com.midtrans.service.*;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionServiceImpl implements TransactionService {

    private final MidtransSnapApi midtransSnapApi;
    private final MidtransCoreApi midtransCoreApi;

    private final TransactionRespository transactionRespository;

    private final TransactionMapping transactionMapping;

    @Override
    public Map<String, Object> checkTransactionStatus(String orderId) throws MidtransError {
        return midtransCoreApi.checkTransaction(orderId).toMap();
    }

    @Override
    public PaginationResponse<TransactionResponse> findAllTransaction(TransactionFilterRequest request) {

        Integer page = request.getPage();
        Integer size = request.getSize();

        Specification<Transaction> specification = TransactionSpecification.getTransaction(request);

        Sort sortBy = Sort.by(Transaction::getCreatedAt).descending();
        PageRequest pageRequest = PageRequest.of(page, size, sortBy);

        Page<Transaction> transactionPage = transactionRespository.findAll(specification, pageRequest);

        List<TransactionResponse> transactionResponse = transactionMapping
                .toListResponses(transactionPage.getContent());

        PaginationMeta paginationMeta = PaginationLinkBuilder.build(page, transactionPage.getTotalPages(), size,
                transactionPage.getTotalElements(),
                p -> ServletUriComponentsBuilder.fromCurrentRequest().replaceQueryParam("page", p).toUriString());

        PaginationResponse<TransactionResponse> paginationResponse = PaginationResponse.<TransactionResponse>builder()
                .data(transactionResponse)
                .pagination(paginationMeta)
                .build();

        return paginationResponse;
    }

    @Override
    public PaginationResponse<TransactionResponse> findAllMyTransaction(User user, TransactionFilterRequest request) {

        Integer page = request.getPage();
        Integer size = request.getSize();

        Specification<Transaction> specification = TransactionSpecification.getMyTransaction(user, request);

        Sort sortBy = Sort.by(Transaction::getCreatedAt).descending();
        PageRequest pageRequest = PageRequest.of(page, size, sortBy);

        Page<Transaction> transactionPage = transactionRespository.findAll(specification, pageRequest);

        List<TransactionResponse> transactionResponse = transactionMapping
                .toListResponses(transactionPage.getContent());

        PaginationMeta paginationMeta = PaginationLinkBuilder.build(page, transactionPage.getTotalPages(), size,
                transactionPage.getTotalElements(),
                p -> ServletUriComponentsBuilder.fromCurrentRequest().replaceQueryParam("page", p).toUriString());

        PaginationResponse<TransactionResponse> paginationResponse = PaginationResponse.<TransactionResponse>builder()
                .data(transactionResponse)
                .pagination(paginationMeta)
                .build();

        return paginationResponse;
    }

    @Override
    @Transactional
    public String createTransaction(User user, TransactionData data) throws MidtransError {

        String orderId = UUID.randomUUID().toString();
        Map<String, Object> params = new HashMap<>();

        Map<String, String> transactionDetails = new HashMap<>();
        transactionDetails.put("order_id", orderId);
        transactionDetails.put("gross_amount", data.getGrossAmount().toString());

        List<String> payments = new ArrayList<>();
        payments.add(data.getPaymentType().toString());

        Map<String, String> callbacks = new HashMap<>();
        callbacks.put("finish", data.getCallbackUrl());
        callbacks.put("error", data.getCallbackUrl());

        params.put("transaction_details", transactionDetails);
        params.put("item_details", data.getItemDetails());
        params.put("enabled_payments", payments);
        params.put("callbacks", callbacks);

        Map<String, Object> response = midtransSnapApi.createTransaction(params).toMap();
        Transaction transaction = Transaction.builder()
                .member(user)
                .orderId(orderId)
                .packageId(data.getPackageId())
                .name(data.getPackageName())
                .paymentUrl(response.get("redirect_url").toString())
                .paymentType(data.getPaymentType())
                .amount(data.getGrossAmount())
                .type(data.getType())
                .build();

        transactionRespository.save(transaction);

        return response.get("redirect_url").toString();
    }

    @Override
    @Transactional
    public Transaction updateTransaction(String orderId, String status, String paymentType) {
        Transaction transaction = this.findByOrderId(orderId);
        transaction.setExpiredAt(Instant.now().plus(Duration.ofMinutes(15)));

        if (status.equals("expire")) {
            transaction.setStatus(TransactionStatus.EXPIRED);
        }

        if (status.equals("settlement")) {
            transaction.setPaidAt(Instant.now());
            transaction.setStatus(TransactionStatus.PROCESS);
        }

        return transactionRespository.save(transaction);
    }

    @Override
    @Transactional
    public void cancelTransaction(String orderId) throws MidtransError {
        Transaction transaction = this.findByOrderId(orderId);
        midtransCoreApi.cancelTransaction(orderId);

        transaction.setStatus(TransactionStatus.CANCELLED);
        transactionRespository.save(transaction);
    }

    public Transaction findByOrderId(String orderId) {
        return transactionRespository.findByOrderId(orderId)
                .orElseThrow(() -> new BadRequestException("Data Tidak Ditemukan!"));
    }
}
