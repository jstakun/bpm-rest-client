package org.jbpm.simple.rest.client;

import java.net.MalformedURLException;
import java.net.URL;

import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.remote.client.api.RemoteRestRuntimeEngineFactory;

public class HRRestClient {
	public static void main( String[] args ) throws MalformedURLException
    {
     
		String deploymentId = "org.jbpm:HR:1.0";
        URL instanceUrl = new URL("http://localhost:8080/business-central/");
        String user = "admin";
        String password = "manager1!";
        
        RuntimeEngine engine = RemoteRestRuntimeEngineFactory.newBuilder()
                .addDeploymentId(deploymentId)
                .addUrl(instanceUrl)
                .addUserName(user)
                .addPassword(password)
                .addTimeout(60)
                .build();
        
        engine.getKieSession().startProcess("org.jbpm.humantask");
        
        engine.getTaskService().start(1, "jstakun");      
    }	
}
