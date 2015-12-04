package com.aws.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aws.twitter.TwitterGet;

public class TwitterGetServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TwitterGet twitterGet = TwitterGet.getInstance();

	/**
	 * Use this servlet we will stop to get tweets.  It is usually called when we close the browser
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String s = request.getParameter("shutdown");
		if("shutdown".equals(s)){
			twitterGet.shutdownTwitterStream();
		}
		
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
