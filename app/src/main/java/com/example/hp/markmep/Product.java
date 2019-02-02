package com.example.hp.markmep;

public class Product {
    private int id;
    private String details;
    private String subject;
    private  String present;


    public Product(int id, String details, String subject, String present) {
        this.id = id;
        this.details=details;
        this.subject=subject;
        this.present=present;

    }//constructor

    public String getPresent() {
        return present;
    }//getter

    public int getId() {
        return id;
    }//getter

    public String getDetails() {
        return details;
    }

    public String getSubject() {
        return subject;
    }

}


