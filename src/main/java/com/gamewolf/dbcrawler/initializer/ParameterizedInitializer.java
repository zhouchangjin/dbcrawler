package com.gamewolf.dbcrawler.initializer;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.gamewolf.database.dbconnector.ConnectionProperties;
import com.gamewolf.database.dbmeta.DataSourceType;
import com.gamewolf.database.dbsource.DataSourceFactory;
import com.gamewolf.database.dbsource.ElasticSearchDataSource;
import com.gamewolf.database.dbsource.MongoDBDataSource;
import com.gamewolf.database.dbsource.MysqlDataSource;
import com.gamewolf.database.handler.ElasticSearchSourceHandler;
import com.gamewolf.database.handler.MappingConfig;
import com.gamewolf.database.handler.MongoHandler;
import com.gamewolf.database.handler.MySqlHandler;
import com.gamewolf.database.orm.annotation.DefaultESTableBinding;
import com.gamewolf.database.orm.annotation.MongoHandlerConfig;
import com.gamewolf.database.orm.annotation.MysqlTableBinding;
import com.gamewolf.database.orm.annotation.PropertyConfiguration;
import com.gamewolf.dbcrawler.base.DBCrawler;
import com.gamewolf.util.lang.IntegerParser;
import com.harmonywisdom.crawler.annotation.PageCrawlerDBSetting;
import com.harmonywisdom.crawler.annotation.PageCrawlerSetting;
import com.harmonywisdom.crawler.init.CrawlerInitializer;
import com.harmonywisdom.crawler.page.JSONObjectBinding;
import com.harmonywisdom.crawler.page.JSONPageCrawler;
import com.harmonywisdom.crawler.page.ObjectPageBingding;
import com.harmonywisdom.crawler.page.PageCrawler;

public class ParameterizedInitializer implements IParameterInitializer {

	public static DataSourceFactory factory = new DataSourceFactory();
	public IParameterSet params;

	void databaseAware() {

		Field f[] = this.getClass().getDeclaredFields();
		for (Field field : f) {
			if (field.getType().equals(MySqlHandler.class) && field.isAnnotationPresent(MysqlTableBinding.class)) {
				MysqlTableBinding binding = field.getAnnotation(MysqlTableBinding.class);
				Class t = binding.javaClass();
				MappingConfig config = new MappingConfig();
				config.setMappingClazz(t);
				String table = binding.table();
				String propP = binding.propertiePath();
				String prop = binding.propertieFile();
				boolean isRes = binding.isResource();

				ConnectionProperties mysqlProp = ConnectionProperties.loadPropertiesFromPropetiesFile(propP, prop,
						isRes);
				MysqlDataSource datasource = (MysqlDataSource) factory.getDataSourceByType(DataSourceType.mysql(),
						mysqlProp);
				datasource.setTable(table);
				MySqlHandler mysqlHandler = new MySqlHandler();
				mysqlHandler.setConfig(config);
				mysqlHandler.setDatasource(datasource);
				mysqlHandler.initialize();
				try {
					field.set(this, mysqlHandler);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					continue;
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					continue;
				}
			} else if (field.getType().equals(MongoHandler.class)
					&& field.isAnnotationPresent(MongoHandlerConfig.class)) {
				MongoHandlerConfig config = field.getAnnotation(MongoHandlerConfig.class);
				String collection = config.collection();
				boolean isResource = config.isResource();
				String fileName = config.propertieFile();
				String filePath = config.propertiePath();
				ConnectionProperties cp = ConnectionProperties.loadPropertiesFromPropetiesFile(filePath, fileName,
						isResource);
				MongoDBDataSource dataSource = (MongoDBDataSource) factory.getDataSourceByType(DataSourceType.mongo(),
						cp);
				dataSource.setCollection(collection);
				MongoHandler handler = new MongoHandler();
				handler.setDatasource(dataSource);
				handler.initialize();

				try {
					field.set(this, handler);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					continue;
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					continue;
				}
			} else if (field.getType().equals(ElasticSearchSourceHandler.class)
					&& field.isAnnotationPresent(DefaultESTableBinding.class)) {
				DefaultESTableBinding esConfig = field.getAnnotation(DefaultESTableBinding.class);
				PropertyConfiguration propConf = DefaultESTableBinding.class.getAnnotation(PropertyConfiguration.class);

				String propfile = propConf.propertieFile();
				String propPath = propConf.propertiePath();
				boolean isRes = propConf.isResource();
				String esType = esConfig.type();
				ConnectionProperties cp = ConnectionProperties.loadPropertiesFromPropetiesFile(propPath, propfile,
						isRes);
				ElasticSearchDataSource dataSource = (ElasticSearchDataSource) factory
						.getDataSourceByType(DataSourceType.elasticSearch(), cp);
				dataSource.setTypeName(esType);
				ElasticSearchSourceHandler handler = new ElasticSearchSourceHandler();
				handler.setDatasource(dataSource);
				handler.initialize();
				try {
					field.setAccessible(true);
					field.set(this, handler);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					continue;
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					continue;
				}

			}
		}

	}

