package com.m4rkovic.succulent_shop.auth;

import com.m4rkovic.succulent_shop.enumerator.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private Long id;
    private String token;
    private String email;
    private String firstname;
    private String lastname;
    private Role role;
    private String address;
}