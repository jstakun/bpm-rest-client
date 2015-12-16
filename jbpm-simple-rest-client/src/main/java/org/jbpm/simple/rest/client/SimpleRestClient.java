package org.jbpm.simple.rest.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Content;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.kie.remote.client.api.RemoteRestRuntimeEngineFactory;


public class SimpleRestClient 
{
    public static void main( String[] args ) throws MalformedURLException
    {
        String deploymentId = "org.acme.insurance:policyquote:1.0.5-SNAPSHOT";
        URL instanceUrl = new URL("http://localhost:8080/business-central/");
        String user = "jstakun";
        String password = "manager1!";
        
        RuntimeEngine engine = RemoteRestRuntimeEngineFactory.newBuilder()
                .addDeploymentId(deploymentId)
                .addUrl(instanceUrl)
                .addUserName(user)
                .addPassword(password)
                .addTimeout(60)
                .build();
        
        KieSession ksession = engine.getKieSession();
        //ksession.addEventListener(arg0);
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("driverName", "john");
        params.put("age", 22);
        params.put("numberOfAccidents", 0);
        params.put("numberOfTickets", 1);
        params.put("vehicleYear", 2009);
        
        ProcessInstance pinst = ksession.startProcess("policyquote.policyquoteprocess", params);
        System.out.println("Started instance " + pinst.getId() + ": " + pinst.getProcessId());
        System.out.println("Current process instance : " + pinst.toString());
        //pinst.signalEvent("ExternalAuditor", "I'm watching you :)");
        
        TaskService taskService = engine.getTaskService();
        List<TaskSummary> tasksAssignedAsPotentialOwner = taskService.getTasksAssignedAsPotentialOwner(user, "en-UK"); 
        if (!tasksAssignedAsPotentialOwner.isEmpty()) {
        	for (TaskSummary ti : tasksAssignedAsPotentialOwner) {
        		System.out.println("Task " + ti.getId() + ": " + ti.getName());
        	}   	
        	
        	//read content
        	/*TaskSummary taskSummary = tasksAssignedAsPotentialOwner.get(0);
        	
        	Task techInterviewTask = taskService.getTaskById(taskSummary.getId());
        	
        	Content contentById = taskService.getContentById(techInterviewTask.getTaskData().getDocumentContentId());
        	
        	Map<String, Object> taskContent = (Map<String, Object>) ContentMarshallerHelper.unmarshall(contentById.getContent(), null);
        	
        	System.out.println(taskContent);*/
        	
        	//claim and complete
        	/*System.out.println("Claiming " + taskSummary.getId() + ": " + taskSummary.getName());
        	
        	taskService.claim(taskSummary.getId(), user);
        	
        	//taskService.start(taskSummary.getId(), user);
              
            Map<String, Object> response = new HashMap<String, Object>();
            response.put("price", "999");
            taskService.complete(taskSummary.getId(), user, response);*/
        	
        } else {
        	System.out.println("No tasks for user " + user);
        }
        
        Collection<ProcessInstance> processInstances = ksession.getProcessInstances();
        if (! processInstances.isEmpty()) {
        	for(ProcessInstance pi : processInstances){
        		System.out.println("Process Instance " + pi.getId() + ": " + pi.getProcessId());
        	}
        } else {
        	System.out.println("No active process instances in memory");
        }
    }   
}
