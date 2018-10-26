package com.gamewolf.dbcrawler.task;

import com.gamewolf.database.handler.MySqlHandler;
import com.gamewolf.database.orm.annotation.MysqlTableBinding;
import com.gamewolf.dbcrawler.model.base.Task;

public class DefaultMysqlTaskQueue implements TaskQueue{
	
    @MysqlTableBinding(javaClass = Task.class,table = "task")
    public MySqlHandler handler;

	@Override
	public void addTask(Task task) {
		// TODO Auto-generated method stub
		handler.insertObject(task);
	}

	@Override
	public Task getTask() {
		return null;
	}
	
	

}
