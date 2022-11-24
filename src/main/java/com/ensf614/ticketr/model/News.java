package com.ensf614.ticketr.model;

public class News {
    private String image;
    private String title;
    private String theNews;

    public News(String image, String theNews, String title) {
        this.image = image;
        this.theNews = theNews;
        this.title = title;
    }

    public String getMovie() {
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

}
