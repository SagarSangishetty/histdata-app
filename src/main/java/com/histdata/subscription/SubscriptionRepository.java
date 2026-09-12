package com.histdata.subscription;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    boolean existsByUserUsernameIgnoreCaseAndSegmentAndActiveTrueAndValidFromLessThanEqualAndValidUntilGreaterThanEqual(
            String username, String segment, LocalDate from, LocalDate until);
}

