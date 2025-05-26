package com.afrosofttech.rest_jwt_demo.controller;

import com.afrosofttech.rest_jwt_demo.dto.auth.request.AccountDto;
import com.afrosofttech.rest_jwt_demo.dto.auth.request.AuthorityDto;
import com.afrosofttech.rest_jwt_demo.dto.auth.request.PasswordDto;
import com.afrosofttech.rest_jwt_demo.dto.auth.response.AccountPayload;
import com.afrosofttech.rest_jwt_demo.dto.auth.response.ProfilePayload;
import com.afrosofttech.rest_jwt_demo.dto.auth.response.TokenPayload;
import com.afrosofttech.rest_jwt_demo.dto.auth.request.UserLoginDto;
import com.afrosofttech.rest_jwt_demo.service.AccountService;
import com.afrosofttech.rest_jwt_demo.service.TokenService;
import com.afrosofttech.rest_jwt_demo.util.constants.AccountError;
import com.afrosofttech.rest_jwt_demo.util.constants.AccountSuccess;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth Controller", description = "Controller for Account management")
@Slf4j
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final AccountService accountService;

    public AuthController(AuthenticationManager authenticationManager, TokenService tokenService,
                          AccountService accountService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.accountService = accountService;
    }

    @PostMapping("/token")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TokenPayload> token(@Valid @RequestBody UserLoginDto userLogin) throws AuthenticationException {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userLogin.getEmail(), userLogin.getPassword())
            );
            String token = tokenService.generateToken(authentication);
            return ResponseEntity.ok(new TokenPayload(token));
        } catch (AuthenticationException ex) {
            log.debug(AccountError.TOKEN_GENERATION_ERROR.toString(), ex.getMessage());
           return new ResponseEntity<>(new TokenPayload(null), HttpStatus.UNAUTHORIZED);
        }
    }
    @PostMapping(value= "/users/add", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a new User")
    public ResponseEntity<String> addUser(@Valid @RequestBody AccountDto accountDto) {
        try {
            accountService.create(accountDto);
            return ResponseEntity.ok(AccountSuccess.ACCOUNT_ADDED.toString());
        } catch (Exception e) {
            log.debug(AccountError.ADD_ACCOUNT_ERROR.toString(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to add account.");
        }
    }
    @GetMapping(value= "/users", produces = "application/json")
    @Operation(summary = "List user api")
    @SecurityRequirement(name = "afrosofttech-demo-api")
    public List<AccountPayload> findUsers() {
        return accountService.findAll();
    }
    @PutMapping(value= "/user/{userId}/authorities/update",
            produces = "application/json", consumes = "application/json")
    @Operation(summary = "Update authorities")
    @SecurityRequirement(name = "afrosofttech-demo-api")
    public ResponseEntity<AccountPayload> updateAuthorities(@Valid @RequestBody AuthorityDto authorityDto,@PathVariable Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body((accountService.updateAuthorities(authorityDto.getAuthorities(), userId)));
    }
    @GetMapping(value= "/profile", produces = "application/json")
    @Operation(summary = "View profile")
    @SecurityRequirement(name = "afrosofttech-demo-api")
    public ResponseEntity<ProfilePayload> profile(Authentication authentication) {
        return ResponseEntity.ok(accountService.getProfile(authentication.getName()));
    }
    @PutMapping(value= "/profile/password/update",
            produces = "application/json", consumes = "application/json")
    @Operation(summary = "Update password")
    @SecurityRequirement(name = "afrosofttech-demo-api")
    @ResponseStatus(HttpStatus.OK)
    public AccountPayload updatePassword(@Valid @RequestBody PasswordDto passwordDto, Authentication authentication) {
        return accountService.updatePassword(authentication.getName(), passwordDto.getPassword());
    }
    @DeleteMapping(value= "/user/{userId}/delete",
            produces = "application/json")
    @PreAuthorize("#userId == authentication.principal.id or hasRole('ADMIN')")
    @Operation(summary = "Delete user")
    @SecurityRequirement(name = "afrosofttech-demo-api")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        accountService.deleteUser(userId);
        return ResponseEntity.ok("User deleted");
    }

}
