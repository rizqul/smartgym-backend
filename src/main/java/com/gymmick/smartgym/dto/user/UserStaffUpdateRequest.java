package com.gymmick.smartgym.dto.user;

import com.gymmick.smartgym.validation.annotation.IdPhoneNumber;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserStaffUpdateRequest {

  private String name;

  @Email(message = "The email field must be a valid email address.")
  private String email;

  @IdPhoneNumber
  private String phoneNumber;

  @Size(min = 8, message = "The password field must be at least 8 characters.")
  private String password;

  private String profileUrl;

}
