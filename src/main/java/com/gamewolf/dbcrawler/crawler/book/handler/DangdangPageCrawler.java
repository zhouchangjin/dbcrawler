package com.gamewolf.dbcrawler.crawler.book.handler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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
import com.gamewolf.database.handler.MySqlHandler;
import com.gamewolf.database.orm.annotation.MysqlTableBinding;
import com.gamewolf.dbcrawler.base.DBCrawler;
import com.gamewolf.dbcrawler.crawler.book.model.DangDangPricePage;
import com.gamewolf.dbcrawler.initializer.ParameterizedInitializer;
import com.gamewolf.dbcrawler.model.base.Task;
import com.harmonywisdom.crawler.annotation.PageCrawlerDBSetting;

public class DangdangPageCrawler extends ParameterizedInitializer{
	
	
	@PageCrawlerDBSetting(value = "DETAIL_DD_PRICE")
	public DBCrawler crawler;
	
	@MysqlTableBinding(javaClass=Task.class, table = "task")
	public MySqlHandler handler;

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
		// TODO Auto-generated method stub
		String file=this.params.getStringValue("out_file");//"c:/dangdang_page.txt";
		if(file==null) {
			file="c:/out.txt";
		}
		try {
			BufferedWriter bw=new BufferedWriter(new FileWriter(file,true));
			String brokerUrl="tcp://127.0.0.1:61616";
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("admin", "admin", brokerUrl);
			
			connection = connectionFactory.createConnection();
			connection.start(); // 创建session
			Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE); // 消息目的地
			Queue clientQueue=session.createQueue("crawl_task");
			MessageConsumer consumer = session.createConsumer(clientQueue);
			
			while (true) {
				TextMessage message = (TextMessage) consumer.receive();
				if (message != null) {
					session.commit();
					Task t=JSON.parseObject(message.getText(),Task.class);
					
					String page=t.getTaskPage();
					String processedUrl=page.replace("product.dangdang.com", "product.m.dangdang.com");
					crawler.getCrawler().addPage(processedUrl);
					DangDangPricePage data=crawl(processedUrl);
					crawler.getCrawler().clearPage();
					if(data!=null) {
						handler.updateObject("is_done=1", "task_id='"+t.getTaskId()+"'");
						String line=data.getUrl()+","+data.getOriginalPrice()+","+data.getPrice()+",'"+data.getCategoryText()+"'"+",'"+data.getProductJson()+"'";
						bw.append(line);
						bw.newLine();
						bw.flush();
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
	
	
	private DangDangPricePage crawl(String url) {
		System.out.println(url);
		
		List<Object> list= crawler.getCrawler().crawl(DangDangPricePage.class);
		
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
