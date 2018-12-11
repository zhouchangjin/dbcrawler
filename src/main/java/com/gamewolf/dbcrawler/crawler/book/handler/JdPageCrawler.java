package com.gamewolf.dbcrawler.crawler.book.handler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.gamewolf.dbcrawler.base.DBCrawler;
import com.gamewolf.dbcrawler.crawler.book.model.JDPageObject;
import com.gamewolf.dbcrawler.initializer.ParameterizedInitializer;
import com.harmonywisdom.crawler.annotation.PageCrawlerDBSetting;
import com.harmonywisdom.crawler.httputil.HtmlFetcher;

public class JdPageCrawler extends ParameterizedInitializer{
	
	@PageCrawlerDBSetting(value = "DETAIL_JD_PRICE")
	public DBCrawler crawler;
	
	public DBCrawler getCrawler() {
		return crawler;
	}
	
	public void setCrawler(DBCrawler crawler) {
		this.crawler = crawler;
	}
	
	

	public static void main(String[] args) {
		JdPageCrawler main=new JdPageCrawler();
		main.init(args);
		main.run();
	}

	private void run() {
		String jdList="c:/jdoutList.txt";
		String jdOutList="c:/jdOut02.txt";
		File input=new File(jdList);
		File output=new File(jdOutList);
		try {
			BufferedReader br=new BufferedReader(new FileReader(input));
			BufferedWriter bw=new BufferedWriter(new FileWriter(output, true));
			String line=null;
			while((line=br.readLine())!=null) {
				crawler.getCrawler().addPage(line);
				
				List<Object> list=crawler.getCrawler().crawl(JDPageObject.class);
				
				JDPageObject obj=(JDPageObject)list.get(0);
				
				String urlForPrice="http://c0.3.cn/stock?skuId="+obj.getSkuid()+"&venderId="+obj.getVenderId()+"&cat="+obj.getCat()+"&area=1_72_2799_0";
				System.out.println(urlForPrice);
				String json=HtmlFetcher.FetchFromUrlWithCharsetSpecified(urlForPrice,"gb2312");
				String outputLine=line+","+json+","+JSON.toJSONString(obj);
				bw.write(outputLine);
				bw.newLine();
				bw.flush();
				crawler.getCrawler().clearPage();
			}
			bw.close();
			br.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


}
