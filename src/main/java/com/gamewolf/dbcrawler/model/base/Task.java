package com.gamewolf.dbcrawler.model.base;

public class Task {
	
	String taskId;
	String taskPage;
	String taskType;
	
	
	public String getTaskType() {
		return taskType;
	}
	
	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}
	
	
	public String getTaskId() {
		return taskId;
	}
	
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	
	public String getTaskPage() {
		return taskPage;
	}
	
	public void setTaskPage(String taskPage) {
		this.taskPage = taskPage;
	}

}
