package com.gamewolf.dbcrawler.initializer;

import java.lang.reflect.Field;

import com.gamewolf.database.dbconnector.ConnectionProperties;
import com.gamewolf.database.dbmeta.DataSourceType;
import com.gamewolf.database.dbsource.DataSourceFactory;
import com.gamewolf.database.dbsource.MysqlDataSource;
import com.gamewolf.database.handler.MappingConfig;
import com.gamewolf.database.handler.MySqlHandler;
import com.gamewolf.database.orm.annotation.MysqlTableBinding;
import com.harmonywisdom.crawler.annotation.PageCrawlerSetting;
import com.harmonywisdom.crawler.init.CrawlerInitializer;
import com.harmonywisdom.crawler.page.JSONObjectBinding;
import com.harmonywisdom.crawler.page.JSONPageCrawler;
import com.harmonywisdom.crawler.page.ObjectPageBingding;
import com.harmonywisdom.crawler.page.PageCrawler;

public class BaseInitializer implements IInitializer{
	
	public static DataSourceFactory factory = new DataSourceFactory();

	@Override
	public void init() {
		// TODO Auto-generated method stub
		mysqlaware();
		crawleraware();
	}
	
	
	void mysqlaware() {
		
		Field f[]=this.getClass().getFields();
		for(Field field:f) {
			if(field.getType().equals(MySqlHandler.class) && field.isAnnotationPresent(MysqlTableBinding.class)) {
				MysqlTableBinding binding=field.getAnnotation(MysqlTableBinding.class);
				@SuppressWarnings("rawtypes")
				Class t=binding.javaClass();
				MappingConfig config=new MappingConfig();
				config.setMappingClazz(t);
				String table=binding.table();
				String propP=binding.propertiePath();
				String prop=binding.propertieFile();
				boolean isRes=binding.isResource();
				
				ConnectionProperties mysqlProp=ConnectionProperties.loadPropertiesFromPropetiesFile(propP, prop, isRes);
				MysqlDataSource datasource=(MysqlDataSource)factory.getDataSourceByType(DataSourceType.mysql(), mysqlProp);
				datasource.setTable(table);
				MySqlHandler mysqlHandler=new MySqlHandler();
				mysqlHandler.setConfig(config);
				mysqlHandler.setDatasource(datasource);
				mysqlHandler.initialize();
				try {
					field.set(this, mysqlHandler);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					continue;
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					continue;
				}
			}
		}
		
		
	}
	
	void crawleraware() {
		
		Field f[]=this.getClass().getFields();
		for(Field field:f) {
			if(field.getType().equals(PageCrawler.class) && field.isAnnotationPresent(PageCrawlerSetting.class)) {
				PageCrawlerSetting setting=field.getAnnotation(PageCrawlerSetting.class);
				String xmlPath=setting.xmlPath();
				String xmlfile=setting.xmlFile();
				boolean isRes=setting.isResource();
				PageCrawler crawler=new PageCrawler();
				if(isRes) {
					
					String path=CrawlerInitializer.class.getClassLoader().getResource(xmlPath+"/"+xmlfile).getFile();
					ObjectPageBingding binding=ObjectPageBingding.buildFromXML(path);
					crawler.setBinding(binding);
					
				}else {
					
					String path=setting.xmlPath()+"/"+setting.xmlFile();
					ObjectPageBingding binding=ObjectPageBingding.buildFromXML(path);
					crawler.setBinding(binding);
					
				}
				
				try {
					field.set(this, crawler);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if(field.getType().equals(JSONPageCrawler.class) && field.isAnnotationPresent(PageCrawlerSetting.class)) {
				
				PageCrawlerSetting setting=field.getAnnotation(PageCrawlerSetting.class);
				String xmlPath=setting.xmlPath();
				String xmlfile=setting.xmlFile();
				boolean isRes=setting.isResource();
				
				JSONPageCrawler crawler=new JSONPageCrawler();
				String path="";
				if(isRes) {
					path=CrawlerInitializer.class.getClassLoader().getResource(xmlPath+"/"+xmlfile).getFile();
					
				}else {
					path=setting.xmlPath()+"/"+setting.xmlFile();
				}
				JSONObjectBinding binding=JSONObjectBinding.buildFromXML(path);
				crawler.setBinding(binding);
				try {
					field.set(this, crawler);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		
	}
	
	
	


}
