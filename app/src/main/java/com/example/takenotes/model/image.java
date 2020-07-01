package com.example.takenotes.model;

public class image {
    private String ImageName;
    private String ImageUrl;

    public image(String imageName, String imageUrl) {
        ImageName = imageName;
        ImageUrl = imageUrl;
    }
    public image()
    {

    }

    public String getImageName() {
        return ImageName;
    }

    public void setImageName(String imageName) {
        ImageName = imageName;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }
}
