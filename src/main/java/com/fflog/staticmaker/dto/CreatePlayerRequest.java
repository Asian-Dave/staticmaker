package com.fflog.staticmaker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreatePlayerRequest {

    @NotBlank(message = "First name is required")
    @Size(max = 15, message = "First name must not exceed 15 characters")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "First name must contain only letters")
    private String firstname;

    @NotBlank(message = "Surname is required")
    @Size(max = 15, message = "Surname must not exceed 15 characters")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Surname must contain only letters")
    private String surname;

    @NotBlank(message = "Role is required")
    @Pattern(regexp = "^(Tank|Healer|DPS)$", message = "Role must be Tank, Healer, or DPS")
    private String role;

    @NotBlank(message = "Datacenter is required")
    private String datacenter;

    @NotBlank(message = "Server is required")
    private String server;

    @NotBlank(message = "Static name is required")
    private String staticName;
}
