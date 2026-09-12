package com.histdata.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EntitlementService {
    private static final Set<String> SUPPORTED_SEGMENTS = Set.of("CM", "CD", "FO");
    private final SubscriptionRepository repository;

    public void requireEntitlement(String username, String segment, LocalDate tradeDate) {
        String normalized = segment.toUpperCase();
        if (!SUPPORTED_SEGMENTS.contains(normalized)) {
            throw new IllegalArgumentException("Unsupported segment: " + segment);
        }
        boolean entitled = repository
                .existsByUserUsernameIgnoreCaseAndSegmentAndActiveTrueAndValidFromLessThanEqualAndValidUntilGreaterThanEqual(
                        username, normalized, tradeDate, tradeDate);
        if (!entitled) {
            throw new AccessDeniedException("No active subscription for " + normalized);
        }
    }
}
