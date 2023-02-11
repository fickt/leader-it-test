package com.project.test.cashorder.mapper;

import com.project.test.cashorder.dto.CashOrderDto;
import com.project.test.cashorder.dto.NewCashOrderRequest;
import com.project.test.cashorder.model.CashOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Mapper
public interface CashOrderMapper {
    CashOrderMapper INSTANCE = Mappers.getMapper(CashOrderMapper.class);
    @Mapping(target = "orderType", expression = "java(cashOrder.getOrderType().getValue())") //enum to string value
    @Mapping(target = "message", expression = "java(cashOrder.getMessage().getValue())")
    CashOrderDto toDto(CashOrder cashOrder);
}
