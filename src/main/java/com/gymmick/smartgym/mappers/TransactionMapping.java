package com.gymmick.smartgym.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.gymmick.smartgym.dto.common.TransactionResponse;
import com.gymmick.smartgym.entity.Transaction;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TransactionMapping {

  TransactionResponse toResponse(Transaction transaction);

  List<TransactionResponse> toListResponses(List<Transaction> transactions);
}
