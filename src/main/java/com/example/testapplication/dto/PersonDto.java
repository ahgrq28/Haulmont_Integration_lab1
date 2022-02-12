package com.example.testapplication.dto;

import java.util.Date;

public class PersonDto {
    private Long personalAccount;
    private String fullName;
    private Date birthday;

    public Long getPersonalAccount() {
        return personalAccount;
    }

    public void setPersonalAccount(Long personalAccount) {
        this.personalAccount = personalAccount;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
}
