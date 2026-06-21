package com.moat.company;

import com.moat.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Spółka na liście użytkownika. Należy do jednego użytkownika (owner).
 */
@Entity
@Table(
        name = "companies",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_company_user_ticker",
                columnNames = {"user_id", "ticker"}))
@Getter
@Setter
@NoArgsConstructor
public class Company {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    @Column(nullable = false)
    private String name;

    /** Ticker giełdowy; nullable dla spółek wprowadzanych ręcznie. */
    @Column
    private String ticker;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
}
