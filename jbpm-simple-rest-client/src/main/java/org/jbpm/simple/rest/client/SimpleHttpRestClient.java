package org.jbpm.simple.rest.client;

import java.io.IOException;
import java.net.URLEncoder;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;

public class SimpleHttpRestClient implements NumberPortabilityCommons {

	//curl -v -u admin -X POST 'http://localhost:8080/business-central/rest/runtime/com.redhat.waw.bpm:NumberPortability:LATEST/process/instance/274/signal?signal=Message-PingMessage&event=value'
	
	public static void main(String[] args) {
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			Credentials creds = new UsernamePasswordCredentials(user, password);
			
			//"TerminationCompletedSignal", "NetworkCancelSignal", "CustomerCancelSignal", "ChangePortDateSignal", "AuditorReviewSignal", "Message-PingMessage"
		    
		    //final String signalName = "ChangePortDateSignal";
		    //final String signalData = "{message=\"2014-10-18\"}";
		    
			long processId = 307;
			
			final String signalName = "Message-PingMessage"; //"AuditorReviewSignal"; //"TerminationCompletedSignal"; //"NetworkCancelSignal";  //
		    final String signalData = URLEncoder.encode("{message=\"hello world\"}", "UTF-8");	
		    
			String restUrl = appUrl + "rest/runtime/" + deploymentId + "/process/instance/" + processId + "/signal?signal=" + signalName + "&event=" + signalData;
			
			HttpPost httpPost = new HttpPost(restUrl);
			
			httpPost.addHeader(BasicScheme.authenticate(creds, "US-ASCII", false));
			
			HttpResponse response = httpClient.execute(httpPost);
			
			StatusLine status = response.getStatusLine();
			
			System.out.println("Response code: " + status.getStatusCode());
			
			HttpEntity entity = response.getEntity();
			
			System.out.println(IOUtils.toString(entity.getContent()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
