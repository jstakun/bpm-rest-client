package org.jbpm.simple.rest.client;

import java.net.MalformedURLException;
import java.net.URL;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.KieInternalServices;
import org.kie.internal.process.CorrelationAwareProcessRuntime;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.process.CorrelationKeyFactory;
import org.kie.internal.runtime.manager.context.CorrelationKeyContext;
import org.kie.remote.client.api.RemoteRestRuntimeEngineFactory;

public class TelcoRestSendMessage {
	public static void main( String[] args ) throws MalformedURLException
    {
        //curl -v -u admin -X POST 'http://localhost:8080/business-central/rest/runtime/com.redhat.waw.bpm:NumberPortability:LATEST/process/instance/274/signal?signal=Message-PingMessage&event=value'
		
		final String deploymentId = "com.redhat.waw.bpm:NumberPortability:LATEST";
        final URL appUrl = new URL("http://localhost:8080/business-central/");
        final String user = "jstakun";
        final String password = "manager1!";
        final long processId = 274;
        
        //"TerminationCompletedSignal", "NetworkCancelSignal", "CustomerCancelSignal", "ChangePortDateSignal", "AuditorReviewSignal", "Message-PingMessage"
        
        //final String signalName = "ChangePortDateSignal";
        //final String signalData = "{message=\"2014-10-18\"}";
        
        //final String signalName = "TerminationCompletedSignal";
        //final String signalData = "{message=\"ok\"}";
        
        final String signalName = "Message-PingMessage";
        final String signalData = "{message=\"Ping Message\"}";
            
        RuntimeEngine engine = RemoteRestRuntimeEngineFactory.newBuilder()
                .addDeploymentId(deploymentId)
                .addUrl(appUrl)
                .addUserName(user)
                .addPassword(password)
                .addTimeout(60)
                .build();
        
        KieSession ksession = engine.getKieSession();
        ksession.signalEvent(signalName, signalData, processId);
        
        
        //PerProcessInstanceRuntimeManager
        
        //String correlationKeyStr = "com.redhat.waw.bpm.NumberPortRequest@202b723c"; 
        //CorrelationKeyFactory factory = KieInternalServices.Factory.get().newCorrelationKeyFactory();
        //CorrelationKey correlationKey = factory.newCorrelationKey(correlationKeyStr);
        
        //RuntimeEnvironment environment, SessionFactory factory, TaskServiceFactory taskServiceFactory, String identifier     
        //PerProcessInstanceRuntimeManager runtimeManager = new PerProcessInstanceRuntimeManager(deploymentId, appUrl, user, password);
        
        //RuntimeEngine runtime = ((PerProcessInstanceRuntimeManager)engine).getRuntimeEngine(CorrelationKeyContext.get(correlationKey));             
              
        
        //ProcessInstance processInstance = ((CorrelationAwareProcessRuntime)ksession).getProcessInstance(correlationKey);
        
        //ProcessInstance processInstance = ksession.getProcessInstance(processId);
        
        //if (processInstance != null) {
        //		processInstance.signalEvent(signalName, signalData);
	    //    	System.out.println("Signal " + signalName + " sent to process " + processId);
        //} else {
        //		System.out.println("No process instance found with correlation key: " + correlationKeyStr);
        //}
        
    }   
}
;