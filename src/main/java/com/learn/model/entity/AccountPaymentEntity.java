package com.learn.model.entity;

import com.learn.model.dto.UserDto;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class AccountPaymentEntity {
    private String accountId;
    private UserDto user;
    private BigDecimal balance;
    private List<Bill> bills;
    private List<Payment> payments;

    public AccountPaymentEntity() {
        this.balance = BigDecimal.ZERO;
        this.bills = new ArrayList<>();
        this.payments = new ArrayList<>();
    }

    public AccountPaymentEntity(UserDto user) {
        this.user = user;
        this.balance = BigDecimal.ZERO;
        this.bills = new ArrayList<>();
        this.payments = new ArrayList<>();
    }
}
