package com.gamewolf.dbcrawler.crawler.book.handler;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.gamewolf.database.handler.DefaultRowRetrieveCallBackHandler;
import com.gamewolf.database.handler.IRowRetrieveCallback;
import com.gamewolf.database.handler.SqliteHandler;
import com.gamewolf.database.orm.annotation.SqliteTableBinding;
import com.gamewolf.dbcrawler.base.BaseDBCrawler;
import com.gamewolf.dbcrawler.base.IDatabaseCrawler;
import com.gamewolf.dbcrawler.initializer.ParameterizedInitializer2;
import com.harmonywisdom.crawler.annotation.PageCrawlerDBSetting;

public class TestPictureCrawler  extends ParameterizedInitializer2{
	
	@PageCrawlerDBSetting(value = "mobygames_screenshots_",propertieFile="sqlite.properties")
	public IDatabaseCrawler crawler;
	
	@SqliteTableBinding(javaClass = JSONObject.class, table = "screenshot")
	public SqliteHandler sqliteHandlerScreenshot;
	
	@SqliteTableBinding(javaClass = JSONObject.class, table = "videogame")
	public SqliteHandler sqliteHandlerVG;
	
	@SqliteTableBinding(javaClass = JSONObject.class, table = "screenshot_task")
	public SqliteHandler sqliteHandlerTask;
	
	public IDatabaseCrawler getCrawler() {
		return crawler;
	}

	public void setCrawler(IDatabaseCrawler crawler) {
		this.crawler = crawler;
	}
	
	public static void main(String[] args) {
		TestPictureCrawler testPictureCrawler=new TestPictureCrawler();
		testPictureCrawler.init(args);
		testPictureCrawler.run();
	}

	private void run() {
		// TODO Auto-generated method stub
		BaseDBCrawler base=(BaseDBCrawler)crawler;
		long cnt=sqliteHandlerVG.getCnt();
		System.out.println(cnt);
		DefaultRowRetrieveCallBackHandler retrieveHandler=new DefaultRowRetrieveCallBackHandler();
		sqliteHandlerVG.retrieveRows(retrieveHandler);
		List<Object> pageList=retrieveHandler.sinkList();
		for(Object row:pageList) {
			JSONObject obj=(JSONObject)row;
			String name=obj.getString("gameName");
			int id=obj.getIntValue("id");
			String url=obj.getString("pageUrl");
			String screenshotUrl=url+"screenshots/";
			System.out.println(screenshotUrl);
			long idCnt=sqliteHandlerTask.getCnt("game_id="+id);
			System.out.print(idCnt);
			if(idCnt>0) {
				System.out.println(name+"图片任务已完成");
				continue;
			}
			base.addPage(screenshotUrl);
			List<Object> list=base.crawl();
			for(int i=0;i<list.size();i++) {
				
				JSONObject page=(JSONObject)list.get(i);
				page.put("gameId", id);
				page.put("gameName", name);
				System.out.println(page);
				sqliteHandlerScreenshot.insertObject(page);
			}
			JSONObject sTask=new JSONObject();
			sTask.put("gameId", id);
			sqliteHandlerTask.insertObject(sTask);
			base.clearPage();
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
