package com.gamewolf.dbcrawler.crawler.book.handler;

import java.io.File;
import java.net.URLEncoder;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSONObject;
import com.gamewolf.database.handler.DefaultRowRetrieveCallBackHandler;
import com.gamewolf.database.handler.IRowRetrieveCallback;
import com.gamewolf.database.handler.SqliteHandler;
import com.gamewolf.database.orm.annotation.SqliteTableBinding;
import com.gamewolf.dbcrawler.base.IDatabaseCrawler;
import com.gamewolf.dbcrawler.initializer.ParameterizedInitializer2;
import com.harmonywisdom.crawler.annotation.PageCrawlerDBSetting;
import com.harmonywisdom.crawler.page.PageCrawler;

public class BaiduSearch extends ParameterizedInitializer2{
	
	@PageCrawlerDBSetting(value = "baidu_search",propertieFile="sqlite.properties")
	public IDatabaseCrawler crawler;
	
	@SqliteTableBinding(javaClass = JSONObject.class, table = "videogame")
	public SqliteHandler gameTable;
	
	@SqliteTableBinding(javaClass = JSONObject.class, table = "game_chinese_refer")
	public SqliteHandler gameChTable;
	
	public IDatabaseCrawler getCrawler() {
		return crawler;
	}

	public void setCrawler(IDatabaseCrawler crawler) {
		this.crawler = crawler;
	}
	
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
		//matchName("超级马力欧兄弟3 (豆瓣)");
		
		
		BaiduSearch app=new BaiduSearch();
		app.init(args);
		app.run();
	}
	
	private static String matchName(String name) {
		if(name.contains("豆瓣")) {
		  name=name.replace("(豆瓣)","");
		  String rexExp="([\u4e00-\u9fa5]+\\s*[0-9]*)";
		  Pattern pattern=Pattern.compile(rexExp);
		  Matcher m=pattern.matcher(name);
		  if(m.find()){
			  String gameName=m.group(0);
			  System.out.println("找到"+gameName);
			  return gameName;
		  }else {
			  return "";
		  }
		}else {
			return "";
		}
	}

	private void run() {
		String loginInfo="BIDUPSID=092084F0DCB3CC8CDF097DA037550F92; PSTM=1730038034; BAIDUID=12948806C6758F59A9DC96CED425C610:FG=1; BD_UPN=12314753; H_WISE_SIDS_BFESS=60278_61027_61876_61987_62055_62066_62077; H_WISE_SIDS=60278_61027_61876_61987_62055_62066_62077; H_PS_PSSID=60278_61027_61876_61987_62055_62066_62077; BDORZ=B490B5EBF6F3CD402E515D22BCDA1598; kleck=33f059269d2c09afade282fb21ab48116963b44cf3547685; BAIDUID_BFESS=12948806C6758F59A9DC96CED425C610:FG=1; delPer=0; BD_CK_SAM=1; PSINO=1; H_PS_645EC=3debIdPGnAW9AvCe3YctXHaTnGlTZAm1FNLOruj9ktiWNA48WJ8J9iIkUHE; BA_HECTOR=2k2ga40g0l2480a5208h2l2h27np7n1jqgueo1u; ZFY=amq4nLW7nvua:AC1h9E6n8wj27:A5j6kIlKS7:AoCrlsAU:C";
		PageCrawler.context.addCommonHeadder("Cookie", loginInfo);
		IRowRetrieveCallback callback=new DefaultRowRetrieveCallBackHandler();
		gameTable.retrieveRows(callback,200,100);
		List<Object> all=callback.sinkList();
		
		for(Object row:all) {
			JSONObject object=(JSONObject)row;
			int id=object.getIntValue("id");
			String name=object.getString("gameName");
			String plat=object.getString("platform");
			//System.out.println(name);
			
			long cnt=gameChTable.getCnt("game_id="+id);
			if(cnt>0) {
				System.out.println(name+"已有中文名");
				continue;
			}
			String urlout=URLEncoder.encode(name+" 豆瓣 "+plat);
			String url="http://www.baidu.com/s?wd="+urlout+"&oq=test&rsv_pq=b95b10ae0001f3c5&rsv_t=7e605NH%2BX%2FVlSSWGpe6reZMfDM9Q5yHGimXDxFD9dnquyZ17NBY8inafwoE&rqlang=cn&rsv_dl=tb&rsv_enter=1&rsv_sug3=33&rsv_sug1=29&rsv_sug7=100&rsv_sug2=0&rsv_btype=t&inputT=10024&rsv_sug4=10024";
			System.out.println(url);
			crawler.addPage(url);
			JSONObject gameChinese=new JSONObject();
			gameChinese.put("gameId", id);
			gameChinese.put("gameName", name);
			List<Object> pages=crawler.crawl();
			for(Object p:pages) {
				JSONObject o=(JSONObject)p;
				String title=o.getString("title");
				String linke=o.getString("link");
				String outName=matchName(title);
				if(!outName.equals("")) {
					gameChinese.put("gameCnName", outName);
					gameChinese.put("referPage", linke);
					gameChTable.insertObject(gameChinese);
					break;
				}
			}
			crawler.clearPage();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
