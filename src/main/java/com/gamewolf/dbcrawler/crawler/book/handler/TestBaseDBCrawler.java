package com.gamewolf.dbcrawler.crawler.book.handler;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.gamewolf.dbcrawler.base.BaseDBCrawler;
import com.gamewolf.dbcrawler.base.IDatabaseCrawler;
import com.gamewolf.dbcrawler.initializer.ParameterizedInitializer2;
import com.harmonywisdom.crawler.annotation.PageCrawlerDBSetting;

public class TestBaseDBCrawler extends ParameterizedInitializer2{
	
	@PageCrawlerDBSetting(value = "test",propertieFile="sqlite.properties")
	public IDatabaseCrawler crawler;

	public IDatabaseCrawler getCrawler() {
		return crawler;
	}

	public void setCrawler(IDatabaseCrawler crawler) {
		this.crawler = crawler;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TestBaseDBCrawler testBaseDBCrawler=new TestBaseDBCrawler();
		testBaseDBCrawler.init(args);
		testBaseDBCrawler.run();

	}

	private void run() {
		
		String url="https://www.mobygames.com/game/1262/riven-the-sequel-to-myst/";
		BaseDBCrawler base=(BaseDBCrawler)crawler;
		base.getCrawler().addPage(url);
		List<Object> result=base.getCrawler().crawl(JSONObject.class);
		JSONObject o=(JSONObject)result.get(0);
		System.out.print(o);
	}

}
