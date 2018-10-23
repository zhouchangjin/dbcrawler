package com.gamewolf.dbcrawler.initializer;

import java.util.Iterator;

public interface IParameterSet {
	
	public Object getValue(String paramName);
	public void setValue(String paramName,Object value);
	public String getStringValue(String paramName);
	public void setStringValue(String paramName,String value);
	public Integer getIntegerValue(String paramName);
	public void setIntegerValue(String paramName,Integer value);
	public Double getDoubleValue(String paramName);
	public void setDoubleValue(String paramName,Double value);
	public Iterator getParameterIterator();
	public int getParameterCount();
	
	public boolean containsKey(String name);


}
