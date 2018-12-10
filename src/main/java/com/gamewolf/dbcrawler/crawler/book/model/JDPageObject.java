package com.gamewolf.dbcrawler.crawler.book.model;

import com.gamewolf.dbcrawler.model.base.GoodsPage;

public class JDPageObject extends GoodsPage{
	
	Float jdPrice=1.0f;
	
	String jdPriceStr;
	
	String bookName;
	
	String bookCover;
	
	String authorName;
	
	String isbn13;
	
	String type;
	
	String publishDate;
	
	String pageSize;
	
	String expressName;
	
	String  weight;
	
	String size;
	
	String summary;
	
	String skuid;
	
	String venderId;
	
	String cat;
	
	String catName;

	public String getCatName() {
		return catName;
	}

	public void setCatName(String catName) {
		this.catName = catName;
	}

	public String getSkuid() {
		return skuid;
	}

	public void setSkuid(String skuid) {
		this.skuid = skuid;
	}

	public String getVenderId() {
		return venderId;
	}

	public void setVenderId(String venderId) {
		this.venderId = venderId;
	}

	public String getCat() {
		return cat;
	}

	public void setCat(String cat) {
		
		this.cat = cat.trim().replace("[", "");
	}

	public Float getJdPrice() {
		return jdPrice;
	}

	public void setJdPrice(Float jdPrice) {
		this.jdPrice = jdPrice;
	}

	public String getJdPriceStr() {
		return jdPriceStr;
	}

	public void setJdPriceStr(String jdPriceStr) {
		this.jdPriceStr = jdPriceStr;
	}

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName.trim();
	}

	public String getBookCover() {
		return bookCover;
	}

	public void setBookCover(String bookCover) {
		this.bookCover = bookCover;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public String getIsbn13() {
		return isbn13;
	}

	public void setIsbn13(String isbn13) {
		this.isbn13 = isbn13;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(String publishDate) {
		this.publishDate = publishDate;
	}

	public String getPageSize() {
		return pageSize;
	}

	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}

	public String getExpressName() {
		return expressName;
	}

	public void setExpressName(String expressName) {
		this.expressName = expressName;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

}
