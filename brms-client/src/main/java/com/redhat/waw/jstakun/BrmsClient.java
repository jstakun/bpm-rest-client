package com.redhat.waw.jstakun;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.drools.core.command.runtime.rule.AgendaGroupSetFocusCommand;
import org.drools.core.command.runtime.rule.GetObjectsCommand;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.ReleaseId;
import org.kie.api.command.BatchExecutionCommand;
import org.kie.api.command.Command;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ObjectFilter;
import org.kie.internal.command.CommandFactory;
import org.kie.internal.runtime.helper.BatchExecutionHelper;

import com.redhat.waw.finacial.creditdecision.CashCreditRequest;
import com.redhat.waw.finacial.creditdecision.Decision;

public class BrmsClient {

	//private static final String url = "http://localhost:8080/business-central/maven2/com/redhat/waw/finacial/CreditDecision/1.0/CreditDecision-1.0.jar";
	
	/*<batch-execution>
  		<retract fact-handle="fact-handle-id" />
	  </batch-execution>*/
	
	public static void main(String[] args) {
		new BrmsClient().execute("Simple");
		//new BrmsClient().execute("Complex");
	}
	
	public void execute(String type) {
		KieServices kieServices = KieServices.Factory.get();
		
		//kieServices.getResources().newUrlResource(url);
		
		//ReleaseId releaseId = kieServices.newReleaseId( "com.redhat.waw.financial", "CreditDecision", "1.2-SNAPSHOT" );
		ReleaseId releaseId = kieServices.newReleaseId( "com.redhat.waw.financial", "CashCreditScoring", "1.0.1-SNAPSHOT" );
		KieContainer kContainer = kieServices.newKieContainer(releaseId);
		
		//KieBaseConfiguration kbaseConf = kieServices.newKieBaseConfiguration();
        //kbaseConf.setProperty("drools.sequential", "true");
		//kbaseConf.setProperty("drools.phreakEnabled", "false");
		//KieBase kbase = kContainer.newKieBase(kbaseConf);
        //StatelessKieSession kSession = kbase.newStatelessKieSession();
		
		KieScanner kScanner = kieServices.newKieScanner(kContainer);
		// Start the KieScanner polling the Maven repository every 10 seconds
		//kScanner.start( 10000L );
		kScanner.scanNow();
		
		KieBase kbase = kContainer.getKieBase();
		Collection<KiePackage> packages = kbase.getKiePackages();
		for (KiePackage p : packages) {
			System.out.println(p.getName());
			Collection<Rule> rules = p.getRules();
			for (Rule r : rules) {
				System.out.println("\n-----------------\n");
				System.out.println(r.getId() + " " + r.getName() + " " + r.getNamespace() + " " + r.getPackageName());
				Map<String, Object> metadata = r.getMetaData();
				for (Map.Entry<String, Object> property : metadata.entrySet()) {
					System.out.println(property.getKey() + ": " + property.getValue());
				}				
			}
		}
		
		KieSession kSession = kContainer.newKieSession("state");//.newKieSession();   
		KieRuntimeLogger logger = KieServices.Factory.get().getLoggers().newFileLogger( kSession, "audit" ); 
		//kSession.getAgenda().getAgendaGroup(type).setFocus(); //Complex, Simple
		new Thread(new KieSessionRunner(kSession, logger, type)).start();   
	}
	
	private class KieSessionRunner implements Runnable {

		KieSession kSession = null;
		KieRuntimeLogger logger = null;
		String focusGroup = null;
		
		public KieSessionRunner(KieSession kSession, KieRuntimeLogger logger, String focusGroup) {
			this.kSession = kSession;
			this.logger = logger;
			this.focusGroup = focusGroup;
		}
		
