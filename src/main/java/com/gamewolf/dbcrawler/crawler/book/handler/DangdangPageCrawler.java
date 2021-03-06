package com.gamewolf.dbcrawler.crawler.book.handler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gamewolf.dbcrawler.base.DBCrawler;
import com.gamewolf.dbcrawler.crawler.book.model.DangDangPricePage;
import com.gamewolf.dbcrawler.initializer.ParameterizedInitializer;
import com.gamewolf.dbcrawler.model.base.Task;
import com.harmonywisdom.crawler.annotation.PageCrawlerDBSetting;
import com.harmonywisdom.crawler.httputil.HtmlFetcher;
import com.harmonywisdom.crawler.proxy.Proxy;
import com.harmonywisdom.crawler.proxy.ProxyPool;
import com.harmonywisdom.crawler.proxy.ProxyTap;
import com.harmonywisdom.crawler.proxy.ProxyTester;
import com.harmonywisdom.crawler.proxy.ResultParser;

public class DangdangPageCrawler extends ParameterizedInitializer{
	
	
	@PageCrawlerDBSetting(value = "DETAIL_DD_PRICE")
	public DBCrawler crawler;
	
	Connection connection;


	public DBCrawler getCrawler() {
		return crawler;
	}

	public void setCrawler(DBCrawler crawler) {
		this.crawler = crawler;
	}

	public static void main(String[] args) {
		DangdangPageCrawler main=new DangdangPageCrawler();
		main.init(args);
		main.run();
	}

	private void run() {
		Integer id=params.getIntegerValue("id");
		if(id==null) {
			id=33701;
		}
		String url="http://webapi.http.zhimacangku.com/getip?num=1&type=2&pro=&city=0&yys=0&port=1&pack="+id+"&ts=0&ys=0&cs=0&lb=1&sb=0&pb=4&mr=1&regions=";
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
				String url="http://product.m.dangdang.com/1146180050.html";
				String res=HtmlFetcher.FetchFromUrlWithProxy(url, proxy.getHost(), proxy.getPort());
				if(res==null ||"".equals(res)) {
					System.out.println("=======连接已经失效=========");
					return false;
				}else if(res.contains("error404")){
					System.out.println("========反爬虫==============");
					return false;
				}else {
					return true;
				}
			}
		};
		
		ProxyPool pool=new ProxyPool(url, parser);
		
		
		boolean useProxyFlag=false;
		String useProxy=this.params.getStringValue("use_proxy");
		String file=this.params.getStringValue("out_file");//"c:/dangdang_page.txt";
		if(file==null) {
			file="out.txt";
		}
		if(useProxy==null || !useProxy.equals("true")) {
			useProxyFlag=false;
		}else {
			useProxyFlag=true;
		}
		try {
			BufferedWriter bw=new BufferedWriter(new FileWriter(file,true));
			String brokerUrl="tcp://www.51meiyu.cn:55732";
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("zhouchangjin", "zhouchangjin", brokerUrl);
			
			connection = connectionFactory.createConnection();
			connection.start(); // 创建session
			Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE); // 消息目的地
			Queue clientQueue=session.createQueue("crawl_task");
			MessageConsumer consumer = session.createConsumer(clientQueue);
			String testUrl="http://product.m.dangdang.com/1146180050.html";
			if(!useProxyFlag) {
				//crawler.getCrawler().context.login(testUrl);
			}
			while (true) {
				
				try {
					Thread.sleep(700);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				TextMessage message = (TextMessage) consumer.receive();
				if (message != null) {
					session.commit();
					Task t=JSON.parseObject(message.getText(),Task.class);
					String page=t.getTaskPage();
					Date now=new Date();
					//long time=now.getTime()/1000;
					String processedUrl=page.replace("product.dangdang.com", "product.m.dangdang.com").trim();//+"?t="+time;
					//在爬取前试探ip被封
					if(!useProxyFlag) {
						String res=crawler.getCrawler().context.fetchHTML(testUrl);
						
						if(res==null || res.equals("")) {
							System.out.println("====未知原因=====");
							break;
						}else if(res!=null && res.contains("error404")){
							System.out.println("========反爬虫1==============");
							break;
						}
					}
					
					
					
					crawler.getCrawler().addPage(processedUrl);
					DangDangPricePage data=crawl(processedUrl,useProxyFlag,pool,test);
					crawler.getCrawler().clearPage();
					if(data!=null) {
						if(!useProxyFlag && (data.getProductJson()==null || data.getProductJson().equals(""))) {
							System.out.println("未知原因");
							break;
						}
						String line=data.getUrl()+","+data.getOriginalPrice()+","+data.getPrice()+",'"+data.getCategoryText()+"'"+",'"+data.getProductJson()+"'";
						bw.append(line);
						bw.newLine();
						bw.flush();
					}else {
						if(!useProxyFlag) {
							String res=crawler.getCrawler().context.fetchHTML(testUrl);//HtmlFetcher.FetchFromUrl(processedUrl);
							if(res==null || res.equals("")) {
								System.out.println("====未知原因=====");
								break;
							}else if(res!=null && res.contains("error404")){
								System.out.println("========反爬虫2==============");
								break;
							}
						}
					}
					
				} else {
					break;
				}
			}
			bw.close();
			session.close();
			connection.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	
	private DangDangPricePage crawl(String url,boolean useProxy,ProxyTap tap,ProxyTester tester) {
		System.out.println(url);
		List<Object> list;
		if(useProxy) {
			list= crawler.getCrawler().crawlWithProxy(DangDangPricePage.class, tap, tester);
		}else {
			list= crawler.getCrawler().crawl(DangDangPricePage.class);
		}
		
		
		if(list.size()>0) {
			DangDangPricePage p=(DangDangPricePage)list.get(0);
			if(p.getPrice()!=null && !"".equals(p.getPrice())) {
				return p;
			}else {
				return null;
			}
		}
		return null;
	}

}
