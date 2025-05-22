package io.github.rosestack.myapp.rest;

import io.github.rosestack.myapp.model.UserDTO;
import io.github.rosestack.myapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
class UserResource {
    private final UserService userService;

    @Operation(
            parameters = {
                @Parameter(name = "page", in = ParameterIn.QUERY, schema = @Schema(implementation = Integer.class)),
                @Parameter(name = "size", in = ParameterIn.QUERY, schema = @Schema(implementation = Integer.class)),
                @Parameter(name = "sort", in = ParameterIn.QUERY, schema = @Schema(implementation = String.class))
            })
    @GetMapping
    public Page<UserDTO> getAllUsers(
            @RequestParam(name = "filter", required = false) final String filter,
            @Parameter(hidden = true) @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable) {
        return userService.findAllUsers(filter, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findUserById(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<UserDTO> createUser(@RequestBody @Valid final UserDTO userDTO) {
        UserDTO response = userService.saveUser(userDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable(name = "id") final Long id, @RequestBody @Valid final UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateUser(id, userDTO));
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<UserDTO> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}
