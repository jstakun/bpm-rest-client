package com.redhat.waw.jstakun;

import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;

public class Example1 {

	public static void main(String[] args) {
		new Example1().execute();
	}

	public void execute() {
		KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer();
        
        System.out.println("Running kbase1 in stream mode ---------------------");
        
        KieBase kbase1 = kContainer.getKieBase("org.kie.kbase1");
        for (int i=0;i<1;i++) {
        	KieSession kSession = kbase1.newKieSession();       
        	new Thread(new KieSessionRunner(kSession)).start();      
        }
        
        /*System.out.println("Running kbase2 cloud mode ---------------------");
        
        KieBase kbase2 = kContainer.getKieBase("org.kie.kbase2");
        for (int i=0;i<10;i++) {
        	KieSession kSession = kbase2.newKieSession();       
        	new Thread(new KieSessionRunner(kSession)).start();      
        }*/
	}
	
	private static void insertAndFireRules(KieSession kSession) {
		EntryPoint storeOne = kSession.getEntryPoint("StoreOne");
        EntryPoint storeTwo = kSession.getEntryPoint("StoreTwo");
        
        storeOne.insert(new Sale("a", 40, 5, "A"));
        storeOne.insert(new Sale("b", 5, 10, "B"));
        storeOne.insert(new Sale("c", 5, 10, "C"));       
        try {
			Thread.sleep(100); //1000
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        storeOne.insert(new Sale("d", 5, 50, "A"));
        //storeOne.insert(new Sale("e", 10000, 1, "B"));
        
        storeTwo.insert(new Sale("a", 40, 5));
        storeTwo.insert(new Sale("b", 5, 10));
        storeTwo.insert(new Sale("c", 5, 10));
        storeTwo.insert(new Sale("d", 5, 50));
        storeTwo.insert(new Sale("e", 10000, 1));
        
        kSession.fireAllRules();
	}
	
	private class KieSessionRunner implements Runnable {

		KieSession kSession = null;
		
		public KieSessionRunner(KieSession kSession) {
			this.kSession = kSession;
		}
		
		public void run() {
			System.out.println("Running session: " + kSession.getId());
			insertAndFireRules(kSession);
			System.out.println("Finished session: " + kSession.getId());
			kSession.dispose();
		}
	}
}
