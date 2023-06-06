package org.launchcode.javawebdevtechjobsauthentication.models.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class LoginFormDTO {

    @NotNull
    @NotBlank
    @Size(min=3,max=50, message="Username must be between 3 to 50 characters")
    private String username;

    @NotNull
    @NotBlank
    @Size(min=3,max=50, message="Password must be between 3 to 50 characters")
    private String password;

    public LoginFormDTO(){}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
