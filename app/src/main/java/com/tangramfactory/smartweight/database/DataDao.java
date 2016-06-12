//package com.tangramfactory.smartweight.database;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//
//import com.tangramfactory.smartweight.R;
//import com.tangramfactory.smartweight.database.listener.DataOperationListener;
//import com.tangramfactory.smartweight.database.listener.DataOperationListenerGetObject;
//import com.tangramfactory.smartweight.utility.DebugLogger;
//
//import org.json.JSONArray;
//
//import java.util.ArrayList;
//
//public class DataDao {
//
//	private DatabaseHelper db;
//
//	private volatile static DataDao instance;
//
//	private Context mContext;
//
//	private DataDao(Context context) {
//		mContext = context;
//		try {
//			db = DatabaseHelper.getInstance(mContext);
//		} catch (Exception e) {
//		}
//	}
//
//	public static final DataDao getInstance(Context context) {
//		if (instance == null) {
//			synchronized (DataDao.class) {
//				if (instance == null) {
//					instance = new DataDao(context);
//				}
//			}
//		}
//		return instance;
//	}
//
//	public void close() {
//		db.close();
//	}
//
//	public void closeCursor(Cursor cursor) {
//		if (cursor != null && !cursor.isClosed()) {
//			cursor.close();
//		}
//	}
//
//	public void getIntervalDataList(final DataOperationListenerGetObject dataOperationListener) {
////		new Thread(new Runnable() {
////			@Override
////			public void run() {
////				Cursor cursor = null;
////				ArrayList<IntervalDataVo> dataList = new ArrayList<IntervalDataVo>();
////				try {
////					String query = mContext.getString(R.string.getIntervalDataList);
////					cursor = db.get(query);
////					cursor.moveToFirst();
////					int numRows = cursor.getCount();
////					IntervalDataVo intervalDataVo;
////					for (int i = 0; i < numRows; i++) {
////						intervalDataVo = new IntervalDataVo();
////						intervalDataVo.setTimeStamp(cursor.getLong((cursor.getColumnIndex("timeStamp"))));
////						intervalDataVo.setGoalCount(cursor.getInt((cursor.getColumnIndex("goalCount"))));
////						intervalDataVo.setGoalSet(cursor.getInt((cursor.getColumnIndex("goalSet"))));
////						intervalDataVo.setLevel(cursor.getInt((cursor.getColumnIndex("level"))));
////						intervalDataVo.setSuccess(cursor.getInt((cursor.getColumnIndex("success"))) == 0 ? false : true);
////						intervalDataVo.setIsSencond(cursor.getInt((cursor.getColumnIndex("isSecond"))) == 0 ? false : true);
////						dataList.add(intervalDataVo);
////						cursor.moveToNext();
////					}
////					db.logCursorInfo(cursor);
////					dataOperationListener.done(dataList);
////				} catch (Exception exception) {
////					exception.printStackTrace();
////					DebugLogger.e(PathConstants.BASE_TAG, exception.getLocalizedMessage(), exception);
////					dataOperationListener.error(exception);
////				} finally {
////					closeCursor(cursor);
////				}
////			}
////		}).start();
//	}
//
////	public ArrayList<IntervalDataVo> getIntervalDataList() {
////		Cursor cursor = null;
////		ArrayList<IntervalDataVo> dataList = new ArrayList<IntervalDataVo>();
////		try {
////			String query = mContext.getString(R.string.getIntervalDataList);
////			cursor = db.get(query);
////			cursor.moveToFirst();
////			int numRows = cursor.getCount();
////			IntervalDataVo intervalDataVo;
////			for (int i = 0; i < numRows; i++) {
////				intervalDataVo = new IntervalDataVo();
////				intervalDataVo.setTimeStamp(cursor.getLong((cursor.getColumnIndex("timeStamp"))));
////				intervalDataVo.setGoalCount(cursor.getInt((cursor.getColumnIndex("goalCount"))));
////				intervalDataVo.setGoalSet(cursor.getInt((cursor.getColumnIndex("goalSet"))));
////				intervalDataVo.setLevel(cursor.getInt((cursor.getColumnIndex("level"))));
////				intervalDataVo.setSuccess(cursor.getInt((cursor.getColumnIndex("success"))) == 0 ? false : true);
////				intervalDataVo.setIsSencond(cursor.getInt((cursor.getColumnIndex("isSecond"))) == 0 ? false : true);
////				dataList.add(intervalDataVo);
////				cursor.moveToNext();
////			}
////			db.logCursorInfo(cursor);
////		} catch (Exception exception) {
////			exception.printStackTrace();
////			DebugLogger.e(PathConstants.BASE_TAG, exception.getLocalizedMessage(), exception);
////		} finally {
////			closeCursor(cursor);
////		}
////		return dataList;
////	}
//
//	public int getIntervalDataListCount() {
//		Cursor cursor = null;
//		int numRows = 0;
//		int count = 0;
//		try {
//			String query = mContext.getString(R.string.getIntervalDataListCount);
//			cursor = db.get(query);
//			cursor.moveToFirst();
//			numRows = cursor.getCount();
//			if (numRows > 0) {
//				count = cursor.getInt((cursor.getColumnIndex("IntervalCount")));
//			}
//			db.logCursorInfo(cursor);
//		} catch (Exception exception) {
//			DebugLogger.e(PathConstants.BASE_TAG, exception.getLocalizedMessage(), exception);
//		} finally {
//			closeCursor(cursor);
//		}
//		return count;
//	}
//
////	public int getIntervalTodayCount() {
////		Cursor cursor = null;
////		int numRows = 0;
////		int count = 0;
////		try {
////			String query = mContext.getString(R.string.getIntervalTodayCount);
////			cursor = db.get(query);
////			cursor.moveToFirst();
////			numRows = cursor.getCount();
////			if (numRows > 0) {
////				count = cursor.getInt((cursor.getColumnIndex("IntervalCount")));
////			}
////			db.logCursorInfo(cursor);
////		} catch (Exception exception) {
////			DebugLogger.e(PathConstants.BASE_TAG, exception.getLocalizedMessage(), exception);
////		} finally {
////			closeCursor(cursor);
////		}
////		return count;
////	}
//
//	public int getRopeTodayCount() {
//		Cursor cursor = null;
//		int numRows = 0;
//		int count = 0;
//		try {
//			String query = mContext.getString(R.string.getRopeTodayCount);
//			cursor = db.get(query);
//			cursor.moveToFirst();
//			numRows = cursor.getCount();
//			if (numRows > 0) {
//				count = cursor.getInt((cursor.getColumnIndex("RopeCount")));
//			}
//			db.logCursorInfo(cursor);
//		} catch (Exception exception) {
//			DebugLogger.e(PathConstants.BASE_TAG, exception.getLocalizedMessage(), exception);
//		} finally {
//			closeCursor(cursor);
//		}
//		return count;
//	}
//	public void getIntervalDataListCount(final DataOperationListenerGetObject dataOperationListener) {
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				Cursor cursor = null;
//				int numRows = 0;
//				int count = 0;
//				try {
//					String query = mContext.getString(R.string.getIntervalDataListCount);
//					cursor = db.get(query);
//					cursor.moveToFirst();
//					numRows = cursor.getCount();
//					if (numRows > 0) {
//						count = cursor.getInt((cursor.getColumnIndex("IntervalCount")));
//					}
//					db.logCursorInfo(cursor);
//					dataOperationListener.done(count);
//				} catch (Exception exception) {
//					DebugLogger.e(PathConstants.BASE_TAG, exception.getLocalizedMessage(), exception);
//					dataOperationListener.error(exception);
//				} finally {
//					closeCursor(cursor);
//				}
//			}
//		}).start();
//	}
//
//	public void getIntervalMainDataCount(final DataOperationListenerGetObject dataOperationListener) {
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				Cursor cursor = null;
//				int numRows = 0;
//				int count = 0;
//				try {
//					String query = mContext.getString(R.string.getIntervalMainDataCount, "%Y", "%m", "%d", DateTimeFormat.forPattern("yyyy-MM-dd")
//							.print(DateTime.now()));
//					cursor = db.get(query);
//					cursor.moveToFirst();
//					DebugLogger.d(PathConstants.BASE_TAG, query);
//					numRows = cursor.getCount();
//					if (numRows > 0) {
//						count = cursor.getInt(cursor.getColumnIndex("IntervalCount"));
//					}
//					db.logCursorInfo(cursor);
//					dataOperationListener.done(count);
//				} catch (Exception exception) {
//					DebugLogger.e(PathConstants.BASE_TAG, exception.getLocalizedMessage(), exception);
//					dataOperationListener.error(exception);
//				} finally {
//					closeCursor(cursor);
//				}
//			}
//		}).start();
//	}
//
//	public void getIntervalData(final String date, final DataOperationListenerGetObject dataOperationListener) {
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				Cursor cursor = null;
//				try {
//					String query = mContext.getString(R.string.getIntervalDataListByDate, "%Y", "%m", "%d", date);
//					DebugLogger.d(PathConstants.BASE_TAG, query);
//					cursor = db.get(query);
//					cursor.moveToFirst();
//					int numRows = cursor.getCount();
//					ArrayList<IntervalDataVo> intervalDataList = new ArrayList<IntervalDataVo>();
//					IntervalDataVo intervalDataVo = new IntervalDataVo();
//					for (int i = 0; i < numRows; i++) {
//						intervalDataVo = new IntervalDataVo();
//						intervalDataVo.setTimeStamp(cursor.getLong((cursor.getColumnIndex("timeStamp"))));
//						intervalDataVo.setGoalCount(cursor.getInt((cursor.getColumnIndex("goalCount"))));
//						intervalDataVo.setGoalSet(cursor.getInt((cursor.getColumnIndex("goalSet"))));
//						intervalDataVo.setLevel(cursor.getInt((cursor.getColumnIndex("level"))));
//						intervalDataVo.setSuccess(cursor.getInt((cursor.getColumnIndex("success"))) == 0 ? false : true);
//						intervalDataVo.setCreateDate(cursor.getString((cursor.getColumnIndex("createDate"))));
//						intervalDataList.add(intervalDataVo);
//						cursor.moveToNext();
//					}
//					db.logCursorInfo(cursor);
//					dataOperationListener.done(intervalDataList);
//				} catch (Exception exception) {
//					exception.printStackTrace();
//					DebugLogger.e(PathConstants.BASE_TAG, exception.getLocalizedMessage(), exception);
//					dataOperationListener.error(exception);
//				} finally {
//					closeCursor(cursor);
//				}
//			}
//		}).start();
//	}
//
//	public JSONArray getRopeDataByInterval(final long intervalTimeStamp) {
//		Cursor cursor = null;
//		JSONArray jsonArray = new JSONArray();
//		try {
//			String query = mContext.getString(R.string.getRopeDataByInterval, intervalTimeStamp);
//			cursor = db.get(query);
//			cursor.moveToFirst();
//			int numRows = cursor.getCount();
//			for (int i = 0; i < numRows; i++) {
//				jsonArray.put(cursor.getLong((cursor.getColumnIndex("timeStamp"))));
//				cursor.moveToNext();
//			}
//		} catch (Exception exception) {
//			DebugLogger.e(PathConstants.BASE_TAG, exception.getLocalizedMessage(), exception);
//		} finally {
//			closeCursor(cursor);
//		}
//		return jsonArray;
//	}
//
//	public void getIntervalRopeData(final long intervalTimeStamp, final DataOperationListenerGetObject dataOperationListener) {
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				Cursor cursor = null;
//				RopeDailyData ropeDailyData = new RopeDailyData();
//				try {
//					String query = mContext.getString(R.string.getIntervalRopeData, intervalTimeStamp);
//					cursor = db.get(query);
//					cursor.moveToFirst();
//					int numRows = cursor.getCount();
//					if (numRows > 0) {
//						ropeDailyData.jumps = cursor.getInt((cursor.getColumnIndex("jumps")));
//						ropeDailyData.calorie = cursor.getFloat((cursor.getColumnIndex("calorie")));
//						ropeDailyData.duration = cursor.getLong((cursor.getColumnIndex("duration")));
//					}
//					db.logCursorInfo(cursor);
//					dataOperationListener.done(ropeDailyData);
//				} catch (Exception exception) {
//					exception.printStackTrace();
//					DebugLogger.e(PathConstants.BASE_TAG, exception.getLocalizedMessage(), exception);
//					dataOperationListener.error(exception);
//				} finally {
//					closeCursor(cursor);
//				}
//			}
//		}).start();
//	}
//
//	public void getIntervalLevelData(final long intervalTimeStamp, final DataOperationListenerGetObject dataOperationListener) {
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				Cursor cursor = null;
//				ArrayList<Integer> jumps = new ArrayList<Integer>();
//				try {
//					String query = mContext.getString(R.string.getIntervalLevelData, intervalTimeStamp);
//					cursor = db.get(query);
//					cursor.moveToFirst();
//					int numRows = cursor.getCount();
//					for (int i = 0; i < numRows; i++) {
//						jumps.add(cursor.getInt(cursor.getColumnIndex("jumps")));
//						cursor.moveToNext();
//					}
//					db.logCursorInfo(cursor);
//					dataOperationListener.done(jumps);
//				} catch (Exception exception) {
//					exception.printStackTrace();
//					DebugLogger.e(PathConstants.BASE_TAG, exception.getLocalizedMessage(), exception);
//					dataOperationListener.error(exception);
//				} finally {
//					closeCursor(cursor);
//				}
//			}
//		}).start();
//	}
//
//	public void getRopeDataList(final DataOperationListenerGetObject dataOperationListener) {
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				Cursor cursor = null;
//				ArrayList<RopeReportData> ropeCountVoList = new ArrayList<RopeReportData>();
//				RopeReportData ropeReportData;
//				try {
//					String query = mContext.getString(R.string.getRopeDataList);
//					cursor = db.get(query);
//					cursor.moveToFirst();
//					int numRows = cursor.getCount();
//					for (int i = 0; i < numRows; i++) {
//						ropeReportData = new RopeReportData();
//						ropeReportData.setTimeStamp(cursor.getLong((cursor.getColumnIndex("timeStamp"))));
//						ropeReportData.setStartTimeUTC(cursor.getString((cursor.getColumnIndex("startTime"))));
//						ropeReportData.setEndTimeUTC(cursor.getString((cursor.getColumnIndex("endTime"))));
//						ropeReportData.setCalorie(cursor.getFloat((cursor.getColumnIndex("calorie"))));
//						ropeReportData.setDuration(cursor.getLong((cursor.getColumnIndex("duration"))));
//						ropeReportData.setRpm(cursor.getInt((cursor.getColumnIndex("rpm"))));
//						ropeReportData.setCount(cursor.getInt((cursor.getColumnIndex("count"))));
//						ropeCountVoList.add(ropeReportData);
//						cursor.moveToNext();
//					}
//
//					dataOperationListener.done(ropeCountVoList);
//				} catch (Exception exception) {
//					DebugLogger.e(PathConstants.BASE_TAG, exception.getLocalizedMessage(), exception);
//					dataOperationListener.error(exception);
//				} finally {
//					closeCursor(cursor);
//				}
//			}
//		}).start();
//	}
//
//	public void insertRopeCount(RopeCountVo ropeCountVo, DataOperationListener dataOperationListener) {
//		db.insert("RopeReport", ropeCountVo.getContentValues());
//		if (dataOperationListener != null)
//			dataOperationListener.done();
//	}
//
//	public void insertRopeCountList(ArrayList<RopeCountVo> list) {
//		for (RopeCountVo ropeCountVo : list) {
//			db.insert("RopeReport", ropeCountVo.getContentValues());
//		}
//	}
//
//	public void insertIntervalData(IntervalDataVo intervalDataVo) {
//		db.insert("Interval", intervalDataVo.getContentValues());
//	}
//
//	public void insertIntervalDataForSync(ArrayList<IntervalDataVo> list, DataOperationListener dataOperationListener) {
//		for (IntervalDataVo intervalDataVo : list) {
//			db.insert("Interval", intervalDataVo.getContentValues());
//			ContentValues contentValues = new ContentValues();
//			contentValues.put("intervalTimeStamp", intervalDataVo.getTimeStamp());
//			JSONArray timeStampList = intervalDataVo.getRopeDataTimeStampList();
//			for (int i = 0; i < timeStampList.length(); i++) {
//				String timeStamp = null;
//				try {
//					timeStamp = String.valueOf(timeStampList.get(i));
//				} catch (Exception e) {
//					DebugLogger.e(PathConstants.BASE_TAG, e.getLocalizedMessage(), e);
//				}
//				db.update("RopeReport", contentValues, "timeStamp=" + timeStamp);
//			}
//		}
//		if (dataOperationListener != null)
//			dataOperationListener.done();
//	}
//
//	public void deleteIntervalData(IntervalDataVo intervalDataVo) {
//		try {
//			db.delete("Interval", "timeStamp=" + String.valueOf(intervalDataVo.getTimeStamp()));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	public void deleteRopeReport() {
//		try {
//			db.delete("RopeReport", "");
//			db.delete("Interval", "");
//			db.delete("UnderArmour", "");
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	public void updateRopeDataForInterval(long intervalTimeStamp) {
//		ContentValues contentValues = new ContentValues();
//		contentValues.put("intervalTimeStamp", "");
//		db.update("RopeReport", contentValues, "intervalTimeStamp=" + String.valueOf(intervalTimeStamp));
//	}
//
//	public void tempUpdateInterval() {
//		ContentValues contentValues = new ContentValues();
//		contentValues.put("createDate", DateConvertUtils.convertDateToString(DateTime.now().minusDays(1).toDate()));
//		db.update("Interval", contentValues, "");
//	}
//
//	public void insertRopeDate(ArrayList<RopeReportData> list, DataOperationListener dataOperationListener) {
//		for (RopeReportData ropeReportData : list) {
//			db.insert("RopeReport", ropeReportData.getContentValues());
//		}
//		if (dataOperationListener != null)
//			dataOperationListener.done();
//	}
//
//	public void getDailyData(final String date, final float recommandCount, final DataOperationListenerGetObject dataOperationListener) {
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				Cursor cursor = null;
//				RopeDailyData ropeDailyData = new RopeDailyData();
//				try {
//					String query = mContext.getString(R.string.getDailyData, "%Y", "%m", "%d", recommandCount, date);
//					cursor = db.get(query);
//					cursor.moveToFirst();
//					int numRows = cursor.getCount();
//					if (numRows > 0) {
//						ropeDailyData.jumps = cursor.getInt((cursor.getColumnIndex("jumps")));
//						ropeDailyData.percent = cursor.getFloat((cursor.getColumnIndex("percent")));
//						ropeDailyData.calorie = cursor.getFloat((cursor.getColumnIndex("calorie")));
//						ropeDailyData.duration = cursor.getLong((cursor.getColumnIndex("duration")));
//					}
//					db.logCursorInfo(cursor);
//					dataOperationListener.done(ropeDailyData);
//				} catch (Exception exception) {
//					exception.printStackTrace();
//					DebugLogger.e(PathConstants.BASE_TAG, exception.getLocalizedMessage(), exception);
//					dataOperationListener.error(exception);
//				} finally {
//					closeCursor(cursor);
//				}
//			}
//		}).start();
//	}
//
//	public void getRopeDataTotalCount(final DataOperationListenerGetObject dataOperationListener) {
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				Cursor cursor = null;
//				try {
//					String query = mContext.getString(R.string.getRopeDataTotalCount);
//					cursor = db.get(query);
//					cursor.moveToFirst();
//					int numRows = cursor.getCount();
//					int totalCount = 0;
//					if (numRows > 0) {
//						totalCount = cursor.getInt((cursor.getColumnIndex("totalCount")));
//					}
//					db.logCursorInfo(cursor);
//					dataOperationListener.done(totalCount);
//				} catch (Exception exception) {
//					exception.printStackTrace();
//					DebugLogger.e(PathConstants.BASE_TAG, exception.getLocalizedMessage(), exception);
//					dataOperationListener.error(exception);
//				} finally {
//					closeCursor(cursor);
//				}
//			}
//		}).start();
//	}
//
//	public int getRopeDataTotalCount() {
//		Cursor cursor = null;
//		int totalCount = 0;
//		try {
//			String query = mContext.getString(R.string.getRopeDataTotalCount);
//			cursor = db.get(query);
//			cursor.moveToFirst();
//			int numRows = cursor.getCount();
//			if (numRows > 0) {
//				totalCount = cursor.getInt((cursor.getColumnIndex("totalCount")));
//			}
//			db.logCursorInfo(cursor);
//		} catch (Exception exception) {
//			exception.printStackTrace();
//			DebugLogger.e(PathConstants.BASE_TAG, exception.getLocalizedMessage(), exception);
//		} finally {
//			closeCursor(cursor);
//		}
//
//		return totalCount;
//	}
//
//	public void getRopeDataLast(final float recommandCount, final DataOperationListenerGetObject dataOperationListener) {
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				Cursor cursor = null;
//				RopeDailyData ropeDailyData = new RopeDailyData();
//				try {
//					String query = mContext.getString(R.string.getRopeDataLast, "%Y", "%m", "%d", recommandCount);
//					DebugLogger.d(PathConstants.BASE_TAG, query);
//					cursor = db.get(query);
//					cursor.moveToFirst();
//					int numRows = cursor.getCount();
//					if (numRows > 0) {
//						ropeDailyData.jumps = cursor.getInt((cursor.getColumnIndex("jumps")));
//						ropeDailyData.percent = cursor.getFloat((cursor.getColumnIndex("percent")));
//						ropeDailyData.calorie = cursor.getFloat((cursor.getColumnIndex("calorie")));
//						ropeDailyData.duration = cursor.getLong((cursor.getColumnIndex("duration")));
//					} else {
//						ropeDailyData = null;
//					}
//					db.logCursorInfo(cursor);
//					dataOperationListener.done(ropeDailyData);
//				} catch (Exception exception) {
//					exception.printStackTrace();
//					DebugLogger.e(PathConstants.BASE_TAG, exception.getLocalizedMessage(), exception);
//					dataOperationListener.error(exception);
//				} finally {
//					closeCursor(cursor);
//				}
//			}
//		}).start();
//	}
//
//	public void getRopeDataToday(final float recommandCount, final DataOperationListenerGetObject dataOperationListener) {
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				Cursor cursor = null;
//				RopeDailyData ropeDailyData = new RopeDailyData();
//				try {
//					String query = mContext.getString(
//							R.string.getRopeDataToday,
//							"%Y",
//							"%m",
//							"%d",
//							recommandCount,
//							DateTimeFormat.forPattern("yyyy-MM-dd").print(DateTime.now()));
//					DebugLogger.d(PathConstants.BASE_TAG, query);
//					cursor = db.get(query);
//					cursor.moveToFirst();
//					int numRows = cursor.getCount();
//					if (numRows > 0) {
//						ropeDailyData.jumps = cursor.getInt((cursor.getColumnIndex("jumps")));
//						ropeDailyData.percent = cursor.getFloat((cursor.getColumnIndex("percent")));
//						ropeDailyData.calorie = cursor.getFloat((cursor.getColumnIndex("calorie")));
//						ropeDailyData.duration = cursor.getLong((cursor.getColumnIndex("duration")));
//					} else {
//						ropeDailyData = null;
//					}
//					db.logCursorInfo(cursor);
//					dataOperationListener.done(ropeDailyData);
//				} catch (Exception exception) {
//					exception.printStackTrace();
//					DebugLogger.e(PathConstants.BASE_TAG, exception.getLocalizedMessage(), exception);
//					dataOperationListener.error(exception);
//				} finally {
//					closeCursor(cursor);
//				}
//			}
//		}).start();
//	}
//
//	public void getWeekGraphDataList(final float recommandCount, final String date, final DataOperationListenerGetObject dataOperationListener) {
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				Cursor cursor = null;
//				RopeGraphData ropeGraphData = new RopeGraphData();
//				float values[][] = new float[2][7];
//				try {
//					String query = mContext.getString(R.string.getWeekGraphDataList, recommandCount, "%w", date);
//					DebugLogger.d(PathConstants.BASE_TAG, query);
//					cursor = db.get(query);
//					cursor.moveToFirst();
//					int numRows = cursor.getCount();
//					float percent = 0;
//					int week = 0;
//					for (int i = 0; i < numRows; i++) {
//						percent = cursor.getFloat((cursor.getColumnIndex("percent")));
//						week = cursor.getInt(cursor.getColumnIndex("week"));
//						values[0][week] = percent > 100 ? 100 : percent;
//						values[1][week] = percent > 100 ? percent - 100 : 0;
//						cursor.moveToNext();
//					}
//					db.logCursorInfo(cursor);
//					ropeGraphData.values = values;
//					ropeGraphData.countHistoryData = numRows;
//					dataOperationListener.done(ropeGraphData);
//				} catch (Exception exception) {
//					exception.printStackTrace();
//					DebugLogger.e(PathConstants.BASE_TAG, exception.getLocalizedMessage(), exception);
//					dataOperationListener.error(exception);
//					;
//				} finally {
//					closeCursor(cursor);
//				}
//			}
//		}).start();
//	}
//
//	public void getWeekTotalData(final String date, final DataOperationListenerGetObject dataOperationListener) {
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				Cursor cursor = null;
//				RopeTotalData ropeTotalData = new RopeTotalData();
//				try {
//					String query = mContext.getString(R.string.getWeekTotalData, date);
//					DebugLogger.d(PathConstants.BASE_TAG, query);
//					cursor = db.get(query);
//					cursor.moveToFirst();
//					ropeTotalData.totalCount = cursor.getInt((cursor.getColumnIndex("TotalCount")));
//					ropeTotalData.totalCalorie = cursor.getFloat((cursor.getColumnIndex("TotalCalorie")));
//					ropeTotalData.totalDuration = cursor.getLong((cursor.getColumnIndex("TotalDuration")));
//
//					ropeTotalData.averageCount = cursor.getInt((cursor.getColumnIndex("AverageCount")));
//					ropeTotalData.averageCalorie = cursor.getFloat((cursor.getColumnIndex("AverageCalorie")));
//					ropeTotalData.averageDuration = cursor.getLong((cursor.getColumnIndex("AverageDuration")));
//					ropeTotalData.averageRPM = cursor.getInt((cursor.getColumnIndex("AverageRPM")));
//					db.logCursorInfo(cursor);
//					dataOperationListener.done(ropeTotalData);
//				} catch (Exception exception) {
//					exception.printStackTrace();
//					DebugLogger.e(PathConstants.BASE_TAG, exception.getLocalizedMessage(), exception);
//					dataOperationListener.error(exception);
//				} finally {
//					closeCursor(cursor);
//				}
//			}
//		}).start();
//	}
//
//	// public void getTargetLogCount(final Long reportFacilitiesId,
//	// DataOperationListenerGetList dataOperationListenerGetList) {
//	// Cursor cursor = null;
//	// try {
//	// String query = mContext.getString(R.string.getWeekGraphDataList, 3300f,
//	// "2015-08");
//	// cursor = db.get(query);
//	// cursor.moveToFirst();
//	// List<Object> targetLogCountList = new ArrayList<Object>();
//	// targetLogCountList.add((Long)
//	// cursor.getLong(cursor.getColumnIndex("facilitiesCheckLogCount")));
//	// targetLogCountList.add((Long)
//	// cursor.getLong(cursor.getColumnIndex("facilitiesMeasurementLogCount")));
//	// dataOperationListenerGetList.done(targetLogCountList);
//	// } catch (Exception exception) {
//	// exception.printStackTrace();
//	// dataOperationListenerGetList.error(exception);
//	// } finally {
//	// closeCursor(cursor);
//	// }
//	// }
//
//	// %1$s %Y
//	// %2$s %m
//	// %3$s %d
//	// %4$s %W
//	// %5$f 3300.0
//	// %6$s 2015-07
//	public void getMonthGraphDataList(final float recommandCount, final String date, final DataOperationListenerGetObject dataOperationListener) {
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				Cursor cursor = null;
//				RopeGraphData ropeGraphData = new RopeGraphData();
//				float values[][] = new float[2][5];
//				try {
//					String query = mContext.getString(R.string.getMonthGraphDataList, "%Y", "%m", "%d", "%W", recommandCount, date);
//					DebugLogger.d(PathConstants.BASE_TAG, query);
//					cursor = db.get(query);
//					cursor.moveToFirst();
//					int numRows = cursor.getCount();
//					float percent = 0;
//					int week = -1;
//					String columDate;
//					// int initWeek = 0;
//					// if (numRows > 0)
//					// initWeek = cursor.getInt(cursor.getColumnIndex("week"));
//
//					for (int i = 0; i < numRows; i++) {
//						percent = cursor.getFloat((cursor.getColumnIndex("percent")));
//						columDate = cursor.getString(cursor.getColumnIndex("date"));
//
//						int day = DateConvertUtils.dayFromYYYYMMDD(columDate);
//						int paramMonth = DateConvertUtils.monthFromYYYYMMDD(date);
//						int month = DateConvertUtils.monthFromYYYYMMDD(columDate);
//						if(day >= 1 && day <=7 && paramMonth == month)									week = 0;
//						else if(day >= 8 && day <=14)													week = 1;
//						else if(day >= 15 && day <=21)													week = 2;
//						else if(day >= 22 && day <=28)													week = 3;
//						else if(((day >= 29) || (day >= 1 &&  day <= 4) && paramMonth != month))		week = 4;
//
//						values[0][week] = percent > 100 ? 100 : percent;
//						values[1][week] = percent > 100 ? percent - 100 : 0;
//						cursor.moveToNext();
//					}
//					db.logCursorInfo(cursor);
//					ropeGraphData.values = values;
//					ropeGraphData.countHistoryData = numRows;
//					dataOperationListener.done(ropeGraphData);
//				} catch (Exception exception) {
//					exception.printStackTrace();
//					DebugLogger.e(PathConstants.BASE_TAG, exception.getLocalizedMessage(), exception);
//					dataOperationListener.error(exception);
//				} finally {
//					closeCursor(cursor);
//				}
//			}
//		}).start();
//	}
//
//	public void getMonthTotalData(final String date, final DataOperationListenerGetObject dataOperationListener) {
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				Cursor cursor = null;
//				RopeTotalData ropeTotalData = new RopeTotalData();
//				try {
//					String query = mContext.getString(R.string.getMonthTotalData, "%Y", "%m", "%d", date);
//					DebugLogger.d(PathConstants.BASE_TAG, query);
//					cursor = db.get(query);
//					cursor.moveToFirst();
//					ropeTotalData.totalCount = cursor.getInt((cursor.getColumnIndex("TotalCount")));
//					ropeTotalData.totalCalorie = cursor.getFloat((cursor.getColumnIndex("TotalCalorie")));
//					ropeTotalData.totalDuration = cursor.getLong((cursor.getColumnIndex("TotalDuration")));
//
//					ropeTotalData.averageCount = cursor.getInt((cursor.getColumnIndex("AverageCount")));
//					ropeTotalData.averageCalorie = cursor.getFloat((cursor.getColumnIndex("AverageCalorie")));
//					ropeTotalData.averageDuration = cursor.getLong((cursor.getColumnIndex("AverageDuration")));
//					ropeTotalData.averageRPM = cursor.getInt((cursor.getColumnIndex("AverageRPM")));
//					db.logCursorInfo(cursor);
//					dataOperationListener.done(ropeTotalData);
//				} catch (Exception exception) {
//					exception.printStackTrace();
//					DebugLogger.e(PathConstants.BASE_TAG, exception.getLocalizedMessage(), exception);
//					dataOperationListener.error(exception);
//				} finally {
//					closeCursor(cursor);
//				}
//			}
//		}).start();
//	}
//
//	// %1$s %Y
//	// %2$s %m
//	// %3$s 2014
//	// %4$f 3300.0
//	public void getYearGraphDataList(final float recommandCount, final String date, final DataOperationListenerGetObject dataOperationListener) {
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				Cursor cursor = null;
//				RopeGraphData ropeGraphData = new RopeGraphData();
//				float values[][] = new float[2][12];
//				try {
//					String query = mContext.getString(R.string.getYearGraphDataList, "%Y", "%m", date, recommandCount);
//					DebugLogger.d(PathConstants.BASE_TAG, query);
//					cursor = db.get(query);
//					cursor.moveToFirst();
//					int numRows = cursor.getCount();
//					float percent = 0;
//					int month = 0;
//
//					for (int i = 0; i < numRows; i++) {
//						percent = cursor.getFloat((cursor.getColumnIndex("percent")));
//						month = cursor.getInt(cursor.getColumnIndex("month")) - 1;
//						values[0][month] = percent > 100 ? 100 : percent;
//						values[1][month] = percent > 100 ? percent - 100 : 0;
//						cursor.moveToNext();
//					}
//					db.logCursorInfo(cursor);
//					ropeGraphData.values = values;
//					ropeGraphData.countHistoryData = numRows;
//					dataOperationListener.done(ropeGraphData);
//				} catch (Exception exception) {
//					exception.printStackTrace();
//					DebugLogger.e(PathConstants.BASE_TAG, exception.getLocalizedMessage(), exception);
//					dataOperationListener.error(exception);
//					;
//				} finally {
//					closeCursor(cursor);
//				}
//			}
//		}).start();
//	}
//
//	public void getYearTotalData(final String date, final DataOperationListenerGetObject dataOperationListener) {
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				Cursor cursor = null;
//				RopeTotalData ropeTotalData = new RopeTotalData();
//				try {
//					String query = mContext.getString(R.string.getYearTotalData, "%Y", "%m", "%d", date);
//					DebugLogger.d(PathConstants.BASE_TAG, query);
//					cursor = db.get(query);
//					cursor.moveToFirst();
//					ropeTotalData.totalCount = cursor.getInt((cursor.getColumnIndex("TotalCount")));
//					ropeTotalData.totalCalorie = cursor.getFloat((cursor.getColumnIndex("TotalCalorie")));
//					ropeTotalData.totalDuration = cursor.getLong((cursor.getColumnIndex("TotalDuration")));
//
//					ropeTotalData.averageCount = cursor.getInt((cursor.getColumnIndex("AverageCount")));
//					ropeTotalData.averageCalorie = cursor.getFloat((cursor.getColumnIndex("AverageCalorie")));
//					ropeTotalData.averageDuration = cursor.getLong((cursor.getColumnIndex("AverageDuration")));
//					ropeTotalData.averageRPM = cursor.getInt((cursor.getColumnIndex("AverageRPM")));
//					db.logCursorInfo(cursor);
//					dataOperationListener.done(ropeTotalData);
//				} catch (Exception exception) {
//					exception.printStackTrace();
//					DebugLogger.e(PathConstants.BASE_TAG, exception.getLocalizedMessage(), exception);
//					dataOperationListener.error(exception);
//				} finally {
//					closeCursor(cursor);
//				}
//			}
//		}).start();
//	}
//
//	public void deleteWorkoutData(WorkoutDataVo workoutDataVo) {
//		try {
//			db.delete("WorkoutData", "id=" + workoutDataVo.getId());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	public void insertWorkoutData(WorkoutDataVo workoutDataVo, DataOperationListener dataOperationListener) {
//		db.insert("WorkoutData", workoutDataVo.getContentValues());
//		if(null != dataOperationListener) {
//			dataOperationListener.done();
//		}
//	}
//
//	public void getWorkoutData(final DataOperationListenerGetObject dataOperationListener) {
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				Cursor cursor = null;
//				ArrayList<WorkoutDataVo> dataList = new ArrayList<WorkoutDataVo>();
//				try {
//					String query = mContext.getString(R.string.getWorkoutDataList);
//					cursor = db.get(query);
//					cursor.moveToFirst();
//					int numRows = cursor.getCount();
//					WorkoutDataVo workoutDataVo;
//					for (int i = 0; i < numRows; i++) {
//						workoutDataVo = new WorkoutDataVo();
//						workoutDataVo.setId(cursor.getInt((cursor.getColumnIndex("id"))));
//						workoutDataVo.setStartTime(cursor.getLong((cursor.getColumnIndex("startTime"))));
//						workoutDataVo.setCalorie(cursor.getDouble((cursor.getColumnIndex("calorie"))));
//						workoutDataVo.setDuration(cursor.getLong((cursor.getColumnIndex("duration"))));
//						workoutDataVo.setEndTime(cursor.getLong((cursor.getColumnIndex("endTime"))));
//						workoutDataVo.setCount(cursor.getInt((cursor.getColumnIndex("count"))));
//						workoutDataVo.setSource(cursor.getString((cursor.getColumnIndex("source"))));
//						dataList.add(workoutDataVo);
//						cursor.moveToNext();
//					}
//					db.logCursorInfo(cursor);
//					dataOperationListener.done(dataList);
//				} catch (Exception exception) {
//					exception.printStackTrace();
//					DebugLogger.e(PathConstants.BASE_TAG, exception.getLocalizedMessage(), exception);
//					dataOperationListener.error(exception);
//				} finally {
//					closeCursor(cursor);
//				}
//			}
//		}).start();
//	}
//
//	public void dummyInsertRopeCount() {
//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				RopeCountVo ropeCountVo;
//				DateTime now = DateTime.now();
//				for (int i = 0; i < 1000; i++) {
//					ropeCountVo = new RopeCountVo();
//					int randomDuration;
//					long randomDay;
//					randomDuration = ((int) (Math.random() * 1000 * 60 * 10)) + 1000 * 60;
//					randomDay = (long) (Math.random() * 1000 * 60 * 60 * 24 * 30 * 13) + randomDuration;
//
//					// int randomDuration = ((int) (Math.random() * 1000 * 60 *
//					// 10)) + 1000 * 60;
//					// long randomDay = (long) (Math.random() * 1000 * 60 * 60 *
//					// 24 * 7) + randomDuration;
//
//					DateTime startDate = new DateTime(now.getMillis() - randomDay);
//					DateTime endDate = new DateTime(startDate.getMillis() + randomDuration);
//
//					int rpm = ((int) (Math.random() * 200)) + 50;
//					int count = (int) ((randomDuration / 1000f / 60f) * 0.75f * rpm);
//
//					double calorie = SmartGymUtility.calculatorCalorieRPMTime(rpm, randomDuration) * count;
//					ropeCountVo.setTimeStamp(endDate.toDate().getTime());
//					ropeCountVo.setStartTime(startDate.toDate());
//					ropeCountVo.setEndTime(endDate.toDate());
//					ropeCountVo.setCalorie(calorie);
//					ropeCountVo.setCount(count);
//					ropeCountVo.setDuration(randomDuration);
//					ropeCountVo.setRpm(rpm);
//					insertRopeCount(ropeCountVo, null);
//				}
//				DataExport.dataExport();
//			}
//		}).start();
//	}
//
//	// private void insertReportFacilitiesCheckLog(String facilitiesId, Long
//	// reportFacilitiesId) {
//	// try {
//	// db.exec(mContext.getString(R.string.insertReportFacilitiesCheckLog), new
//	// String[] {
//	// String.valueOf(reportFacilitiesId), facilitiesId
//	// });
//	// } catch (Exception exception) {
//	// exception.printStackTrace();
//	// }
//	// }
//	//
//	//
//	// public void getTargetLogCount(final Long reportFacilitiesId,
//	// DataOperationListenerGetList dataOperationListenerGetList) {
//	// Cursor cursor = null;
//	// try {
//	// cursor = db.get(mContext.getString(R.string.getTargetLogCount), new
//	// String[] {
//	// String.valueOf(reportFacilitiesId)
//	// });
//	// cursor.moveToFirst();
//	// List<Object> targetLogCountList = new ArrayList<Object>();
//	// targetLogCountList.add((Long)
//	// cursor.getLong(cursor.getColumnIndex("facilitiesCheckLogCount")));
//	// targetLogCountList.add((Long)
//	// cursor.getLong(cursor.getColumnIndex("facilitiesMeasurementLogCount")));
//	// dataOperationListenerGetList.done(targetLogCountList);
//	// } catch (Exception exception) {
//	// exception.printStackTrace();
//	// dataOperationListenerGetList.error(exception);
//	// } finally {
//	// closeCursor(cursor);
//	// }
//	// }
//	//
//	//
//	//
//	// public void updateReportFacilitiesIsMeasurementLogToSendServer(Long
//	// reportFacilitiesId, boolean isMeasurementLogToSendServer) {
//	// ContentValues values = new ContentValues();
//	// values.put("isMeasurementLogToSendServer", isMeasurementLogToSendServer ?
//	// 1 : 0);
//	// db.update("ReportFacilities", values, "reportFacilitiesId = " +
//	// reportFacilitiesId);
//	// }
//
//}
