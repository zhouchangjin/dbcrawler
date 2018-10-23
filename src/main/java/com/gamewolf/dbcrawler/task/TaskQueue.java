package com.gamewolf.dbcrawler.task;

import com.gamewolf.dbcrawler.model.base.Task;

public interface TaskQueue {
	
	public void addTask(Task task);
	
	public Task getTask();

}
