package com.it355pz2.controllers;

import com.it355pz2.entity.PaymentType;
import com.it355pz2.repository.PaymentTypeRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/payment-types")
public class PaymentTypeController {
    private PaymentTypeRepository paymentTypeRepository;

    @GetMapping("/")
    public ResponseEntity<List<PaymentType>> getPaymentTypes() {
        List<PaymentType> allPaymentTypes = paymentTypeRepository.findAll();
        List<PaymentType> activePaymentTypes = allPaymentTypes.stream()
                .filter(paymentType -> !paymentType.isDeleted())
                .toList();
        return ResponseEntity.ok(activePaymentTypes);
    }
}
