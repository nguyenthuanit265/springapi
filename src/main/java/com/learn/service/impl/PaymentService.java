package com.learn.service.impl;

import com.learn.enums.BillState;
import com.learn.enums.BillType;
import com.learn.enums.PaymentState;
import com.learn.model.entity.AccountPaymentEntity;
import com.learn.model.entity.Bill;
import com.learn.model.entity.Payment;
import com.learn.utils.DateUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class PaymentService {

    @PostConstruct
    public void init() {
//        User user = new User();
//        user.setId(1L);
//        user.setName("user1");
//        user.setEmail("user1@gmail.com");
//
//        this.user = user;
//
//        // Define bills
//        List<Bill> bills = initBills();
//        this.setBills(bills);
    }

    public static List<Bill> initBills() {
        Bill bill = new Bill();
        bill.setBillId(1);
        bill.setBillType(BillType.ELECTRIC);
        bill.setAmount(200000);
        bill.setDueDate(LocalDate.of(2020, 10, 25));
        bill.setBillState(BillState.NOT_PAID);
        bill.setProvider("EVN HCM");
        bill.setPaid(false);

        Bill bill2 = new Bill();
        bill2.setBillId(2);
        bill2.setBillType(BillType.WATER);
        bill2.setAmount(175000);
        bill2.setDueDate(LocalDate.of(2020, 10, 30));
        bill2.setBillState(BillState.NOT_PAID);
        bill2.setProvider("SAVACO HCM");
        bill2.setPaid(false);

        Bill bill3 = new Bill();
        bill3.setBillId(3);
        bill3.setBillType(BillType.INTERNET);
        bill3.setAmount(800000);
        bill3.setDueDate(LocalDate.of(2020, 11, 30));
        bill3.setBillState(BillState.NOT_PAID);
        bill3.setProvider("VNPT");
        bill3.setPaid(false);

        return new ArrayList<>(Arrays.asList(bill, bill2, bill3));
    }

    public ResponseEntity<?> cashIn(AccountPaymentEntity accountPayment, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            log.info("Amount is negative number!!!");
            return ResponseEntity.badRequest().body(null);
        }

        accountPayment.setBalance(accountPayment.getBalance().add(amount));
        log.info("Your available balance: {}", accountPayment.getBalance());
        return ResponseEntity.ok().body(accountPayment);
    }

    public ResponseEntity<?> listBills(AccountPaymentEntity accountPayment) {
        log.info("Bill No. Type Amount Due Date State PROVIDER");
        accountPayment.getBills().forEach(bill -> {
            log.info("{}. {} {} {} {} {}", bill.getBillId(), bill.getBillType(), bill.getAmount(), bill.getDueDate(), bill.getBillState().toString().toUpperCase(), bill.getProvider());
        });
        return ResponseEntity.ok().body(accountPayment.getBills());
    }

    public ResponseEntity<?> payBill(AccountPaymentEntity accountPayment) {
        // Validate bill
        List<Integer> billIds = accountPayment.getBills().stream().map(Bill::getBillId).toList();
        BigDecimal totalBillAmount = BigDecimal.ZERO;
        Map<Integer, Integer> mapBillDup = new ConcurrentHashMap<>();
        for (int billId : billIds) {
            if (mapBillDup.containsKey(billId) && mapBillDup.get(billId) >= 1) {
                String message = String.format("Sorry! Bill %s is duplicated", billId);
                log.info(message);
                return ResponseEntity.badRequest().body(message);
            } else {
                mapBillDup.put(billId, 1);
            }

            Bill bill = findBillById(accountPayment, billId);
            if (bill == null) {
                String message = String.format("Sorry! Not found a bill with such id: %s", billId);
                log.info(message);
                return ResponseEntity.badRequest().body(message);
            }

            if (bill.getBillState().equals(BillState.PAID)) {
                String message = String.format("Sorry! Existed bill %s is paid%n", bill.getBillId());
                return ResponseEntity.badRequest().body(message);
            }

            if (bill.getBillState().equals(BillState.NOT_PAID)) {
                totalBillAmount = totalBillAmount.add(BigDecimal.valueOf(bill.getAmount()));
            }
        }

        // Check fund
        if (accountPayment.getBalance().compareTo(totalBillAmount) < 0) {
            String message = "Sorry! Not enough fund to proceed with payment";
            log.info(message);
            return ResponseEntity.badRequest().body(message);
        }

        for (int billId : billIds) {
            Bill bill = findBillById(accountPayment, billId);
            if (accountPayment.getBalance().compareTo(BigDecimal.valueOf(bill.getAmount())) > 0) {
                Payment payment = new Payment();
                payment.setBill(bill);
                payment.setPaymentDate(LocalDate.now());
                payment.setAmount(bill.getAmount());
                payment.setPaymentState(PaymentState.PROCESSED);
                accountPayment.getPayments().add(payment);

                accountPayment.getBalance().subtract(BigDecimal.valueOf(bill.getAmount()));
                bill.setBillState(BillState.PAID);
                log.info("Payment has been completed for Bill with id {}", billId);
                log.info("Your current balance is: {}", accountPayment.getBalance().doubleValue());
            }
        }

        return ResponseEntity.ok().body(accountPayment);
    }

    public void listDueBills(AccountPaymentEntity accountPayment) {
        log.info("Bill No. Type Amount Due Date State PROVIDER");
        // Sort
        List<Bill> billsNotPaid = getBillNotPaid(accountPayment);
        Collections.sort(billsNotPaid, Comparator.comparing(Bill::getDueDate));

        for (Bill bill : billsNotPaid) {
            if (bill.getBillState().equals(BillState.NOT_PAID)) {
                log.info("{}. {} {} {} {} {}", bill.getBillId(), bill.getBillType(), bill.getAmount(), bill.getDueDate(), bill.getBillState(), bill.getProvider());
            }
        }
    }

    public void schedulePayment(AccountPaymentEntity accountPayment, int billId, String scheduledDate) {
        Bill bill = findBillById(accountPayment, billId);
        if (bill != null && bill.getBillState().equals(BillState.NOT_PAID) && accountPayment.getBalance().compareTo(BigDecimal.valueOf(bill.getAmount())) > 0) {
            Payment payment = new Payment();
            payment.setBill(bill);
            payment.setPaymentDate(bill.getDueDate());
            payment.setScheduledDate(DateUtils.convertStringToDate(scheduledDate));
            payment.setAmount(bill.getAmount());
            payment.setPaymentState(PaymentState.PENDING);
            accountPayment.getPayments().add(payment);

            log.info("Payment for bill id {} is scheduled on {}", billId, scheduledDate);
        } else {
            log.info("Sorry! Unable to schedule payment for Bill with id {}", billId);
        }
    }

    public ResponseEntity<?> listPayments(AccountPaymentEntity accountPayment) {
        log.info("No. Amount Payment Date State Bill Id");
        for (int i = 0; i < accountPayment.getPayments().size(); i++) {
            Payment payment = accountPayment.getPayments().get(i);
            String billId = payment.getBill() != null ? String.valueOf(payment.getBill().getBillId()) : "";
            log.info("{}. {} {} {} {}", i + 1, payment.getAmount(), payment.getPaymentDate(), payment.getPaymentState(), billId);
        }

        return ResponseEntity.ok().body(accountPayment.getPayments());
    }

    public ResponseEntity<?> searchBillsByProvider(AccountPaymentEntity accountPayment, String provider) {
        log.info("Bill No. Type Amount Due Date State PROVIDER");
        List<Bill> bills = new ArrayList<>();
        for (Bill bill : accountPayment.getBills()) {
            if (bill.getProvider().equals(provider)) {
                log.info("{}. {} {} {} {} {}", bill.getBillId(), bill.getBillType(), bill.getAmount(), bill.getDueDate(), bill.getBillState(), bill.getProvider());
                bills.add(bill);
            }
        }
        return ResponseEntity.ok().body(bills);
    }

    public Bill findBillById(AccountPaymentEntity accountPayment, int billId) {
        if (billId < 0) {
            return null;
        }

        for (Bill bill : accountPayment.getBills()) {
            if (bill.getBillId() == billId) {
                return bill;
            }
        }

        return null;
    }

    public List<Bill> getBillNotPaid(AccountPaymentEntity accountPayment) {
        List<Bill> billsNotPaid = new ArrayList<>();
        for (Bill bill : accountPayment.getBills()) {
            if (bill.getBillState().equals(BillState.NOT_PAID)) {
                billsNotPaid.add(bill);
            }
        }

        return billsNotPaid;
    }
}
