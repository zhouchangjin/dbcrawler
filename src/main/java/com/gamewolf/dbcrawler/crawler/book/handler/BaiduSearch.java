package com.gamewolf.dbcrawler.crawler.book.handler;

import java.io.File;

import com.alibaba.fastjson.JSONObject;
import com.gamewolf.database.handler.SqliteHandler;
import com.gamewolf.database.orm.annotation.SqliteTableBinding;
import com.gamewolf.dbcrawler.initializer.ParameterizedInitializer2;

public class BaiduSearch extends ParameterizedInitializer2{
	
	@SqliteTableBinding(javaClass = JSONObject.class, table = "videogame")
	public SqliteHandler gameTable;
	
	public static void remove() {
		String folder="d:/game/image/2/";
		File f=new File(folder);
		String path[]=f.list();
		for(String p:path) {
			if(!p.endsWith("webp")) {
				
				File pic=new File(folder+p);
				if(pic.exists()) {
					pic.delete();
				}
				System.out.println(p);
			}
		}
	}


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		remove();
		/**
		BaiduSearch app=new BaiduSearch();
		app.init(args);
		app.run();**/
	}

	private void run() {
		// TODO Auto-generated method stub
		
	}

}
