package com.tangramfactory.smartweight.database.listener;


public interface DataOperationListener {
	public void done();
	public void error(Exception exception);
}
