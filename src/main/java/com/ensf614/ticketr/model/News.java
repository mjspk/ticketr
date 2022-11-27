package com.ensf614.ticketr.model;

public class News {
    private String image;
    private String title;
    private String theNews;

    public News() {
    }

    public News(String image, String theNews, String title) {
        this.image = image;
        this.theNews = theNews;
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTheNews() {
        return theNews;
    }

    public void setTheNews(String theNews) {
        this.theNews = theNews;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