		public void run() {
			System.out.println("Running session: " + kSession.getId());
			insertAndFireRules(kSession, focusGroup);
			
			/*ObjectFilter filter = new ObjectFilter() {
	            public boolean accept(Object object) {
	            	return object instanceof Decision;
	        }};
			
	        GetObjectsCommand getObjectsCommand = new GetObjectsCommand(filter, "decision");
			
	        List<Command<?>> cmds = new ArrayList<Command<?>>();
			cmds.add(getObjectsCommand);
			BatchExecutionCommand batch = CommandFactory.newBatchExecution(cmds);
			
			System.out.println("Executing batch:\n" + BatchExecutionHelper.newXStreamMarshaller().toXML(batch));
			
			ExecutionResults results = kSession.execute(batch);
			
			Collection<Object> objects = (Collection<Object>)results.getValue("decision");
			
			//Collection<Object> objects = (Collection<Object>)kSession.getObjects(filter);
	        
			for (Object o : objects) {
				printResponse(o);
			}*/
			
			System.out.println("Finished session: " + kSession.getId());
			logger.close();
			kSession.dispose();
		}
		
		private void insertAndFireRules(KieSession kSession, String focusGroup) {
			CashCreditRequest request = new CashCreditRequest(); 
			
			request.setAge((short)18);
			request.setAmount(1000d);
			request.setSalary(10000d);
			request.setId(1);
			
			//kSession.insert(request);
			
			/*CashCreditRequest request2 = new CashCreditRequest(); 
			
			request2.setAge((short)17);
			request2.setAmount(1000d);
			request2.setSalary(10000d);
			request2.setId(2);
			
			kSession.insert(request2);*/
			
			//kSession.fireAllRules();
			
			AgendaGroupSetFocusCommand agendaGroupSetFocusCommand = new AgendaGroupSetFocusCommand();
			agendaGroupSetFocusCommand.setName(focusGroup);
			
			Command insertObjectCommand = CommandFactory.newInsert(request, "request", false, null);//new InsertObjectCommand(request);
            //insertObjectCommand.setOutIdentifier("request");
            
			Command fireAllRulesCommand = CommandFactory.newFireAllRules();//new FireAllRulesCommand();
			
			ObjectFilter filter = new ObjectFilter() {
	            public boolean accept(Object object) {
	            	return object instanceof Decision;
	        }};
	        
	        Command getObjectsCommand = new GetObjectsCommand(filter, "decision"); //CommandFactory.newGetObjects(filter, "decision"); //
			
	        List<Command> cmds = new ArrayList<Command>();
	        cmds.add(agendaGroupSetFocusCommand);
			cmds.add(insertObjectCommand);
			cmds.add(fireAllRulesCommand);
	        cmds.add(getObjectsCommand);
			
			BatchExecutionCommand batch = CommandFactory.newBatchExecution(cmds, "main");
			
			System.out.println("Executing batch:\n" + BatchExecutionHelper.newXStreamMarshaller().toXML(batch));
			
			System.out.println("Executing batch:\n" + BatchExecutionHelper.newJSonMarshaller().toXML(batch));
			
			ExecutionResults response = kSession.execute(batch);
			
			System.out.println("Request:");
			printResponse(response.getValue("request"));
			
			System.out.println("Response:");
			Object objects = response.getValue("decision");
			
			//Collection<Object> objects = (Collection<Object>)kSession.getObjects(filter);
	        
			if (objects instanceof Collection) {
				for (Object o : (Collection<Object>) objects) {
					printResponse(o);
				}
			}
			
			System.out.println("Request fact handle: " + response.getFactHandle("request"));
		}
		
		private void printResponse(Object o) {
			if (o != null) {
				System.out.println("Object class: " + o.getClass().getName());
				if (o instanceof CashCreditRequest) {
					CashCreditRequest r = (CashCreditRequest)o;
					Decision d = r.getDecision();
					if (d != null) {
						System.out.println("Request object id: " + r.getId() + " " + d.getDecision() + " " + d.getEngine() + " " + d.getMaxAmount() + " " + d.getApprovalA() + " " + d.getApprovalB() + " " + d.getCondition());
					} else {
						System.out.println("Request object id: " + r.getId());
					}
				} else if (o instanceof Decision) {
					Decision d = (Decision) o;
					System.out.println("Decision object: " + d.getDecision() + " " + d.getEngine() + " " + d.getMaxAmount() + " " + d.getApprovalA() + " " + d.getApprovalB() + " " + d.getCondition());
				}
			} else {
				System.out.println("Object is null");
			}
		}
	}
}
