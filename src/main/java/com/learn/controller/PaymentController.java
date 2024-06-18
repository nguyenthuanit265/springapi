package com.learn.controller;

import com.learn.model.request.CashInRequest;
import com.learn.model.request.SearchBillsByProviderRequest;
import com.learn.security.JwtAuthFilter;
import com.learn.service.impl.PaymentService;
import com.learn.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payment")
public class PaymentController {
    private final PaymentService paymentService;
    private final UserService userService;

    public PaymentController(PaymentService paymentService, UserService userService) {
        this.paymentService = paymentService;
        this.userService = userService;
    }

    @PostMapping("/cashIn")
    public ResponseEntity<?> cashIn(@RequestBody CashInRequest cashInRequest) {
        return paymentService.cashIn(JwtAuthFilter.accountPayment, cashInRequest.getAmount());
    }

    @GetMapping("/listBills")
    public ResponseEntity<?> listBills() {
        return paymentService.listBills(JwtAuthFilter.accountPayment);
    }

    @GetMapping("/listPayments")
    public ResponseEntity<?> listPayments() {
        return paymentService.listPayments(JwtAuthFilter.accountPayment);
    }

    @PostMapping("/searchBillsByProvider")
    public ResponseEntity<?> searchBillsByProvider(@RequestBody SearchBillsByProviderRequest request) {
        return paymentService.searchBillsByProvider(JwtAuthFilter.accountPayment, request.getProvider());
    }
}