	void crawleraware() {

		Field f[] = this.getClass().getFields();
		for (Field field : f) {
			if (field.getType().equals(PageCrawler.class) && field.isAnnotationPresent(PageCrawlerSetting.class)) {
				PageCrawlerSetting setting = field.getAnnotation(PageCrawlerSetting.class);
				String xmlPath = setting.xmlPath();
				String xmlfile = setting.xmlFile();
				boolean isRes = setting.isResource();
				PageCrawler crawler = new PageCrawler();
				if (isRes) {

					String path = CrawlerInitializer.class.getClassLoader().getResource(xmlPath + "/" + xmlfile)
							.getFile();
					ObjectPageBingding binding = ObjectPageBingding.buildFromXML(path);
					crawler.setBinding(binding);

				} else {

					String path = setting.xmlPath() + "/" + setting.xmlFile();
					ObjectPageBingding binding = ObjectPageBingding.buildFromXML(path);
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
			} else if (field.getType().equals(JSONPageCrawler.class)
					&& field.isAnnotationPresent(PageCrawlerSetting.class)) {

				PageCrawlerSetting setting = field.getAnnotation(PageCrawlerSetting.class);
				String xmlPath = setting.xmlPath();
				String xmlfile = setting.xmlFile();
				boolean isRes = setting.isResource();

				JSONPageCrawler crawler = new JSONPageCrawler();
				String path = "";
				if (isRes) {
					path = CrawlerInitializer.class.getClassLoader().getResource(xmlPath + "/" + xmlfile).getFile();

				} else {
					path = setting.xmlPath() + "/" + setting.xmlFile();
				}
				JSONObjectBinding binding = JSONObjectBinding.buildFromXML(path);
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

	@Override
	public void init(String[] args) {
		// TODO Auto-generated method stub
		params = new ParameterSet();
		for (String s : args) {
			if (s.startsWith("--") && s.contains("=")) {
				String parts[] = s.split("=");
				String name = parts[0].replace("--", "");
				String value = parts[1];

				if (IntegerParser.isPosInt(value)) {
					params.setIntegerValue(name, IntegerParser.parsePositiveInt(value));
				} else {
					params.setStringValue(name, value);
				}

			}
		}

		databaseAware();
		crawleraware();
		dbCrawlerAware();
	}

	@Override
	public int getPage() {
		// TODO Auto-generated method stub
		if (params.containsKey("page")) {
			return params.getIntegerValue("page");
		} else {
			return 0;
		}

	}

	public void dbCrawlerAware() {

		Field f[] = this.getClass().getDeclaredFields();
		for (Field field : f) {
			if (field.getType().equals(DBCrawler.class) && field.isAnnotationPresent(PageCrawlerDBSetting.class)) {
				PageCrawlerDBSetting setting = field.getAnnotation(PageCrawlerDBSetting.class);
				DBCrawler crawler = DBCrawler.startBuild()
						.fromJDBCPropertieFile(setting.propertiePath(), setting.propertieFile(), setting.isResource())
						.setTable(setting.table()).setMappingConfig(setting.javaClass()).setIdName(setting.idField())
						.setConfigurationColumn(setting.colName()).setId(setting.value()).build();

				try {
					Method m = this.getClass().getMethod(
							"set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1),
							DBCrawler.class);
					m.invoke(this, crawler);

				} catch (NoSuchMethodException e) {

					e.printStackTrace();
					continue;
				} catch (SecurityException e) {
					e.printStackTrace();
					continue;
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					continue;
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					continue;
				} catch (InvocationTargetException e) {
					e.printStackTrace();
					continue;
				}

			}
		}

	}

}
