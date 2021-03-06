package com.gamewolf.dbcrawler.crawler.book.handler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gamewolf.dbcrawler.base.DBCrawler;
import com.gamewolf.dbcrawler.crawler.book.model.DDBookFSearch;
import com.gamewolf.dbcrawler.initializer.ParameterizedInitializer;
import com.gamewolf.util.book.ISBNCode;
import com.harmonywisdom.crawler.annotation.PageCrawlerDBSetting;
import com.harmonywisdom.crawler.httputil.HtmlFetcher;
import com.harmonywisdom.crawler.proxy.Proxy;
import com.harmonywisdom.crawler.proxy.ProxyPool;
import com.harmonywisdom.crawler.proxy.ProxyTester;
import com.harmonywisdom.crawler.proxy.ResultParser;

public class DangDangISBNCrawler extends ParameterizedInitializer {
	
	@PageCrawlerDBSetting(value = "ISBN_DD")
	public DBCrawler crawler;
	
	public ResultParser parser;
	
	public ProxyTester tester;
	
	
	public ProxyPool pool;
	
	public static void main(String[] args) {
		DangDangISBNCrawler main=new DangDangISBNCrawler();
		main.init(args);
		main.run();
	}
    
	public DBCrawler getCrawler() {
		return crawler;
	}

	public void setCrawler(DBCrawler ddCrawler) {
		this.crawler = ddCrawler;
	}
	
	
	private void initProxy() {
		String id=params.getStringValue("id");
		if(id==null) {
			id="19087";
		}
		String url="http://webapi.http.zhimacangku.com/getip?num=1&type=2&pro=&city=0&yys=0&port=1&pack="+id+"&ts=0&ys=0&cs=0&lb=1&sb=0&pb=4&mr=1&regions=";
		
		//String url="http://webapi.http.zhimacangku.com/getip?num=1&type=2&pro=&city=0&yys=0&port=1&pack=19087&ts=0&ys=0&cs=0&lb=1&sb=0&pb=4&mr=1&regions=";
		
		parser=new ResultParser() {
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
		
		
		tester=new ProxyTester() {
			
			@Override
			public boolean test(Proxy proxy) {
				// TODO Auto-generated method stub
				String url="http://search.dangdang.com/?key=9787565823435";
				String res=HtmlFetcher.FetchFromUrlWithProxy(url, proxy.getHost(), proxy.getPort());
				if(res==null ||"".equals(res)) {
					System.out.println("=======连接已经失效=========");
					return false;
				}else if(res.contains("page_hurry")){
					System.out.println("========反爬虫==============");
					return false;
				}else {
					return true;
				}
			}
		};
		
		pool=new ProxyPool(url, parser);
	}

	private void run() {
		initProxy();
		int startPage=getPage();
		String useProxy=this.params.getStringValue("use_proxy");
		//String isbnFile="c:/isbn_dangdang_rest.txt";  //输入为isbn列表，每行一个isbn
		//String outFile="c:/isbn_dangdang.txt";
		String isbnFile=this.params.getStringValue("task_file");
		String outFile=this.params.getStringValue("output_file");
		System.out.println(isbnFile);
		System.out.println(outFile);
		System.out.println(useProxy);
		try {
			BufferedReader br=new BufferedReader(new FileReader(isbnFile));
			BufferedWriter bw=new BufferedWriter(new FileWriter(outFile));
			String line=null;
			while((line=br.readLine())!=null) {
				//System.out.println("处理的书isbn  "+line);
				if(ISBNCode.IsbnValidation(line)) {
					//System.out.println(line);
					String isbn=line;
					
					String ddUrl="http://search.dangdang.com/?key=" + isbn;//"https://search.jd.com/Search?keyword=" + isbn;
					crawler.getCrawler().addPage(ddUrl);
					List<Object> list=null;
					if(useProxy!=null && useProxy.equals("true")) {
						list=crawler.getCrawler().crawlWithProxy(DDBookFSearch.class, pool, tester);//.crawl(DDBookFSearch.class);
					}else {
						list=crawler.getCrawler().crawl(DDBookFSearch.class);
					}
					
					DDBookFSearch search=(DDBookFSearch) list.get(0);
					String bookName=search.getBookName();
					String bookCover=search.getBookCover();
					String lin=search.getLink();
					String author=search.getAuthor();
					String pressName=search.getPressName();
					String newLine=line+","+bookName+","+bookCover+","+lin+","+author+","+pressName;
					System.out.println(newLine);
					bw.write(newLine);
					bw.newLine();
					bw.flush();
					crawler.getCrawler().clearPage();
					Thread.sleep(300);
				}
				
			}
			br.close();
			bw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
