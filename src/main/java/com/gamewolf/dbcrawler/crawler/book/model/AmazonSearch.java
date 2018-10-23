package com.gamewolf.dbcrawler.crawler.book.model;

import com.gamewolf.dbcrawler.model.base.PageObject;

public class AmazonSearch extends PageObject{

	String bookName;
	String bookCover;
	String link;
	public String getBookName() {
		return bookName;
	}
	public void setBookName(String bookName) {
		this.bookName = bookName;
	}
	public String getBookCover() {
		return bookCover;
	}
	public void setBookCover(String bookCover) {
		this.bookCover = bookCover;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}

}
