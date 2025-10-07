package com.blossom.blossomtest.model.payment;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("CARD")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardPayment extends Payment {
    private String cardHolderMasked;
    private String last4;
}
