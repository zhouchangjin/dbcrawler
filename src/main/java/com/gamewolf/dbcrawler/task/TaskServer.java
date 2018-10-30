package com.gamewolf.dbcrawler.task;

import java.lang.reflect.Field;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;


import org.apache.activemq.ActiveMQConnectionFactory;

import com.alibaba.fastjson.JSON;
import com.gamewolf.dbcrawler.model.base.Task;
import com.gamewolf.dbcrawler.util.AutoAwareUtil;

public class TaskServer {
	
	@TaskType
	public static TaskQueue taskQueue;
	
	@FactoryType
	public static ConnectionFactory connectionFactory;
	
	Connection connection;
	
	Destination serverQueue;
	
	Destination clientQueue;
	
	public TaskServer() {
		
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
					connectionFactory=new ActiveMQConnectionFactory("zhouchangjin", "zhouchangjin", "tcp://114.116.44.106:61616");
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
	
	public void run() {
		
		try {
			connection = connectionFactory.createConnection();
			connection.start(); // 创建session
			Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE); // 消息目的地
			serverQueue= session.createQueue("task"); // 消息消费者
			clientQueue=session.createQueue("crawl_task");
			MessageConsumer consumer = session.createConsumer(serverQueue);
			MessageProducer producer = session.createProducer(clientQueue);
			producer.setDeliveryMode(DeliveryMode.PERSISTENT); // 发送消息
			while (true) {
				TextMessage message = (TextMessage) consumer.receive();
				if (message != null) {
					Task t=JSON.parseObject(message.getText(),Task.class);
					System.out.println(message.getText());
					taskQueue.addTask(t);
					producer.send(message);
					session.commit();
				} else {
					break;
				}
			}
			session.close();
			connection.close();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // 启动连接

		
	}

	public static void main(String[] args) {
		TaskServer server=new TaskServer();
		server.init();
		server.run();
	}

}
