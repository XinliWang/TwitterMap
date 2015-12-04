package com.aws.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.omg.CORBA.Request;

import com.aws.db.TwitterDao;
import com.aws.twitter.TwitterGet;

import twitter4j.JSONException;
import twitter4j.JSONObject;

/**
 *  The class could start to fetch the tweets
 */

public class TwitterFetchServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private TwitterGet twitterGet = TwitterGet.getInstance();
	private TwitterDao twitterDAO = TwitterDao.getInstance();
	
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException {
		String c = req.getParameter("crawl");
        //I use ajax,when open browser, we will execute it
		if("crawl".equals(c)){
			twitterGet.getTweets();
		}
		JSONObject json = new JSONObject();
		try {
			json.put("result", twitterDAO.getAllTweets());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		res.setContentType("application/json");
		res.getWriter().write(json.toString());
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}
