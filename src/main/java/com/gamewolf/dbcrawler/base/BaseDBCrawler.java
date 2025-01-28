package com.gamewolf.dbcrawler.base;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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

import com.alibaba.fastjson.JSONObject;
import com.gamewolf.database.dbconnector.ConnectionProperties;
import com.gamewolf.database.dbsource.DataSourceFactory;
import com.gamewolf.database.dbsource.ITableDatasource;
import com.gamewolf.database.handler.DataSourceHanlderFactory;
import com.gamewolf.database.handler.IDatasourceHandler;
import com.gamewolf.database.handler.MappingConfig;
import com.gamewolf.util.datafile.XMLNode;
import com.gamewolf.util.datafile.XMLReader;
import com.harmonywisdom.crawler.httputil.HtmlFetcher;
import com.harmonywisdom.crawler.page.ObjectPageBingding;
import com.harmonywisdom.crawler.page.PageCrawler;
import com.harmonywisdom.crawler.page.W3CNodeUtil;

public class BaseDBCrawler implements IDatabaseCrawler{
	
	public static DataSourceFactory factory = new DataSourceFactory();
	
	IDatasourceHandler<?> datasourceHandler;
	
	PageCrawler crawler;
	
	MappingConfig config;
	
	ObjectPageBingding binding;
	
	Document doc;
	
	
	Class clz=null;
	
	String idCol;
	
	String value;
	
	String colField;
	
	String listPath=null;
	
	boolean isList=false;
	
	List<String> urlList;
	
	public BaseDBCrawler() {
		this.crawler=new PageCrawler();
		urlList=new ArrayList<String>();
	}
	
	public static BaseDBCrawler startBuild() {
		BaseDBCrawler dbCrawler=new BaseDBCrawler();
		return dbCrawler;
	}
	
	private Class getMappingClass() {
		if(this.clz==null) {
			this.clz=JSONObject.class;
		}
		return this.clz;
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
				crawler.setBinding(binding);
				this.listPath=xpath;
				this.isList=true;
				this.clz=Class.forName(className);
			}else {
				if(node.getAttribute("className")!=null) {
					String className=node.getAttribute("className").toString();
					this.clz=Class.forName(className);
				}
				binding=ObjectPageBingding.buildFromXMLString(text);
				crawler.setBinding(binding);
			}
			
		} catch (Exception e) {
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

	@Override
	public void addPage(String url) {
		urlList.add(url);
	}

	@Override
	public List<Object> crawl() {
		if(isList) {
			List<String> allConts=new ArrayList<String>();
			for(String page:urlList) {
				List<String> contList=crawlPageObjects(page);
				allConts.addAll(contList);
			}
			return crawler.crawlByHtmlCont(getMappingClass(),
					allConts);

		}else {
			return crawler.crawlByUrls(getMappingClass(), this.urlList);
		}
	}

	private List<String> crawlPageObjects(String page) {
		HtmlCleaner cleaner = new HtmlCleaner();
		try {
			String listCont=HtmlFetcher.FetchFromUrl(page);
			TagNode tagnode = cleaner.clean(new ByteArrayInputStream(listCont.getBytes()));
			this.doc = new DomSerializer(new CleanerProperties()).createDOM(tagnode);
			NodeList nodeList = XPathAPI.selectNodeList(doc, this.listPath);
			List<String> htmlContList=new ArrayList<String>();
			for(int i=0;i<nodeList.getLength();i++) {
				Node node=nodeList.item(i);
				String cont=W3CNodeUtil.getInnerHTML(node);
				htmlContList.add(cont);
			}
			return htmlContList;
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<String>();
		} 
		
	}

}
