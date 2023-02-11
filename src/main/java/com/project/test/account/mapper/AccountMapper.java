package com.project.test.account.mapper;

import com.project.test.account.dto.AccountDto;
import com.project.test.account.model.Account;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Mapper
public interface AccountMapper {
    AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

    AccountDto toDto(Account account);
}
