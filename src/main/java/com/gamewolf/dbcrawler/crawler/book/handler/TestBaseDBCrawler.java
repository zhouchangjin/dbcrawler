package com.gamewolf.dbcrawler.crawler.book.handler;

import java.util.List;

import com.gamewolf.dbcrawler.base.BaseDBCrawler;
import com.gamewolf.dbcrawler.base.IDatabaseCrawler;
import com.gamewolf.dbcrawler.initializer.ParameterizedInitializer2;
import com.harmonywisdom.crawler.annotation.PageCrawlerDBSetting;

public class TestBaseDBCrawler extends ParameterizedInitializer2{
	
	@PageCrawlerDBSetting(value = "mobygames_nes_list",propertieFile="sqlite.properties")
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
		
		//String url="https://www.mobygames.com/game/1262/riven-the-sequel-to-myst/";
		String url="https://www.mobygames.com/platform/nes/";
		BaseDBCrawler base=(BaseDBCrawler)crawler;
		base.addPage(url);
		List<Object> list=base.crawl();
		for(Object o:list) {
			System.out.println(o);
		}

	}

}
