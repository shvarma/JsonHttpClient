package com;

import java.io.File;
import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jayway.jsonpath.JsonPath;


/**
 * Simple class to launch a jenkins build on run@Cloud platform, should also work on every jenkins instance (not tested)
 *
 */
public class TestPreemptive {

	public static void main(String[] args) throws JSONException {
		// Credentials
//		String username = "admin";
//		String password = "Desire@2014";
		
		String username = "harishs";
		String password = "Desire@2014";


		// Jenkins url
//		String jenkinsUrl = "http://localhost:8080/";
		String jenkinsUrl = "http://192.168.168.207:8080/";

		// Build name
//		String jobName = "JOB";
		String jobName = "01_ET";


		// Build token
//		String buildToken = "FIRST_BUILD";
		String buildToken = "ET1";

		// Create your httpclient
		DefaultHttpClient client = new DefaultHttpClient();
		DefaultHttpClient client1 = new DefaultHttpClient();

		// Then provide the right credentials
		client.getCredentialsProvider().setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
				new UsernamePasswordCredentials(username, password));

		// Generate BASIC scheme object and stick it to the execution context
		BasicScheme basicAuth = new BasicScheme();
		BasicHttpContext context = new BasicHttpContext();
		BasicHttpContext context1 = new BasicHttpContext();
		context.setAttribute("preemptive-auth", basicAuth);
		context1.setAttribute("preemptive-auth", basicAuth);

		// Add as the first (because of the zero) request interceptor
		// It will first intercept the request and preemptively initialize the authentication scheme if there is not
		client.addRequestInterceptor(new PreemptiveAuth(), 0);
		
		String getUrl = "";
		
		// To create job
//		getUrl = jenkinsUrl +  "http://localhost:8080/createItem?name="JOB";
		
		// To copy job and create job
//		getUrl = jenkinsUrl +  "http://localhost:8080/createItem?name="JOB1"&mode=copy&from="JOB"";
		
		// To fetch all jobs, views etc
//		getUrl = jenkinsUrl + "api/json?pretty=true";

		
		// You get request that will start the build
		getUrl = jenkinsUrl + "/job/" + jobName + "/build?token=" + buildToken;
		
		HttpGet get = new HttpGet(getUrl);
		HttpGet get1 = new HttpGet(getUrl);
//		get.setHeader("Content-type","application/json");
		get.setHeader("Content-type","application/xml");
		get1.setHeader("Content-type","application/xml");

		try {
			// Execute your request with the given context
			HttpResponse response = client.execute(get, context);
			@SuppressWarnings("unused")
			HttpResponse response1 = client1.execute(get1, context1);
			HttpEntity entity = response.getEntity();			
			String jsonString = EntityUtils.toString(entity);
			System.out.println("JSON reponse: \n" + jsonString);
		    JSONObject jsonObject = new JSONObject(jsonString);
//		    System.out.println("JSON Array: \n" + jsonObject.get("jobs"));
//		    JSONArray jsonJobsArray = new JSONArray(jsonObject.get("jobs").toString());
//		    JSONArray jsonJobsArray = jsonObject.getJSONArray("jobs");
//		    System.out.println("Job 1 Object: \n" + jsonJobsArray.get(0));


		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static class PreemptiveAuth implements HttpRequestInterceptor {

		/*
		 * (non-Javadoc)
		 *
		 * @see org.apache.http.HttpRequestInterceptor#process(org.apache.http.HttpRequest,
		 * org.apache.http.protocol.HttpContext)
		 */
		public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
			// Get the AuthState
			AuthState authState = (AuthState) context.getAttribute(ClientContext.TARGET_AUTH_STATE);

			// If no auth scheme available yet, try to initialize it preemptively
			if (authState.getAuthScheme() == null) {
				AuthScheme authScheme = (AuthScheme) context.getAttribute("preemptive-auth");
				CredentialsProvider credsProvider = (CredentialsProvider) context
						.getAttribute(ClientContext.CREDS_PROVIDER);
				HttpHost targetHost = (HttpHost) context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
				if (authScheme != null) {
					Credentials creds = credsProvider.getCredentials(new AuthScope(targetHost.getHostName(), targetHost
							.getPort()));
					if (creds == null) {
						throw new HttpException("No credentials for preemptive authentication");
					}
					authState.setAuthScheme(authScheme);
					authState.setCredentials(creds);
				}
			}
		}
	}
}
