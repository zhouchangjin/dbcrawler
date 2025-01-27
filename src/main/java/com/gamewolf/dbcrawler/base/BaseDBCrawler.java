package com.gamewolf.dbcrawler.base;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.gamewolf.database.dbconnector.ConnectionProperties;
import com.gamewolf.database.dbsource.DataSourceFactory;
import com.gamewolf.database.dbsource.ITableDatasource;
import com.gamewolf.database.handler.DataSourceHanlderFactory;
import com.gamewolf.database.handler.IDatasourceHandler;
import com.gamewolf.database.handler.MappingConfig;
import com.gamewolf.util.datafile.XMLNode;
import com.gamewolf.util.datafile.XMLReader;
import com.harmonywisdom.crawler.page.ObjectPageBingding;
import com.harmonywisdom.crawler.page.PageCrawler;

public class BaseDBCrawler implements IDatabaseCrawler{
	
	public static DataSourceFactory factory = new DataSourceFactory();
	
	IDatasourceHandler<?> datasourceHandler;
	
	PageCrawler crawler;
	
	MappingConfig config;
	
	ObjectPageBingding binding;
	
	Document doc;
	
	NodeList nodeList;
	
	String clz;
	
	String idCol;
	
	String value;
	
	String colField;
	
	String listPath;
	
	public BaseDBCrawler() {
		this.crawler=new PageCrawler();
	}
	
	public static BaseDBCrawler startBuild() {
		BaseDBCrawler dbCrawler=new BaseDBCrawler();
		return dbCrawler;
	}


	@Override
	public IDatabaseCrawler build() {
		// TODO Auto-generated method stub
		datasourceHandler.initialize();
		Object o=datasourceHandler.getOne(this.idCol+"='"+value+"'");
		String getMethodName="get"+colField.substring(0,1).toUpperCase()+colField.substring(1);
		try {
			Method m=o.getClass().getMethod(getMethodName);
			String text=m.invoke(o).toString();
			//System.out.println(text);
			XMLNode node=XMLReader.parseXMLString(text);
			if(node.getNode("List")!=null) {
				XMLNode list=node.getNode("List");
				String xpath=list.getNode("xpath").getValue();
				String className=list.getNode("Object").getAttribute("className").toString();
				XMLNode objNode=list.getNode("Object");
				binding=ObjectPageBingding.buildFromXMLString(objNode.toXML());
				this.listPath=xpath;
				this.clz=className;
				
			}else {
				binding=ObjectPageBingding.buildFromXMLString(text);
				crawler.setBinding(binding);
			}
			
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			return this;
		} catch (SecurityException e) {	
			e.printStackTrace();
			return this;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return this;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return this;
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			return this;
		}
		
		
		return this;
	}

	@Override
	public <T> IDatabaseCrawler setMappingConfig(Class<T> t) {
		config=new MappingConfig();
		config.setMappingClazz(t);
		datasourceHandler.setConfig(config);
		return this;
	}

	@Override
	public IDatabaseCrawler setTable(String table) {
		datasourceHandler.getDatasource().setTable(table);
		return this;
	}

	@Override
	public IDatabaseCrawler fromJDBCPropertieFile(String path, String file, boolean isRes) {
		ConnectionProperties connectionProperties=ConnectionProperties.loadPropertiesFromPropetiesFile(path, file, isRes);
		ITableDatasource datasource=(ITableDatasource) factory.getDataSourceByConnectionProperties(connectionProperties);
		DataSourceHanlderFactory dataSourceHanlderFactory=new DataSourceHanlderFactory();
		this.datasourceHandler=dataSourceHanlderFactory.createDatasourceHandler(datasource);
		datasourceHandler.setTableDatasource(datasource);
		return this;
	}

	@Override
	public IDatabaseCrawler setConfigurationColumn(String colName) {
		this.colField=colName;
		return this;
	}

	@Override
	public IDatabaseCrawler setIdName(String name) {
		this.idCol=name;
		return this;
	}
	
	public PageCrawler getCrawler() {
		return this.crawler;
	}

	@Override
	public IDatabaseCrawler setId(String id) {
		this.value=id;
		return this;
	}

}
