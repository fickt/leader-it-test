package com.project.test.cashorder.repository;

import com.project.test.cashorder.model.CashOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CashOrderRepository extends JpaRepository<CashOrder, Long> {
    List<CashOrder> findAllByAccountId(Long accountId);
}
