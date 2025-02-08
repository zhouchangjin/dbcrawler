package com.gamewolf.dbcrawler.crawler.book.handler;

import java.io.File;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.gamewolf.database.handler.IRowRetrieveCallback;
import com.gamewolf.database.handler.SqliteHandler;
import com.gamewolf.database.orm.annotation.SqliteTableBinding;
import com.gamewolf.dbcrawler.initializer.ParameterizedInitializer2;
import com.gamewolf.util.image.ImageUtil;
import com.harmonywisdom.crawler.httputil.HtmlFetcher;
import com.harmonywisdom.crawler.httputil.PictureFetcher;

public class TestImgDownload extends ParameterizedInitializer2{
	

	
	@SqliteTableBinding(javaClass = JSONObject.class, table = "screenshot")
	public SqliteHandler screenshotTable;

	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TestImgDownload app=new TestImgDownload();
		app.init(args);
		app.run();

	}

	private void run() {
		// TODO Auto-generated method stub
		String parentFolder="d:/game/image/2";
		screenshotTable.retrieveRows(new IRowRetrieveCallback() {
			
			@Override
			public List<Object> sinkList() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public void onEachRow(Object row, int rowNum) {
				// TODO Auto-generated method stub
				JSONObject object=(JSONObject)row;
				int gameId=object.getIntValue("gameId");
				int id=object.getIntValue("id");
				String name=object.getString("gameName");
				if(name.contains(":")){
					name=name.replace(":", "-");
				}
				if(name.contains("/")) {
					name=name.replace("/", "_");
				}
				String url=object.getString("screenshot");
				String fileName=gameId+"_"+name.trim()+"_"+id;
				File checkFile=new File(parentFolder+"/"+fileName+".webp");
				if(!checkFile.exists()) {
					PictureFetcher.FetchPicture(url,fileName,parentFolder);
				}else {
					System.out.println(fileName+"已经下载");
				}
			}
			
			@Override
			public Object getObject(int index) {
				// TODO Auto-generated method stub
				return null;
			}
		}, 10001, 10000);;
	}

}
