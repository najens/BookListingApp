package com.example.android.booklistingapp;

/**
 * Created by Nate on 10/10/2017.
 */

public class Book {

    // Define variables in Book class
    private String mTitle;
    private String mAuthor;
    private String mSubject;
    private String mPages;
    private String mUrl;
    private String mImage;

    /**
     * Create a new Book object.
     *
     * @param title   is the tile of the book
     * @param author  is the author of the book
     * @param subject is the subject of the book
     * @param pages   is the the number of pages of the book
     * @param url     is the url of the book
     * @param image   is the image url of the book
     */
    public Book(String title, String author, String subject, String pages, String url, String image) {
        mTitle = title;
        mAuthor = author;
        mSubject = subject;
        mPages = pages;
        mUrl = url;
        mImage = image;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getSubject() {
        return mSubject;
    }

    public String getPages() {
        return mPages;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getImage() {
        return mImage;
    }
}
