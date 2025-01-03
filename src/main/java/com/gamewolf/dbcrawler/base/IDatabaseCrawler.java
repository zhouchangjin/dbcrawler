package com.gamewolf.dbcrawler.base;

public interface IDatabaseCrawler {
	
	IDatabaseCrawler build();
	
	<T> IDatabaseCrawler setMappingConfig(Class<T> t);
	
	IDatabaseCrawler setTable(String table);
	
	IDatabaseCrawler setConfigurationColumn(String colName); 
	
	IDatabaseCrawler fromJDBCPropertieFile(String path,String file,boolean isRes); 
	
	IDatabaseCrawler setIdName(String name);

}
