package com.gamewolf.dbcrawler.util;

import java.lang.reflect.Field;

import com.gamewolf.database.dbconnector.ConnectionProperties;
import com.gamewolf.database.dbmeta.DataSourceType;
import com.gamewolf.database.dbsource.DataSourceFactory;
import com.gamewolf.database.dbsource.MysqlDataSource;
import com.gamewolf.database.handler.MappingConfig;
import com.gamewolf.database.handler.MySqlHandler;
import com.gamewolf.database.orm.annotation.MysqlTableBinding;

public class AutoAwareUtil {
	public static DataSourceFactory factory = new DataSourceFactory();
	
	public static void autoawireMysql(Object obj) {
		Field f[]=obj.getClass().getDeclaredFields();
		for(int i=0;i<f.length;i++) {
			Field field=f[i];
			if(field.getType().equals(MySqlHandler.class) && field.isAnnotationPresent(MysqlTableBinding.class)) {
				MysqlTableBinding binding=field.getAnnotation(MysqlTableBinding.class);
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
					field.set(obj, mysqlHandler);
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

}
