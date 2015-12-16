package com.redhat.waw.jstakun;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.core.command.runtime.rule.GetObjectsCommand;
import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.ReleaseId;
import org.kie.api.command.BatchExecutionCommand;
import org.kie.api.command.Command;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ObjectFilter;
import org.kie.internal.command.CommandFactory;
import org.kie.internal.runtime.helper.BatchExecutionHelper;

import com.redhat.waw.ose.model.Constraints;
import com.redhat.waw.ose.model.CustomerTransaction;
import com.redhat.waw.ose.model.Decision;

public class CustomerTransactionsClient {

	public static void main(String[] args) {
		new CustomerTransactionsClient().execute();
	}
	
	
	public void execute() {
		KieServices kieServices = KieServices.Factory.get();
		
		ReleaseId releaseId = kieServices.newReleaseId( "com.redhat.waw.financial", "CustomerTransactions", "1.0.1" );
		KieContainer kContainer = kieServices.newKieContainer(releaseId);
		
		KieScanner kScanner = kieServices.newKieScanner(kContainer);
		
		kScanner.scanNow();
		
		KieSession kSession = kContainer.newKieSession();   
		KieRuntimeLogger logger = KieServices.Factory.get().getLoggers().newFileLogger( kSession, "audit" ); 
		//kSession.getAgenda().getAgendaGroup(type).setFocus(); //Complex, Simple
		new Thread(new KieSessionRunner(kSession, logger)).start();   
	}
	
	private class KieSessionRunner implements Runnable {

		KieSession kSession = null;
		KieRuntimeLogger logger = null;
		
		public KieSessionRunner(KieSession kSession, KieRuntimeLogger logger) {
			this.kSession = kSession;
			this.logger = logger;
		}
		
		public void run() {
			System.out.println("Running session: " + kSession.getId());
			insertAndFireRules(kSession);
			
			System.out.println("Finished session: " + kSession.getId());
			logger.close();
			kSession.dispose();
		}
		
		private void insertAndFireRules(KieSession kSession) {
			Constraints c = new Constraints();
			c.setBetweenMin(500d);
			c.setBetweenMax(700d);
			//Constraints c1 = new Constraints();
			//c1.setBetweenMin(500);
			//c1.setBetweenMax(700);
			//Constraints c2 = new Constraints();
			//c2.setBetweenMin(500);
			//c2.setBetweenMax(700);
			CustomerTransaction ct = new CustomerTransaction();
			ct.setAmount(600d);
			ct.setTransactionid("1");
			CustomerTransaction ct1 = new CustomerTransaction();
			ct1.setAmount(650d);
			ct1.setTransactionid("2");
			CustomerTransaction ct2 = new CustomerTransaction();
			ct2.setAmount(400d);
			ct2.setTransactionid("3");
			
			Collection<Object> toInsert = new ArrayList<Object>();
			toInsert.add(c);
			//toInsert.add(c1);
			//toInsert.add(c2);
			toInsert.add(ct);
			toInsert.add(ct1);
			toInsert.add(ct2);
			
			
			Command insertObjectCommand = CommandFactory.newInsertElements(toInsert);
			Command fireAllRulesCommand = CommandFactory.newFireAllRules();
			
			ObjectFilter filter = new ObjectFilter() {
	            public boolean accept(Object object) {
	            	return object instanceof Decision;
	        }};
			
	        Command getObjectsCommand = new GetObjectsCommand(filter, "decision"); 
			
	        List<Command> cmds = new ArrayList<Command>();
	        cmds.add(insertObjectCommand);
	        cmds.add(fireAllRulesCommand);
	        cmds.add(getObjectsCommand);
			
			BatchExecutionCommand batch = CommandFactory.newBatchExecution(cmds);
			
			System.out.println("Executing batch:\n" + BatchExecutionHelper.newXStreamMarshaller().toXML(batch));
			
			ExecutionResults response = kSession.execute(batch);
			
			Object objects = response.getValue("decision");
			
			//Collection<Object> objects = (Collection<Object>)kSession.getObjects(filter);
	        
			if (objects instanceof Collection) {
				for (Object o : (Collection<Object>) objects) {
					if (o instanceof Decision) {
						Decision d = (Decision)o;
						System.out.println("Decision: " + d.getId() + " " + d.getValue());
					} else {
						System.out.println("Response: " + o.getClass().getName());
					}
				}
			}
		}
	}	
}
