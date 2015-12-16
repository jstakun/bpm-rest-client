package org.jbpm.simple.rest.client;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.KieInternalServices;
import org.kie.internal.process.CorrelationAwareProcessRuntime;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.process.CorrelationKeyFactory;
import org.kie.remote.client.api.RemoteRestRuntimeEngineFactory;
import com.redhat.waw.bpm.NumberPortRequest;

public class TelcoRestClient implements NumberPortabilityCommons {

	public static void main( String[] args ) throws MalformedURLException
    {
		RuntimeEngine engine = RemoteRestRuntimeEngineFactory.newBuilder()
                .addDeploymentId(deploymentId)
                .addUrl(new URL(appUrl))
                .addUserName(user)
                .addPassword(password)
                .addTimeout(60)
                .build();
        
        Map<String, Object> params = new HashMap<String, Object>();      
        NumberPortRequest request = new NumberPortRequest(0, BigInteger.valueOf(123457890l), "Jan", "Nowak", new Date(System.currentTimeMillis()), "new", new Date(System.currentTimeMillis() + (28 * 24 * 60 * 60 * 1000l)));
        params.put("request", request);
        
        CorrelationKeyFactory factory = KieInternalServices.Factory.get().newCorrelationKeyFactory();
        System.out.println("Generating process correlation key: " + request.toString());
        CorrelationKey key = factory.newCorrelationKey(request.toString());
        KieSession ksession = engine.getKieSession();
        // start process instance
        ProcessInstance pinst = ((CorrelationAwareProcessRuntime)ksession).startProcess("NumberPortability.MobilePortOutProcess", key, params);
        
        // next the process instance can be found be correlation key: 
        
        System.out.println("Started instance " + pinst.getId() + ": " + pinst.getProcessId());
        //System.out.println("Current process instance : " + pinst.toString());
    }   
	
}
