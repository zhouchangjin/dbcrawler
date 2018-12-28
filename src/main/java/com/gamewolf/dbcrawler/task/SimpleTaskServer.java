package com.gamewolf.dbcrawler.task;

import java.lang.reflect.Field;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;


import com.gamewolf.dbcrawler.util.AutoAwareUtil;

public class SimpleTaskServer {
	
	@FactoryType
	public static ConnectionFactory connectionFactory;
	
	Connection connection;
	
	Session session; 
	
	Destination taskMonitor;
	
	Destination taskCreator;
	
	Destination task;
	
	@TaskType
	public static TaskQueue taskQueue;
	
	public SimpleTaskServer() {
		
	}
	
	public void init() {
		if(taskQueue==null) {
			try {
				Field f=TaskServer.class.getField("taskQueue");
				TaskType t=f.getAnnotation(TaskType.class);
				String type=t.type();
				if(type.equals("mysql")) {
					taskQueue=new DefaultMysqlTaskQueue();
					AutoAwareUtil.autoawireMysql(taskQueue);
				}else if(type.equals("redis")) {
					
				}
				
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(connectionFactory==null) {
			try {
				Field f=TaskServer.class.getField("connectionFactory");
				FactoryType fac=f.getAnnotation(FactoryType.class);
				if(fac.type().equals("activemq")) {
					connectionFactory=new ActiveMQConnectionFactory("admin", "admin", "tcp://www.51meiyu.cn:55732");
				}
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		SimpleTaskServer server=new SimpleTaskServer();
		server.init();
		server.run();
	}

	private void run() {
		
		try {
			connection = connectionFactory.createConnection();
			connection.start(); // 创建session
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE); // 消息目的地
			
			taskCreator = session.createQueue("create_task");  // 接收任务创建消息
			task = session.createQueue("task");                // 创建任务执行消息
			taskMonitor = session.createQueue("monitor_task"); // 接收任务完成消息
			
			MessageConsumer create_consumer = session.createConsumer(taskCreator);
			MessageProducer producer = session.createProducer(task);
			MessageConsumer monitor_consumer = session.createConsumer(taskMonitor);
			producer.setDeliveryMode(DeliveryMode.PERSISTENT); // 发送消息
			
			create_consumer.setMessageListener(new MessageListener() {
				
				@Override
				public void onMessage(Message message) {
					TextMessage msg=(TextMessage)message; 
					String json="";
					try {
						json = msg.getText();
						
						System.out.println(json);
					} catch (JMSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
					
				}
			});
			
			

			while(true) {
				Thread.sleep(1000);
			}
			
		} catch (JMSException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				session.close();
				connection.close();
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}


}
