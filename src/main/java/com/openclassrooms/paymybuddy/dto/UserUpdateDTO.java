package com.openclassrooms.paymybuddy.dto;

import lombok.Data;

@Data
public class UserUpdateDTO {
    private String username;
    private String email;
    private String password;
}
