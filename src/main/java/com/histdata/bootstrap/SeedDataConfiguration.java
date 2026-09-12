package com.histdata.bootstrap;

import com.histdata.subscription.Subscription;
import com.histdata.subscription.SubscriptionRepository;
import com.histdata.user.AppUser;
import com.histdata.user.AppUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
@Profile("local")
public class SeedDataConfiguration implements ApplicationRunner {
    private final AppUserRepository users;
    private final SubscriptionRepository subscriptions;
    private final PasswordEncoder encoder;
    private final String clientPassword;
    private final String adminPassword;

    public SeedDataConfiguration(AppUserRepository users,
                                 SubscriptionRepository subscriptions,
                                 PasswordEncoder encoder,
                                 @Value("${seed.client-password}") String clientPassword,
                                 @Value("${seed.admin-password}") String adminPassword) {
        this.users = users;
        this.subscriptions = subscriptions;
        this.encoder = encoder;
        this.clientPassword = clientPassword;
        this.adminPassword = adminPassword;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        AppUser client = users.findByUsernameIgnoreCase("client01")
                .orElseGet(() -> users.save(new AppUser("client01", encoder.encode(clientPassword), "CLIENT")));
        users.findByUsernameIgnoreCase("admin01")
                .orElseGet(() -> users.save(new AppUser("admin01", encoder.encode(adminPassword), "ADMIN")));
        if (subscriptions.count() == 0) {
            LocalDate start = LocalDate.of(2020, 1, 1);
            LocalDate end = LocalDate.of(2035, 12, 31);
            subscriptions.save(new Subscription(client, "CM", start, end));
            subscriptions.save(new Subscription(client, "CD", start, end));
            subscriptions.save(new Subscription(client, "FO", start, end));
        }
    }
}
