package com.gamewolf.dbcrawler.model.base;

import com.harmonywisdom.crawler.page.RegExpUtil;

public class Goods extends Search {
	
	Float price=-1.0f;

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}
	
	public void setPrice(String price) {
		
		String res=RegExpUtil.findStr(price, "[0-9]+\\.[0-9]+");
		if(res!=null && !"".equals(res)) {
			this.price=Float.parseFloat(res);
		}
		
	}

}
