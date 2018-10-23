package com.gamewolf.dbcrawler.initializer;
import java.util.HashMap;
import java.util.Iterator;

public class ParameterSet implements IParameterSet {

	HashMap<String,Object> map;
	@Override
	public String toString() {
		String out="";
		Iterator<String> it=map.keySet().iterator();
		while(it.hasNext())
		{
			String paramName=it.next();
			out+=paramName+"="+map.get(paramName)+" ";
		}
		return out;
	}
	public ParameterSet()
	{
		map=new HashMap<String,Object>();
	}
	@Override
	public Object getValue(String paramName) {
		// TODO Auto-generated method stub
		return map.get(paramName);
	}

	@Override
	public void setValue(String paramName, Object value) {
		// TODO Auto-generated method stub
           map.put(paramName, value);
	}

	@Override
	public String getStringValue(String paramName) {
		// TODO Auto-generated method stub
		return (String) map.get(paramName);
	}

	@Override
	public void setStringValue(String paramName, String value) {
		// TODO Auto-generated method stub
		map.put(paramName, value);
	}

	@Override
	public Integer getIntegerValue(String paramName) {
		// TODO Auto-generated method stub
		return (Integer) map.get(paramName);
	}

	@Override
	public void setIntegerValue(String paramName, Integer value) {
		// TODO Auto-generated method stub
		map.put(paramName, value);
	}

	@Override
	public Double getDoubleValue(String paramName) {
		// TODO Auto-generated method stub
		return (Double) map.get(paramName);
	}

	@Override
	public void setDoubleValue(String paramName, Double value) {
		// TODO Auto-generated method stub
		map.put(paramName, value);
		
	}

	@Override
	public Iterator getParameterIterator() {
		// TODO Auto-generated method stub
		return map.keySet().iterator();
	}

	@Override
	public int getParameterCount() {
		// TODO Auto-generated method stub
		return map.keySet().size();
	}
	@Override
	public boolean containsKey(String name) {
		// TODO Auto-generated method stub
		return map.containsKey(name);
	}

}
