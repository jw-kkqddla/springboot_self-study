package com.example.demo.pojo.dto;

import jakarta.validation.constraints.NotBlank;

public class AdminDTO {
    @NotBlank(message = "账户不能为空")
    private String adminName;
    @NotBlank(message = "密码不能为空")
    private String password;

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "AdminDTO{" +
                "name='" + adminName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
