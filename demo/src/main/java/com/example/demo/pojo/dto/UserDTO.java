package com.example.demo.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public class UserDTO {
    @NotBlank(message = "账户不能为空")
    private String userName;
    @NotBlank(message = "密码不能为空")
    @Length(min = 6,max = 20)
    private String password;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "name='" + userName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}


