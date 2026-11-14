package com.gymmick.smartgym.seeder;

import java.util.*;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.gymmick.smartgym.entity.User;
import com.gymmick.smartgym.entity.enums.UserRole;
import com.gymmick.smartgym.repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;

@Order(2)
@Component
@RequiredArgsConstructor
public class UserSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seeder.enabled:true}")
    private boolean seederEnabled;

    @Value("${app.seeder.user-count:50}")
    private int userCount;

    @Override
    @Transactional
    public void run(String... args) {
        if (!seederEnabled) {
            return;
        }

        long existingCount = userRepository.count();
        if (existingCount >= userCount) {
            return;
        }

        int countToGenerate = (int) (userCount - existingCount);

        Faker faker = new Faker(Locale.of("id", "ID"));
        List<User> users = new ArrayList<>();
        Set<String> generatedEmails = new HashSet<>();

        String encodedDefaultPassword = passwordEncoder.encode("password");

        for (int i = 0; i < countToGenerate; i++) {
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            String fullName = firstName + " " + lastName;

            String emailPrefix = (firstName + "." + lastName + faker.number().digits(4))
                    .toLowerCase()
                    .replaceAll("[^a-z0-9.]", "");
            String email = emailPrefix + "@example.com";

            while (generatedEmails.contains(email) || userRepository.existsByEmail(email)) {
                emailPrefix = (firstName + "." + lastName + faker.number().digits(6))
                        .toLowerCase()
                        .replaceAll("[^a-z0-9.]", "");
                email = emailPrefix + "@example.com";
            }

            generatedEmails.add(email);

            String emailHash = DigestUtils.sha256Hex(email.trim().toLowerCase());
            String profileUrl = String.format("https://gravatar.com/avatar/%s", emailHash);

            UserRole role = (i % 10 == 0) ? UserRole.STAFF : UserRole.MEMBER;

            User user = User.builder()
                    .name(fullName)
                    .email(email)
                    .password(encodedDefaultPassword)
                    .profileUrl(profileUrl)
                    .role(role)
                    .accountNonExpired(true)
                    .accountNonLocked(true)
                    .credentialsNonExpired(true)
                    .enabled(true)
                    .build();

            users.add(user);
        }

        userRepository.saveAll(users);
    }
}
