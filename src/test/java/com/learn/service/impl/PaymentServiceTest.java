package com.learn.service.impl;

import com.learn.enums.BillState;
import com.learn.enums.BillType;
import com.learn.enums.PaymentState;
import com.learn.model.entity.AccountPaymentEntity;
import com.learn.model.entity.Bill;
import com.learn.model.entity.Payment;
import com.learn.utils.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;
    private AccountPaymentEntity accountPayment;
    private List<Bill> bills;

    @BeforeEach
    void setUp() {
        accountPayment = new AccountPaymentEntity();
        accountPayment.setBalance(BigDecimal.valueOf(1000000));

        bills = PaymentService.initBills();
        accountPayment.setBills(bills);
        accountPayment.setPayments(new ArrayList<>());
    }

    @Test
    void testCashInPositiveAmount() {
        BigDecimal amount = BigDecimal.valueOf(50000);
        ResponseEntity<?> response = paymentService.cashIn(accountPayment, amount);
        assertEquals(ResponseEntity.ok().body(accountPayment), response);
        assertEquals(BigDecimal.valueOf(1050000), accountPayment.getBalance());
    }

    @Test
    void testCashInNegativeAmount() {
        BigDecimal amount = BigDecimal.valueOf(-50000);
        ResponseEntity<?> response = paymentService.cashIn(accountPayment, amount);
        assertEquals(ResponseEntity.badRequest().body(null), response);
    }

    @Test
    void testListBills() {
        ResponseEntity<?> response = paymentService.listBills(accountPayment);
        assertEquals(ResponseEntity.ok().body(bills), response);
    }

    @Test
    void testPayBillInsufficientFunds() {
        accountPayment.setBalance(BigDecimal.valueOf(100));
        ResponseEntity<?> response = paymentService.payBill(accountPayment);
        assertTrue(response.getBody().toString().contains("Not enough fund to proceed with payment"));
        assertEquals(ResponseEntity.badRequest().body("Sorry! Not enough fund to proceed with payment"), response);
    }

    @Test
    void testPayBill() {
        accountPayment.setBalance(BigDecimal.valueOf(2000000));
        ResponseEntity<?> response = paymentService.payBill(accountPayment);
        assertEquals(ResponseEntity.ok().body(accountPayment), response);
        for (Bill bill : accountPayment.getBills()) {
            assertEquals(BillState.PAID, bill.getBillState());
        }
    }

    @Test
    void testListDueBills() {
        paymentService.listDueBills(accountPayment);
        // Assuming log.info prints to the console, manually verify the output.
    }

    @Test
    void testSchedulePayment() {
        paymentService.schedulePayment(accountPayment, 1, "2023-12-25");
        Payment scheduledPayment = accountPayment.getPayments().get(0);
        assertEquals(PaymentState.PENDING, scheduledPayment.getPaymentState());
        assertEquals(DateUtils.convertStringToDate("2023-12-25"), scheduledPayment.getScheduledDate());
    }

    @Test
    void testListPayments() {
        paymentService.payBill(accountPayment); // First pay the bills to generate payments
        ResponseEntity<?> response = paymentService.listPayments(accountPayment);
        assertEquals(ResponseEntity.ok().body(accountPayment.getPayments()), response);
    }

    @Test
    void testSearchBillsByProvider() {
        ResponseEntity<?> response = paymentService.searchBillsByProvider(accountPayment, "EVN HCM");
        List<Bill> result = (List<Bill>) response.getBody();
        assertEquals(1, result.size());
        assertEquals("EVN HCM", result.get(0).getProvider());
    }

    @Test
    void testFindBillById() {
        Bill bill = paymentService.findBillById(accountPayment, 1);
        assertNotNull(bill);
        assertEquals(1, bill.getBillId());
    }

    @Test
    void testInitBills() {
        List<Bill> bills = PaymentService.initBills();
        assertEquals(3, bills.size());
        assertEquals(BillType.ELECTRIC, bills.get(0).getBillType());
        assertEquals(BillType.WATER, bills.get(1).getBillType());
        assertEquals(BillType.INTERNET, bills.get(2).getBillType());
    }

    @Test
    void testCashInZeroAmount() {
        BigDecimal amount = BigDecimal.ZERO;
        ResponseEntity<?> response = paymentService.cashIn(accountPayment, amount);
        assertEquals(ResponseEntity.ok().body(accountPayment), response);
        assertEquals(BigDecimal.valueOf(1000000), accountPayment.getBalance());
    }

    @Test
    void testPayBillWithDuplicatedBills() {
        accountPayment.getBills().add(accountPayment.getBills().get(0)); // Duplicate first bill
        ResponseEntity<?> response = paymentService.payBill(accountPayment);
        assertTrue(response.getBody().toString().contains("Bill 1 is duplicated"));
        assertEquals(ResponseEntity.badRequest().body("Sorry! Bill 1 is duplicated"), response);
    }

    @Test
    void testPayBillWithPaidBills() {
        accountPayment.getBills().get(0).setBillState(BillState.PAID);
        ResponseEntity<?> response = paymentService.payBill(accountPayment);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
    }

    @Test
    void testPaySingleBill() {
        accountPayment.getBills().remove(1);
        accountPayment.getBills().remove(1);
        ResponseEntity<?> response = paymentService.payBill(accountPayment);
        assertEquals(ResponseEntity.ok().body(accountPayment), response);
        assertEquals(BillState.PAID, accountPayment.getBills().get(0).getBillState());
        assertEquals(1, accountPayment.getPayments().size());
    }

    @Test
    void testListDueBillsWithNoBills() {
        accountPayment.setBills(new ArrayList<>());
        paymentService.listDueBills(accountPayment);
        // Assuming log.info prints to the console, manually verify the output.
    }

    @Test
    void testSchedulePaymentWithInsufficientFunds() {
        accountPayment.setBalance(BigDecimal.valueOf(100));
        paymentService.schedulePayment(accountPayment, 1, "2023-12-25");
        assertTrue(accountPayment.getPayments().isEmpty());
    }

    @Test
    void testSchedulePaymentForPaidBill() {
        accountPayment.getBills().get(0).setBillState(BillState.PAID);
        paymentService.schedulePayment(accountPayment, 1, "2023-12-25");
        assertTrue(accountPayment.getPayments().isEmpty());
    }

    @Test
    void testListPaymentsWithNoPayments() {
        ResponseEntity<?> response = paymentService.listPayments(accountPayment);
        assertEquals(ResponseEntity.ok().body(new ArrayList<>()), response);
    }

    @Test
    void testSearchBillsByNonExistingProvider() {
        ResponseEntity<?> response = paymentService.searchBillsByProvider(accountPayment, "NON_EXISTING_PROVIDER");
        List<Bill> result = (List<Bill>) response.getBody();
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindBillByIdWithNegativeId() {
        Bill bill = paymentService.findBillById(accountPayment, -1);
        assertNull(bill);
    }

    @Test
    void testFindBillByIdWithNonExistingId() {
        Bill bill = paymentService.findBillById(accountPayment, 999);
        assertNull(bill);
    }

    @Test
    void testGetBillNotPaid() {
        List<Bill> notPaidBills = paymentService.getBillNotPaid(accountPayment);
        assertEquals(3, notPaidBills.size());
    }

    @Test
    void testGetBillNotPaidWithAllBillsPaid() {
        for (Bill bill : accountPayment.getBills()) {
            bill.setBillState(BillState.PAID);
        }
        List<Bill> notPaidBills = paymentService.getBillNotPaid(accountPayment);
        assertTrue(notPaidBills.isEmpty());
    }

    @Test
    void testGetBillNotPaidWithSomeBillsPaid() {
        accountPayment.getBills().get(0).setBillState(BillState.PAID);
        List<Bill> notPaidBills = paymentService.getBillNotPaid(accountPayment);
        assertEquals(2, notPaidBills.size());
    }

    @Test
    void testListPaymentsAfterPayingBills() {
        accountPayment.setBalance(BigDecimal.valueOf(2000000));
        paymentService.payBill(accountPayment);
        ResponseEntity<?> response = paymentService.listPayments(accountPayment);
        List<Payment> payments = (List<Payment>) response.getBody();
        assertEquals(3, payments.size());
    }
}
