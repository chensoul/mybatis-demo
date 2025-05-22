package io.github.rosestack.myapp.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDTO {
    Long id;

    @NotNull
    @Size(max = 255)
    @UserNameUnique
    String name;
}
