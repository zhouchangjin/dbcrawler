package com.gamewolf.dbcrawler.crawler.book.handler;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.alibaba.fastjson.JSONObject;
import com.gamewolf.database.handler.SqliteHandler;
import com.gamewolf.database.orm.annotation.SqliteTableBinding;
import com.gamewolf.dbcrawler.base.BaseDBCrawler;
import com.gamewolf.dbcrawler.base.IDatabaseCrawler;
import com.gamewolf.dbcrawler.initializer.ParameterizedInitializer2;
import com.harmonywisdom.crawler.annotation.PageCrawlerDBSetting;
import com.harmonywisdom.crawler.page.PageCrawler;

public class TestBaseDBCrawler extends ParameterizedInitializer2{
	
	@PageCrawlerDBSetting(value = "mobygames_nes_list",propertieFile="sqlite.properties")
	public IDatabaseCrawler crawler;
	
	@SqliteTableBinding(javaClass = JSONObject.class, table = "videogame")
	public SqliteHandler sqliteHandler;

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

		String loginInfo="_ga=GA1.1.754618193.1737972366; darkMode=false; minCriticRatings=5; minUserRatings=25; perPage=18; browserSort=moby_score; includeDLC=false; remember-www=login-v2-973724-954438|64bdd8dfbedc5a3414931109800f81513561e6a0aae62db2ecf61d9fd19688382e28752ff7eb41b025b8d9a19f0a02000dbdd7db38b11681959be73ac8c7128a; session-www=.eJwlj0tqxDAQBe-idQRqdevnywxSfyYmwQ7yzGxC7h6FLB_Ug6pvd-Nr2u1xfujhNlcAjbmCRw3oKQv4hgieCEdF7anV5N7czaZe7257zKeutcu6ojAQZMkBrCGVQSFHUs5g2DqTxNIZKaVk0IUbIKBUDQVGocDQumXjFIVoUMxiBD0J5SI5lgYmVBFYqZI0QhUOfVhunDguqyB_Ws9L57_N53nfD_-KvhUskXxLK6EuZvTjWNA5Red-3Ff5l9vg5xdkzktZ.Z5pCqw.pl5L9kAozbIVtKzUjgsUrIVQCH8; _ga_DF0Y2K8D8P=GS1.1.1738161689.11.1.1738162859.0.0.0";
		BaseDBCrawler base=(BaseDBCrawler)crawler;
		
		PageCrawler.context.addCommonHeadder("cookie", loginInfo);
		String platForm="playstation";
		String nes="https://www.mobygames.com/platform/"+platForm+"/page:";
	
		//String snesUrl="https://www.mobygames.com/platform/snes/page:";
		//String genisisUrl="https://www.mobygames.com/platform/genesis/page:";
		//String gameBoyUrl="https://www.mobygames.com/platform/gameboy/page:";
		//String gameboyColor="https://www.mobygames.com/platform/gameboy-color/page:";
		//String gbaUrl="https://www.mobygames.com/platform/gameboy-advance/page:";
		//String ndsUrl="https://www.mobygames.com/platform/nintendo-ds/page:";
		//String playstation="https://www.mobygames.com/platform/playstation/page:";
		//String saturn="https://www.mobygames.com/platform/sega-saturn/page:";
		//String n64="https://www.mobygames.com/platform/n64/page:";
		//String psp="https://www.mobygames.com/platform/psp/page:";
		//String ps2="https://www.mobygames.com/platform/ps2/page:";
		//String gameCube="https://www.mobygames.com/platform/gamecube/page:";
		//String dreamcast="https://www.mobygames.com/platform/dreamcast/page:";
		//String xbox="https://www.mobygames.com/platform/xbox/page:";
		//String msx="https://www.mobygames.com/platform/msx/page:";
		//String c64="https://www.mobygames.com/platform/c64/page:";
		//String dos="https://www.mobygames.com/platform/dos/page:";
		Set<String> gameLink=new HashSet<String>();
		boolean errorFlag=false;
		for(int i=17;i<21;i++) {
			String pageUrl=nes+i+"/";
			base.addPage(pageUrl);
			List<Object> list=base.crawl();
			for(Object o:list) {
				JSONObject json=(JSONObject)o;
				json.put("platform", platForm);
				json.put("pageNum", i);
				String url=json.getString("pageUrl");
				if(!gameLink.contains(url)) {
					sqliteHandler.insertObject(o);
					System.out.println(json);
					gameLink.add(url);
				}else {
					errorFlag=true;
					System.out.println("Page num"+i+" duplicated");
					break;
				}
				
			}
			base.clearPage();
			if(errorFlag) {
				break;
			}
			Random r=new Random();
			int num=r.nextInt(6);
			try {
				Thread.sleep(1000*(num+3));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		

	}

}
