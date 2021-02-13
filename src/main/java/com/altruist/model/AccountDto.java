package com.altruist.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class AccountDto {
  @NotBlank
  public String username;
  @NotBlank
  public String email;
  public String name;
  public String street;
  public String city;
  public String state;
  public Integer zipcode;
}
