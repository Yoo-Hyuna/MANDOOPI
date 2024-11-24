package com.example.scalpingex01;

public class UserProfile {
    private String name;
    private String phone;
    private String birthdate;
    private String gender;

    // 기본 생성자
    public UserProfile() {}

    // 사용자 정보를 담는 생성자
    public UserProfile(String name, String phone, String birthdate, String gender) {
        this.name = name;
        this.phone = phone;
        this.birthdate = birthdate;
        this.gender = gender;
    }

    // Getter와 Setter 추가
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
