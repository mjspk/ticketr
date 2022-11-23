package com.ensf614.ticketr.model;

public class News {
    private Movie movie;
    private String theNews;

    public News(Movie movie, String theNews) {
        this.movie = movie;
        this.theNews = theNews;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public String getTheNews() {
        return theNews;
    }

    public void setTheNews(String theNews) {
        this.theNews = theNews;
    }

}
