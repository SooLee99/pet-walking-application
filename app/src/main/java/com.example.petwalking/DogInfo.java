package com.example.petwalking;

public class DogInfo {
    private String idToken;     // 회원확인
    private String dogName;     // 이름
    private String dogBreed;    // 견종
    private String dogGender;      // 성별
    private String dogBirth;       // 생년월일
    private String dogWeight;      // 몸무게

    public DogInfo() {
    }

    public String getDogName() {
        return dogName;
    }

    public void setDogName(String dogName) {
        this.dogName = dogName;
    }

    public String getDogBreed() {
        return dogBreed;
    }

    public void setDogBreed(String dogBreed) {
        this.dogBreed = dogBreed;
    }

    public String getDogGender() {
        return dogGender;
    }

    public void setDogGender(String dogGender) {
        this.dogGender = dogGender;
    }

    public String getDogBirth() {
        return dogBirth;
    }

    public void setDogBirth(String dogBirth) {
        this.dogBirth = dogBirth;
    }

    public String getDogWeight() {
        return dogWeight;
    }

    public void setDogWeight(String dogWeight) {
        this.dogWeight = dogWeight;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

}
