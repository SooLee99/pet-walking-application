package com.example.petwalking;

// 이미지주소를 담을 클래스
public class Model {
    private String imageUrl;

    Model(){

    }

    public Model(String imageUrl){
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
