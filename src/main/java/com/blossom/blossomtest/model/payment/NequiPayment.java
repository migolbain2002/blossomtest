package com.blossom.blossomtest.model.payment;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("NEQUI")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NequiPayment extends Payment {
    private String number;
}
