package com.afrosofttech.rest_jwt_demo.service;

import com.afrosofttech.rest_jwt_demo.dto.auth.request.AccountRequestDto;
import com.afrosofttech.rest_jwt_demo.dto.auth.response.AccountResponseDto;
import com.afrosofttech.rest_jwt_demo.dto.auth.response.ProfilePayload;
import com.afrosofttech.rest_jwt_demo.entity.Account;
import com.afrosofttech.rest_jwt_demo.exception.ResourceNotFoundException;
import com.afrosofttech.rest_jwt_demo.repository.AccountRepository;
import com.afrosofttech.rest_jwt_demo.util.constants.Authority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AccountService implements UserDetailsService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    public AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder){
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Account save(Account account) {
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        if(account.getAuthorities() == null)
            account.setAuthorities(Authority.USER.name());
        return accountRepository.save(account);
    }

    public List<AccountResponseDto> findAll(){
        return accountRepository.findAll().stream()
                .map(account -> new AccountResponseDto(
                        account.getId(),
                        account.getEmail(),
                        account.getAuthorities()
                ))
                .collect(Collectors.toList());
    }

    public void create(AccountRequestDto dto) {
        Account account = new Account();
        account.setEmail(dto.getEmail());
        account.setPassword(passwordEncoder.encode(dto.getPassword()));
        account.setAuthorities(Authority.USER.name());
        accountRepository.save(account);
    }
    public ProfilePayload getProfile(String email) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found for email: " + email));
        return ProfilePayload.builder()
                .id(account.getId())
                .email(account.getEmail())
                .authorities(account.getAuthorities())
                .build();
    }
    public AccountResponseDto updatePassword(String email, String newPassword) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found for email: " + email));

        account.setPassword(newPassword); // ideally encode this
        this.save(account);

        return AccountResponseDto.builder()
                .id(account.getId())
                .email(account.getEmail())
                .authorities(account.getAuthorities())
                .build();
    }
    public AccountResponseDto updateAuthorities(String newAuthorities, Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found for id: " + id));

        account.setAuthorities(newAuthorities);
        this.save(account);

        return AccountResponseDto.builder()
                .id(account.getId())
                .email(account.getEmail())
                .authorities(account.getAuthorities())
                .build();
    }
    public void deleteUser(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found for id: " + id));
        accountRepository.delete(account);
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
         Optional<Account> optionalAccount = accountRepository.findByEmail(email);
         if(!optionalAccount.isPresent()){
             throw new UsernameNotFoundException("Account Not Found");
         }
         Account account = optionalAccount.get();
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(account.getAuthorities()));
        return new User(account.getEmail(), account.getPassword(), authorities);
    }
}
