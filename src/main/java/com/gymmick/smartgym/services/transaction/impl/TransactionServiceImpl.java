package com.gymmick.smartgym.services.transaction.impl;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import org.apache.commons.lang3.math.NumberUtils;
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
import com.gymmick.smartgym.exceptions.ResourceNotFoundException;
import com.gymmick.smartgym.mappers.TransactionMapping;
import com.gymmick.smartgym.repositories.TransactionRespository;
import com.gymmick.smartgym.services.transaction.TransactionService;
import com.gymmick.smartgym.specification.TransactionSpecification;
import com.midtrans.httpclient.error.MidtransError;
import com.midtrans.service.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionServiceImpl implements TransactionService {

    private final MidtransSnapApi midtransSnapApi;
    private final MidtransCoreApi midtransCoreApi;

    private final TransactionRespository transactionRespository;

    private final TransactionMapping transactionMapping;

    @Override
    public PaginationResponse<TransactionResponse> findAllTransaction(TransactionSearchRequest request) {

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

        return PaginationResponse.<TransactionResponse>builder()
                .data(transactionResponse)
                .pagination(paginationMeta)
                .build();
<<<<<<< HEAD

<<<<<<< HEAD
<<<<<<< HEAD
        return paginationResponse;
=======
    return paginationResponse;
  }

  @Override
  public PaginationResponse<TransactionResponse> findAllMyTransaction(User user, TransactionSearchRequest request) {

    Integer page = request.getPage();
    Integer size = request.getSize();

    Specification<Transaction> specification = TransactionSpecification.getMyTransaction(user, request);

    String sortByType = request.getSortBy() != null && !request.getSortBy().isBlank() ? request.getSortBy()
        : "createdAt";
    String sortByDirection = request.getDirection() != null && !request.getSortBy().isBlank() ? request.getDirection()
        : "desc";

    Sort sortBy = Sort.by(Sort.Direction.fromString(sortByDirection), sortByType);
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
  public Transaction findByOrderId(String orderId) {
    return transactionRespository.findByOrderId(orderId)
        .orElseThrow(() -> new BadRequestException("Data Tidak Ditemukan!"));
  }

  @Override
  @Transactional
  public TransactionCreate createTransaction(User user, TransactionData data) throws MidtransError {

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
    TransactionCreate transactionCreate = TransactionCreate.builder()
        .orderId(orderId)
        .redirectUrl(response.get("redirect_url").toString())
        .token(response.get("token").toString())
        .build();

    Transaction transaction = Transaction.builder()
        .member(user)
        .orderId(orderId)
        .packageId(data.getPackageId())
        .name(data.getPackageName())
        .paymentUrl(transactionCreate.getRedirectUrl())
        .paymentType(data.getPaymentType())
        .token(transactionCreate.getToken())
        .amount(data.getGrossAmount())
        .type(data.getType())
        .build();

    transactionRespository.save(transaction);

    return transactionCreate;
  }

  @Override
  @Transactional
  public Transaction detailTransaction(String orderId) {
    Transaction transaction = this.findByOrderId(orderId);

    return transaction;
  }

  @Override
  @Transactional
  public Transaction updateTransaction(String orderId, String status, String paymentType) {
    Transaction transaction = this.findByOrderId(orderId);
    transaction.setExpiredAt(Instant.now().plus(Duration.ofMinutes(15)));

    if (status.equals("cancel")) {
      transaction.setStatus(TransactionStatus.CANCELLED);
>>>>>>> cdbef48 ([FIX] add enpoint to update and delete trainer. add sort for transaction)
=======
        return paginationResponse;
>>>>>>> b9483e4 (feat: add update trainer slot and change some endpoint)
=======
>>>>>>> e19aec3 (fix: standardize exception messages to English and improve logging in various services)
    }

    @Override
    public PaginationResponse<TransactionResponse> findAllMyTransaction(User user, TransactionSearchRequest request) {

        Integer page = request.getPage();
        Integer size = request.getSize();

        Specification<Transaction> specification = TransactionSpecification.getMyTransaction(user, request);

<<<<<<< HEAD
        Sort sortBy = Sort.by(Transaction::getCreatedAt).descending();
=======
        String sortByType = request.getSortBy() != null && !request.getSortBy().isBlank() ? request.getSortBy()
                : "createdAt";
        String sortByDirection = request.getDirection() != null && !request.getSortBy().isBlank()
                ? request.getDirection()
                : "desc";

        Sort sortBy = Sort.by(Sort.Direction.fromString(sortByDirection), sortByType);
>>>>>>> b9483e4 (feat: add update trainer slot and change some endpoint)
        PageRequest pageRequest = PageRequest.of(page, size, sortBy);

        Page<Transaction> transactionPage = transactionRespository.findAll(specification, pageRequest);

        List<TransactionResponse> transactionResponse = transactionMapping
                .toListResponses(transactionPage.getContent());

        PaginationMeta paginationMeta = PaginationLinkBuilder.build(page, transactionPage.getTotalPages(), size,
                transactionPage.getTotalElements(),
                p -> ServletUriComponentsBuilder.fromCurrentRequest().replaceQueryParam("page", p).toUriString());

        return PaginationResponse.<TransactionResponse>builder()
                .data(transactionResponse)
                .pagination(paginationMeta)
                .build();
    }

    @Override
    public List<TransactionResponse> findRecentTransaction(String limit) {

        Integer sizeLimit = NumberUtils.toInt(limit, 10);

        List<Transaction> transactions = transactionRespository
                .findAllByOrderByCreatedAtDesc(PageRequest.of(0, sizeLimit > 100 ? 100 : sizeLimit));

        return transactionMapping.toListResponses(transactions);
    }

    @Override
    public Transaction findByOrderId(String orderId) {
        return transactionRespository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException());
    }

    @Override
    @Transactional
    public TransactionCreate createTransaction(User user, TransactionData data) throws MidtransError {

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
        TransactionCreate transactionCreate = TransactionCreate.builder()
                .orderId(orderId)
                .redirectUrl(response.get("redirect_url").toString())
                .token(response.get("token").toString())
                .build();

        Transaction transaction = Transaction.builder()
                .member(user)
                .orderId(orderId)
                .packageId(data.getPackageId())
                .name(data.getPackageName())
                .paymentUrl(transactionCreate.getRedirectUrl())
                .paymentType(data.getPaymentType())
                .token(transactionCreate.getToken())
                .amount(data.getGrossAmount())
                .type(data.getType())
                .build();

        transaction = transactionRespository.save(transaction);

        log.info("Create a payment link for transaction {}", transaction.getId());

        return transactionCreate;
    }

    @Override
    @Transactional
    public Transaction detailTransaction(String orderId) {
        return this.findByOrderId(orderId);
    }

    @Override
    @Transactional
    public Transaction updateTransaction(String orderId, String status, String paymentType) {
        Transaction transaction = this.findByOrderId(orderId);
        transaction.setExpiredAt(Instant.now().plus(Duration.ofMinutes(15)));

        if (status.equals("cancel")) {
            transaction.setStatus(TransactionStatus.CANCELLED);
        }

        if (status.equals("expire")) {
            transaction.setStatus(TransactionStatus.EXPIRED);
        }

        if (status.equals("settlement")) {
            transaction.setPaidAt(Instant.now());
            transaction.setStatus(TransactionStatus.PROCESS);
        }

        log.info("Update transaction {}", transaction.getId());

        return transactionRespository.save(transaction);
    }

    @Override
    @Transactional
    public void refundTransaction(String orderId) {
        Transaction transaction = this.findByOrderId(orderId);

        if (!transaction.getStatus().equals(TransactionStatus.SUCCESS)) {

            log.info("Failed to refund transaction {}", transaction.getId());

            throw new BadRequestException("This transaction cannot be refunded!");
        }

        try {
            Map<String, String> refundDetails = new HashMap<>();
            refundDetails.put("refund_key", orderId);
            refundDetails.put("amount", transaction.getAmount().toString());

            midtransCoreApi.refundTransaction(orderId, refundDetails);
            log.info("Success to refund the transaction {}", transaction.getId());
        } catch (Exception e) {

            log.info("Failed to refund transaction {}", transaction.getId());
            throw new BadRequestException("Failed to refund the transaction!");
        }
    }

    @Override
    public Long countTotalIncomeByType(TransactionType type) {
        return transactionRespository.sumAmountByTypeAndStatus(type, TransactionStatus.SUCCESS);
    }

    @Override
    public Long countTransactionByStatus(TransactionStatus status) {
        return transactionRespository.countByStatus(status);
    }

    @Override
    public Long sumTotalIncomeYearly(int year, ZoneId zone) {

        Instant startOfYear = LocalDate.of(year, 1, 1)
                .atStartOfDay(zone)
                .toInstant();

        Instant endOfYear = LocalDate.of(year + 1, 1, 1)
                .atStartOfDay(zone)
                .toInstant();

        return transactionRespository.sumAmountByStatusAndInRange(TransactionStatus.SUCCESS, startOfYear, endOfYear);
    }

    @Override
    public List<TransactionResponse> getAllTransactionData() {
        return transactionMapping.toListResponses(transactionRespository.findAll());
    }
}
