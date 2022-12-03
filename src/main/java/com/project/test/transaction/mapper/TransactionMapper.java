package com.project.test.transaction.mapper;

import com.project.test.transaction.dto.TransactionDto;
import com.project.test.transaction.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TransactionMapper {
    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);
    @Mapping(target = "transactionType", expression = "java(transaction.getTransactionType().get())") //enum to string value
    @Mapping(target = "message", expression = "java(transaction.getMessage().get())")
    TransactionDto toDto(Transaction transaction);

}
