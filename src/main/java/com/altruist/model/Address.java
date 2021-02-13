package com.altruist.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.*;

@Data
public class Address {

  @JsonIgnore
  private UUID uuid;
  @NotBlank
  private String name;
  @NotBlank
  private String street;
  @NotBlank
  private String city;
  @NotNull
  private State state;
  @NotNull
  private Integer zipcode;

}
