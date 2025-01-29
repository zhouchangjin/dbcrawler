package com.gamewolf.dbcrawler.base;

import java.util.List;


public interface IDatabaseCrawler {
	
	IDatabaseCrawler build();
	
	<T> IDatabaseCrawler setMappingConfig(Class<T> t);
	
	IDatabaseCrawler setTable(String table);
	
	IDatabaseCrawler setConfigurationColumn(String colName); 
	
	IDatabaseCrawler fromJDBCPropertieFile(String path,String file,boolean isRes); 
	
	IDatabaseCrawler setIdName(String name);
	
	IDatabaseCrawler setId(String id);
	
	void addPage(String url);
	
	void clearPage();
	
	List<Object> crawl();
}
