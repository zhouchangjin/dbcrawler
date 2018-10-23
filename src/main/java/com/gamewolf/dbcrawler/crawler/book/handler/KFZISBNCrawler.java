package com.gamewolf.dbcrawler.crawler.book.handler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gamewolf.dbcrawler.base.DBCrawler;
import com.gamewolf.dbcrawler.initializer.ParameterizedInitializer;
import com.gamewolf.dbcrawler.model.base.Search;
import com.gamewolf.util.book.ISBNCode;
import com.harmonywisdom.crawler.annotation.PageCrawlerDBSetting;
import com.harmonywisdom.crawler.httputil.HtmlFetcher;
import com.harmonywisdom.crawler.proxy.Proxy;
import com.harmonywisdom.crawler.proxy.ProxyPool;
import com.harmonywisdom.crawler.proxy.ProxyTester;
import com.harmonywisdom.crawler.proxy.ResultParser;

public class KFZISBNCrawler extends ParameterizedInitializer{
	
	@PageCrawlerDBSetting(value = "ISBN_KFZ")
	public DBCrawler crawler;

	public DBCrawler getCrawler() {
		return crawler;
	}

	public void setCrawler(DBCrawler crawler) {
		this.crawler = crawler;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		KFZISBNCrawler main=new KFZISBNCrawler();
		main.init(args);
		main.run();
		
	}

	private void run() {
		String url="http://webapi.http.zhimacangku.com/getip?num=1&type=2&pro=&city=0&yys=0&port=1&pack=19087&ts=0&ys=0&cs=0&lb=1&sb=0&pb=4&mr=1&regions=";
		ResultParser parser=new ResultParser() {
			@Override
			public List<Proxy> parse(String text) {
				List<Proxy> list=new ArrayList<Proxy>();
				JSONObject obj=JSON.parseObject(text);
				if(obj.getBoolean("success")) {
					JSONArray array=obj.getJSONArray("data");
					for(int i=0;i<array.size();i++) {
						JSONObject proxyObj=array.getJSONObject(i);
						String ip=proxyObj.getString("ip");
						int port=proxyObj.getIntValue("port");
						Proxy p=new Proxy();
						p.setHost(ip);
						p.setPort(port);
						list.add(p);
					}
				}
				return list;
			}
		};
		
		ProxyTester test=new ProxyTester() {
			
			@Override
			public boolean test(Proxy proxy) {
				// TODO Auto-generated method stub
				String url="http://www.baidu.com";
				String res=HtmlFetcher.FetchFromUrlWithProxy(url, proxy.getHost(), proxy.getPort());
				if(res==null ||"".equals(res)) {
					System.out.println("=======连接已经失效=========");
					return false;
				}else {
					return true;
				}
			}
		};
		
		ProxyPool pool=new ProxyPool(url, parser);
		
		int startPage=getPage();
		String isbnFile="c:/isbn_kfz_rest.txt";
		String outFile="c:/isbn_kfz.txt";
		try {
			BufferedReader br=new BufferedReader(new FileReader(isbnFile));
			BufferedWriter bw=new BufferedWriter(new FileWriter(outFile));
			String line=null;
			while((line=br.readLine())!=null) {
				if(ISBNCode.IsbnValidation(line)) {
					//System.out.println(line);
					String isbn=line;
					String page="http://search.kongfz.com/product_result/?key=" + isbn; 
					crawler.getCrawler().addPage(page);
					List<Object> list=crawler.getCrawler().crawlWithProxy(Search.class, pool, test);
					Search search=(Search) list.get(0);
					String newLine=line+","+search.getName()+","+search.getLink();
					System.out.println(newLine);
					bw.append(newLine);
					bw.newLine();
					bw.flush();
					crawler.getCrawler().clearPage();
				}
				Thread.sleep(500*(int)(Math.random()*10));
			}
			bw.close();
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
