package com.fflog.staticmaker.dto;

import com.fflog.staticmaker.model.RegionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateRaidGroupRequest {

    @NotBlank(message = "Group name is required")
    @Size(max = 50, message = "Group name must not exceed 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Group name must contain only letters and numbers")
    private String groupName;

    @NotNull(message = "Region is required")
    private RegionType region;
}
