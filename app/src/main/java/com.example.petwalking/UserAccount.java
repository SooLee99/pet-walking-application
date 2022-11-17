package com.example.petwalking;
/*
*   사용자 계정 정보 모델 클래스
*   (추후 닉네임, 프로필 이미지 URL 등 저장가능)
 */

public class UserAccount {
    private String idToken;     // Firebase Uid (고유 토큰정보)
    private String emailId;     // 이메일 아이디
    private String password;    // 비밀번호
    private String name;        // 이름
    private String phoneNumber; // 핸드폰 전화번호
    private String result;      // 현재 로그인 상태(구글, 일반, 카카오)

    // Firebase 에서는 빈 생성자를 만들어야 한다. 그렇지 않으면 데이터 조회 시 에러발생 (ㅠㅠ)
    public UserAccount() { }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
