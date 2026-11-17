package com.gymmick.smartgym.dto.dashboard;

import com.gymmick.smartgym.validation.annotation.IdPhoneNumber;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrainerCreateRequest {

  @NotBlank(message = "The name field is required.")
  private String name;

  @NotBlank(message = "The email field is required.")
  @Email(message = "The email field must be a valid email address.")
  private String email;

  @NotBlank(message = "The phone number field is required.")
  @IdPhoneNumber
  private String phoneNumber;

  @NotBlank(message = "The password field is required.")
  @Size(min = 8, message = "The password field must be at least 8 characters.")
  private String password;

}
