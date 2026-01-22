package com.ntdoc.notangdoccore.user.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RegistrationRequest {
    private String username;
    private String password;
    private String email;
    //rivate List<String> roles;
}
