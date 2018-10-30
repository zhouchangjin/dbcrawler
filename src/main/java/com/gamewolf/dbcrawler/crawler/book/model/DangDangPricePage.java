package com.gamewolf.dbcrawler.crawler.book.model;

import com.gamewolf.dbcrawler.model.base.PageObject;

public class DangDangPricePage extends PageObject{
	
	String price;
	
	String originalPrice;
	
	String categoryText;
	
	String productJson;

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getOriginalPrice() {
		return originalPrice;
	}

	public void setOriginalPrice(String originalPrice) {
		this.originalPrice = originalPrice;
	}

	public String getCategoryText() {
		return categoryText;
	}

	public void setCategoryText(String categoryText) {
		this.categoryText = categoryText;
	}

	public String getProductJson() {
		return productJson;
	}

	public void setProductJson(String productJson) {
		this.productJson = productJson;
	}

}
