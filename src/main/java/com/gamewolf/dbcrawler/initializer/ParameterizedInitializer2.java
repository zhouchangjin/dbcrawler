package com.gamewolf.dbcrawler.initializer;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.gamewolf.database.dbsource.DataSourceFactory;
import com.gamewolf.database.handler.init.SqliteInitializer;
import com.gamewolf.dbcrawler.base.BaseDBCrawler;
import com.gamewolf.dbcrawler.base.IDatabaseCrawler;
import com.gamewolf.util.lang.IntegerParser;
import com.harmonywisdom.crawler.annotation.PageCrawlerDBSetting;

public class ParameterizedInitializer2 extends SqliteInitializer implements IParameterInitializer {
	
	public static DataSourceFactory factory = new DataSourceFactory();
	public IParameterSet params;

	@Override
	public void init(String[] args) {
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
		initSqliteHandler();
		dbCrawlerAware();

	}

	private void dbCrawlerAware() {
		Field f[] = this.getClass().getDeclaredFields();
		for (Field field : f) {
			if (field.getType().equals(IDatabaseCrawler.class) && field.isAnnotationPresent(PageCrawlerDBSetting.class)) {
				PageCrawlerDBSetting setting = field.getAnnotation(PageCrawlerDBSetting.class);
				
				IDatabaseCrawler crawler=BaseDBCrawler.startBuild()
						.fromJDBCPropertieFile(
								setting.propertiePath(), 
								setting.propertieFile(), 
								setting.isResource())
						.setTable(setting.table())
						.setIdName(setting.idField())
						.setId(setting.value())
						.setConfigurationColumn(setting.colName())
						.setMappingConfig(setting.javaClass()).build();
				try {
					Method m = this.getClass().getMethod(
							"set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1),
							IDatabaseCrawler.class);
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

	@Override
	public int getPage() {
		if (params.containsKey("page")) {
			return params.getIntegerValue("page");
		} else {
			return 0;
		}
	}

}
