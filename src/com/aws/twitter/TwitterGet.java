package com.aws.twitter;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import com.aws.db.DatabaseUtil;
import com.aws.db.TwitterDao;

public final class TwitterGet {
	private static final String PROPERTIES_NAME = "config.properties";
	private static final String PROPERTIES_NOT_FOUND = "property file '" + PROPERTIES_NAME
			+ "' not found in the classpath";

	private static class Holder {
		private static final TwitterGet twitterGet = new TwitterGet();
	}

	public static TwitterGet getInstance() {
		return Holder.twitterGet;
	}

	private TwitterGet() {
		try {
			conn = TwitterDao.getInstance().getConnection();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	private final TwitterDao twitterDao = TwitterDao.getInstance();
	private TwitterStream twitterStream;

	private String oAuthConsumerKey;
	private String oAuthConsumerSecret;
	private String token;
	private String tokenSecret;
	private Connection conn;

    //the TwitterFetchServlet will call this method
	public void getTweets() {
        //check if database is exist
		checkTableExist();
        //get token of twitter Api
		getPropValues();

		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey(oAuthConsumerKey).setOAuthConsumerSecret(oAuthConsumerSecret)
				.setOAuthAccessToken(token).setOAuthAccessTokenSecret(tokenSecret);

		twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
		StatusListener listener = new StatusListener() {
			@Override
			public void onStatus(Status status) {
				if (status.getGeoLocation() != null) {
					String content = status.getText();
					String category = MatchKeyword.getkeyword(content);
					if (category != null) {
						System.out.println(status.getUser().getName() + " : " + status.getText());
						long twitterId = status.getUser().getId();
						String username = status.getUser().getScreenName();
						Double latitude = status.getGeoLocation().getLatitude();
						Double longitude = status.getGeoLocation().getLongitude();
						Long timestamp = status.getCreatedAt().getTime();
						Twitter twitter = new Twitter(twitterId, username, latitude, longitude, content, timestamp,category);
						try {
                            //insert tweets into databse
							twitterDao.insert(twitter);
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
			}

			@Override
			public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
			}

			@Override
			public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
			}

			@Override
			public void onScrubGeo(long userId, long upToStatusId) {
			}

			@Override
			public void onStallWarning(StallWarning warning) {
			}

			@Override
			public void onException(Exception ex) {
				ex.printStackTrace();
			}
		};
		twitterStream.addListener(listener);
		FilterQuery tweetFilterQuery = new FilterQuery();
		tweetFilterQuery.track(MatchKeyword.Keywords); // OR
		tweetFilterQuery.locations(new double[][] { { -180, -90 }, { 180, 90 } });
		twitterStream.filter(tweetFilterQuery);
	}

	public void shutdownTwitterStream() {
		System.out.println("twitter stream shut down");
		try {
			DatabaseUtil.getInstance().releaseConnection(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		twitterStream.shutdown();
	}

	private void getPropValues() {
		InputStream inputStream = null;
		try {
			Properties prop = new Properties();
			inputStream = getClass().getClassLoader().getResourceAsStream(PROPERTIES_NAME);
			if (inputStream != null) {
				prop.load(inputStream);
			} else {
				throw new FileNotFoundException(PROPERTIES_NOT_FOUND);
			}
			// get the property value
			oAuthConsumerKey = prop.getProperty("twitter_Key");
			oAuthConsumerSecret = prop.getProperty("twitter_secret");
			token = prop.getProperty("twitter_token");
			tokenSecret = prop.getProperty("twitter_token_secret");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void checkTableExist() {
		try {
			twitterDao.checkAndCreateTable();
		} catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}