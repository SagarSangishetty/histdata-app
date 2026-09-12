package com.histdata.subscription;

import com.histdata.user.AppUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "SUBSCRIPTIONS", uniqueConstraints =
        @UniqueConstraint(name = "UK_SUBSCRIPTION_USER_SEGMENT", columnNames = {"USER_ID", "SEGMENT"}))
@Getter
@Setter
@NoArgsConstructor
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subscription_seq")
    @SequenceGenerator(name = "subscription_seq", sequenceName = "SUBSCRIPTION_SEQ", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ID", nullable = false)
    private AppUser user;

    @Column(nullable = false, length = 10)
    private String segment;

    @Column(nullable = false)
    private LocalDate validFrom;

    @Column(nullable = false)
    private LocalDate validUntil;

    @Column(nullable = false)
    private boolean active = true;

    public Subscription(AppUser user, String segment, LocalDate validFrom, LocalDate validUntil) {
        this.user = user;
        this.segment = segment;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
    }
}

