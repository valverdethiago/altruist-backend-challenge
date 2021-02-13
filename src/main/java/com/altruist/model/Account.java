package com.altruist.model;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Data
public class Account {

  @JsonIgnore
  private UUID uuid;
  @JsonIgnore
  private UUID addressUuid;
  @NotBlank
  private String username;
  @NotBlank
  private String email;
  @Valid
  private Address address;
}
