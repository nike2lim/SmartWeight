package com.tangramfactory.smartweight.database.listener;


public interface DataOperationListenerGetObject {
	public void done(Object object);
	public void error(Exception exception);
}
