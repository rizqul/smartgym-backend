package com.gymmick.smartgym.entity;

import java.time.Instant;
import java.util.*;

import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gymmick.smartgym.entity.enums.*;
import com.gymmick.smartgym.entity.shared.TimeStamp;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
@SQLRestriction("deleted_at is null")
@EntityListeners(AuditingEntityListener.class)
public class User extends TimeStamp implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = true)
    private String profileUrl;

    @Column(nullable = true)
    private String phoneNumber;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Builder.Default
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.MEMBER;

    private MembershipStatus membershipStatus;

    private Instant membershipExpiredAt;

    private Instant deletedAt;

    @JsonIgnore
    @Builder.Default
    private Boolean accountNonExpired = true;

    @JsonIgnore
    @Builder.Default
    private Boolean accountNonLocked = true;

    @JsonIgnore
    @Builder.Default
    private Boolean credentialsNonExpired = true;

    @JsonIgnore
    @Builder.Default
    private Boolean enabled = true;

    @JsonIgnore
    public String getUsername() {
        return this.email;
    }

    public MembershipStatus getMembershipStatus() {
        return membershipStatus == MembershipStatus.ACTIVE
                && (membershipExpiredAt == null || membershipExpiredAt.isAfter(Instant.now()))
                        ? MembershipStatus.ACTIVE
                        : MembershipStatus.NON_ACTIVE;
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
    }
}
