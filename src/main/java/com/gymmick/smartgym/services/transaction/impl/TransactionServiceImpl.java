package com.gymmick.smartgym.services.transaction.impl;

import java.time.Instant;
import java.util.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.gymmick.smartgym.builder.PaginationLinkBuilder;
import com.gymmick.smartgym.dto.common.TransactionResponse;
import com.gymmick.smartgym.dto.dashboard.TransactionFilterRequest;
import com.gymmick.smartgym.dto.pagination.PaginationMeta;
import com.gymmick.smartgym.dto.pagination.PaginationResponse;
import com.gymmick.smartgym.dto.transaction.TransactionData;
import com.gymmick.smartgym.entity.*;
import com.gymmick.smartgym.entity.enums.TransactionStatus;
import com.gymmick.smartgym.entity.enums.TransactionType;
import com.gymmick.smartgym.exceptions.BadRequestException;
import com.gymmick.smartgym.mappers.TransactionMapping;
import com.gymmick.smartgym.repositories.TransactionRespository;
import com.gymmick.smartgym.services.transaction.TransactionService;
import com.gymmick.smartgym.specification.TransactionSpecification;
import com.midtrans.httpclient.error.MidtransError;
import com.midtrans.service.MidtransSnapApi;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionServiceImpl implements TransactionService {

  private final MidtransSnapApi midtransSnapApi;

  private final TransactionRespository transactionRespository;

  private final TransactionMapping transactionMapping;

  @Override
  @Transactional
  public String createTransaction(User user, TransactionData data) throws MidtransError {

    Map<String, Object> params = new HashMap<>();

    Map<String, String> transactionDetails = new HashMap<>();
    transactionDetails.put("order_id", data.getOrderId());
    transactionDetails.put("gross_amount", data.getGrossAmount().toString());

    Map<String, String> callbacks = new HashMap<>();
    callbacks.put("finish", data.getCallbackUrl());
    callbacks.put("error", data.getCallbackUrl());

    params.put("transaction_details", transactionDetails);
    params.put("item_details", data.getItemDetails());
    params.put("callbacks", callbacks);

    Map<String, Object> response = midtransSnapApi.createTransaction(params).toMap();
    Transaction transaction = Transaction.builder()
        .packageId(data.getPackageId())
        .orderId(data.getOrderId())
        .paymentUrl(response.get("redirect_url").toString())
        .member(user)
        .type(data.getType())
        .amount(data.getGrossAmount())
        .build();

    transactionRespository.save(transaction);

    return response.get("redirect_url").toString();
  }

  @Override
  @Transactional
  public Transaction updateTransaction(String orderId, String status) {

    Transaction transaction = transactionRespository.findByOrderId(orderId)
        .orElseThrow(() -> new BadRequestException("Data Tidak Ditemukan!"));

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
  public PaginationResponse<TransactionResponse> findAllTransaction(TransactionFilterRequest request) {

    Integer page = request.getPage();
    Integer size = request.getSize();

    Specification<Transaction> specification = TransactionSpecification.getTransactionByTransactionType(request,
        TransactionType.MEMBERSHIP);

    Sort sortBy = Sort.by(Transaction::getCreatedAt).descending();
    PageRequest pageRequest = PageRequest.of(page, size, sortBy);

    Page<Transaction> transactionPage = transactionRespository.findAll(specification, pageRequest);

    List<TransactionResponse> transactionResponse = transactionMapping.toListResponses(transactionPage.getContent());

    PaginationMeta paginationMeta = PaginationLinkBuilder.build(page, transactionPage.getTotalPages(), size,
        transactionPage.getTotalElements(),
        p -> ServletUriComponentsBuilder.fromCurrentRequest().replaceQueryParam("page", p).toUriString());

    PaginationResponse<TransactionResponse> paginationResponse = PaginationResponse.<TransactionResponse>builder()
        .data(transactionResponse)
        .pagination(paginationMeta)
        .build();

    return paginationResponse;
  }
}
