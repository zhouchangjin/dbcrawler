package com.gamewolf.dbcrawler.base;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.xpath.XPathAPI;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.gamewolf.database.dbconnector.ConnectionProperties;
import com.gamewolf.database.dbmeta.DataSourceType;
import com.gamewolf.database.dbsource.DataSourceFactory;
import com.gamewolf.database.dbsource.MysqlDataSource;
import com.gamewolf.database.handler.MappingConfig;
import com.gamewolf.database.handler.MySqlHandler;
import com.gamewolf.util.datafile.XMLNode;
import com.gamewolf.util.datafile.XMLReader;
import com.harmonywisdom.crawler.httputil.HtmlFetcher;
import com.harmonywisdom.crawler.page.ObjectPageBingding;
import com.harmonywisdom.crawler.page.PageCrawler;
import com.harmonywisdom.crawler.page.PageSelector;
import com.harmonywisdom.crawler.page.W3CNodeUtil;

public class DBCrawler{
	
	public static DataSourceFactory factory = new DataSourceFactory();
	
	MySqlHandler handler;
	PageCrawler crawler;
	MappingConfig config;
	MysqlDataSource datasource;
	
	ObjectPageBingding binding;
	String listPath;
	String clz;
	String colName;
	String fieldName;
	String idCol;
	String value;
	String listCont;
	Document doc;
	NodeList nodeList;
	
	
	public DBCrawler() {
		handler=new MySqlHandler();
		crawler=new PageCrawler();

	}
	
	public MySqlHandler getHandler() {
		return handler;
	}



	public void setHandler(MySqlHandler handler) {
		this.handler = handler;
	}



	public PageCrawler getCrawler() {
		return crawler;
	}



	public void setCrawler(PageCrawler crawler) {
		this.crawler = crawler;
	}



	public static DBCrawler startBuild() {
		DBCrawler dbCrawler=new DBCrawler();
		
		return dbCrawler;
	}
	
	
	public <T> DBCrawler setMappingConfig(Class<T> t) {
		config=new MappingConfig();
		config.setMappingClazz(t);
		handler.setConfig(config);
		return this;
	}
	
	public DBCrawler setTable(String table) {
		datasource.setTable(table);
		handler.setDatasource(datasource);
		return this;
	}
	
	public DBCrawler fromJDBCPropertieFile(String path,String file,boolean isRes) {
		ConnectionProperties mysqlProp=ConnectionProperties.loadPropertiesFromPropetiesFile(path, file, isRes);
		datasource=(MysqlDataSource)factory.getDataSourceByType(DataSourceType.mysql(), mysqlProp);
		return this;
	}
	
	public DBCrawler setConfigurationColumn(String colName) {
		this.colName=colName;
		
		return this;
	}
	
	public DBCrawler setConfigurationField(String fieldName) {
		this.fieldName=fieldName;
		return this;
	}
	
	public DBCrawler setIdName(String name) {
		this.idCol=name;
		return this;
	}
	
	public DBCrawler setId(String value) {
		this.value=value;
		return this;
	}
	
	public DBCrawler build() {
		handler.initialize();
		Object o=handler.getOne(this.idCol+"='"+value+"'");
		if(this.fieldName==null || this.fieldName.equals("")) {
			this.fieldName=this.colName;
		}
		String getMethodName="get"+fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);
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
	
	public Object get(int i){
		try {

			Node node=nodeList.item(i);
			String cnt=W3CNodeUtil.getInnerHTML(node);
			PageSelector selector=new PageSelector();
			selector.setClz(Class.forName(this.clz));
			selector.setCont(cnt);
			selector.initialize();
			return selector.buildObject(binding);
		}catch(Exception e) {
			return null;
		}
	}
	
	public int getCnt() {
		return nodeList.getLength();
	}
	
	private void initialize() {
		HtmlCleaner cleaner = new HtmlCleaner();
		try {
			TagNode node = cleaner.clean(new ByteArrayInputStream(this.listCont.getBytes()));
			this.doc = new DomSerializer(new CleanerProperties()).createDOM(node);
			//System.out.println(W3CNodeUtil.getInnerHTML(doc));
			this.nodeList = XPathAPI.selectNodeList(doc, this.listPath);
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void loadCont(String listUrl) {
		this.listCont=HtmlFetcher.FetchFromUrl(listUrl);
		initialize();
	}
	
	public void loadHtmlCnt(String cont) {
		this.listCont=cont;
		initialize();
	}
	
	
	

}
