
package com.gymmick.smartgym.services.membership.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gymmick.smartgym.dto.membership.*;
import com.gymmick.smartgym.entity.MembershipPackage;
import com.gymmick.smartgym.entity.enums.MembershipType;
import com.gymmick.smartgym.exceptions.ResourceNotFoundException;
import com.gymmick.smartgym.mappers.MembershipPackageMapping;
import com.gymmick.smartgym.repositories.*;
import com.gymmick.smartgym.services.transaction.TransactionService;

@ExtendWith(MockitoExtension.class)
class MembershipServiceImplTest {

  @Mock
  private MembershipRepository membershipRepository;
  @Mock
  private MembershipPackageRepository membershipPackageRepository;
  @Mock
  private TransactionRespository transactionRespository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private TransactionService transactionService;
  @Mock
  private MembershipPackageMapping membershipPackageMapping;

  @InjectMocks
  private MembershipServiceImpl membershipService;

  private UUID packageId;
  private MembershipPackage membershipPackage;

  @BeforeEach
  void setUp() {
    packageId = UUID.randomUUID();
    membershipPackage = mock(MembershipPackage.class);
  }

  @Test
  void countMembershipPackage_shouldReturnRepositoryCount() {
    when(membershipPackageRepository.countByDeletedAtIsNull())
        .thenReturn(5L);

    Long result = membershipService.countMembershipPackage();

    assertEquals(5L, result);
    verify(membershipPackageRepository).countByDeletedAtIsNull();
  }

  @Test
  void createPackage_shouldSavePackage() {
    MembershipPackageCreateRequest request = mock(MembershipPackageCreateRequest.class);

    when(request.getType()).thenReturn(MembershipType.MONTHLY);
    when(request.getName()).thenReturn("Monthly Package");
    when(request.getDescription()).thenReturn("Monthly gym access");
    when(request.getDuration()).thenReturn(30);
    when(request.getPrice()).thenReturn(250000L);

    membershipService.createPackage(request);

    verify(membershipPackageRepository).save(
        argThat(saved -> saved.getType() == MembershipType.MONTHLY
            && saved.getName().equals("Monthly Package")
            && saved.getDuration().equals(30)
            && saved.getPrice().equals(250000L)));
  }

  @Test
  void updatePackage_shouldUpdateExistingPackage() {
    MembershipPackageUpdateRequest request = mock(MembershipPackageUpdateRequest.class);

    when(membershipPackageRepository.findById(packageId))
        .thenReturn(Optional.of(membershipPackage));

    when(request.getType()).thenReturn(MembershipType.MONTHLY);
    when(request.getName()).thenReturn("Updated Package");
    when(request.getDescription()).thenReturn("Updated description");
    when(request.getDuration()).thenReturn(60);

    membershipService.updatePackage(packageId.toString(), request);

    verify(membershipPackage).setType(MembershipType.MONTHLY);
    verify(membershipPackage).setName("Updated Package");
    verify(membershipPackage).setDescription("Updated description");
    verify(membershipPackage).setDuration(60);

    verify(membershipPackageRepository).save(membershipPackage);
  }

  @Test
  void updatePackage_shouldThrowException_whenPackageDoesNotExist() {
    MembershipPackageUpdateRequest request = mock(MembershipPackageUpdateRequest.class);

    when(membershipPackageRepository.findById(packageId))
        .thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> membershipService.updatePackage(packageId.toString(), request));

    verify(membershipPackageRepository, never()).save(any());
  }

  @Test
  void deletePackage_shouldSoftDeletePackage() {
    when(membershipPackageRepository.findById(packageId))
        .thenReturn(Optional.of(membershipPackage));

    membershipService.deletePackage(packageId.toString());

    verify(membershipPackage).setDeletedAt(any(Instant.class));
    verify(membershipPackageRepository).save(membershipPackage);
  }

  @Test
  void findById_shouldReturnPackage_whenPackageExists() {
    when(membershipPackageRepository.findById(packageId))
        .thenReturn(Optional.of(membershipPackage));

    MembershipPackage result = membershipService.findById(packageId.toString());

    assertSame(membershipPackage, result);
  }

  @Test
  void findById_shouldThrowException_whenPackageDoesNotExist() {
    when(membershipPackageRepository.findById(packageId))
        .thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> membershipService.findById(packageId.toString()));
  }
}
