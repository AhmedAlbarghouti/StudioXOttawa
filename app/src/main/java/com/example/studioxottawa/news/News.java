package com.example.studioxottawa.news;

public class News {

    private String title;
    private String description;
    private String link;
    private String date;

    public News(String title, String description, String link, String date){
        this.setTitle(title);
        this.setDescription(description);
        this.setLink(link);
        this.setDate(date);
    }


    public void setTitle(String s){this.title = s;}

    public String getTitle(){return this.title;}

    public void setDescription(String s){this.description = s;}

    public String getDescription(){return this.description;}

    public void setLink(String s){this.link = s;}

    public String getLink(){return this.link;}

    public void setDate(String s){this.date = s;}

    public String getDate(){return this.date;}
}
