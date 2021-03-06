package com.aws.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.aws.twitter.Twitter;

import twitter4j.JSONArray;
import twitter4j.JSONException;
import twitter4j.JSONObject;

public class TwitterDao {
	private static final String TABLE_NAME = "twitter";
	private static final String INSERT_TO_TWITTER = "INSERT INTO " + TABLE_NAME + " VALUES(?,?,?,?,?,?,?)";
	private static final String GET_GEO_DATA = "SELECT latitude, longitude, category FROM " + TABLE_NAME;
	private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME
			+ "(twitterId LONG, username VARCHAR(50), latitude DOUBLE, longitude DOUBLE,"
			+ " content VARCHAR(200), timestamp LONG, category VARCHAR(20))";
	private Connection conn; 
	public TwitterDao() {
		try {
			conn = DatabaseUtil.getInstance().getConnection();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		} 
	}

	private static class Holder {
		private static final TwitterDao twitterDao = new TwitterDao();
	}

    //singleton pattern
	public static TwitterDao getInstance() {
		return Holder.twitterDao;
	}
	
	public boolean insert(Twitter twitter) throws SQLException {
		PreparedStatement statement = null;
		try {
			statement = conn.prepareStatement(INSERT_TO_TWITTER);
			statement.setLong(1, twitter.getTwitterID());
			statement.setString(2, twitter.getUsername());
			statement.setDouble(3, twitter.getLatitude());
			statement.setDouble(4, twitter.getLongitude());
			statement.setString(5, twitter.getContent());
			statement.setLong(6, twitter.getTimestamp());
			statement.setString(7, twitter.getCategory());
			statement.executeUpdate();
			return true;
		} finally {
			DatabaseUtil.getInstance().releaseStatement(statement);
		}
	}

    //get all json format data about all tweets
	public JSONArray getAllTweets() {
		//Connection conn = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		JSONArray locations = new JSONArray();
		try {
			//conn = getConnection();
			statement = conn.prepareStatement(GET_GEO_DATA);
			resultSet = statement.executeQuery();
			// store in JSON
			JSONObject twitterObject;
			while (resultSet.next()) {
				twitterObject = new JSONObject();
				twitterObject.put("lat", resultSet.getDouble("latitude"));
				twitterObject.put("lon", resultSet.getDouble("longitude"));
				twitterObject.put("category", resultSet.getString("category"));
				locations.put(twitterObject);
			}
			return locations;
		} catch (SQLException
				| JSONException e) {
			e.printStackTrace();
		} finally {
			releaseDatabase(statement, resultSet);
		}
		return locations;
	}

    //we will automatically check if there is table, if not then create them.
	public void checkAndCreateTable() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		Connection conn = getConnection();
		Statement statement = conn.createStatement();
		statement.executeUpdate(CREATE_TABLE);
	}

	public Connection getConnection()
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		return DatabaseUtil.getInstance().getConnection();
	}

    //we use this method to shutdown the database in order to stop insert operation
	private void releaseDatabase(PreparedStatement statement, ResultSet resultSet) {
		try {
			DatabaseUtil.getInstance().releaseResultSet(resultSet);
			DatabaseUtil.getInstance().releaseStatement(statement);
			//DatabaseUtil.getInstance().releaseConnection(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
