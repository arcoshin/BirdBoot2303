package com.birdboot.entity;

import java.io.Serializable;

/**
 * 文章模板類
 */
public class Article implements Serializable {
    private String title;
    private String author;
    private String articleText;

    public Article() {

    }

    public Article(String title, String author, String articleText) {
        this.title = title;
        this.author = author;
        this.articleText = articleText;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getArticleText() {
        return articleText;
    }

    public void setArticleText(String articleText) {
        this.articleText = articleText;
    }

    @Override
    public String toString() {
        return "Article{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                '}';
    }
}
