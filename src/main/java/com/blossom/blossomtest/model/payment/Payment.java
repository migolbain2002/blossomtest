package com.blossom.blossomtest.model.payment;

import com.blossom.blossomtest.model.order.Order;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "payment_type")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "payment_type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CardPayment.class, name = "CARD"),
        @JsonSubTypes.Type(value = NequiPayment.class, name = "NEQUI")
})
public abstract class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private LocalDateTime paidAt;

    @OneToOne
    @JoinColumn(name = "order_id", unique = true)
    private Order order;

}
