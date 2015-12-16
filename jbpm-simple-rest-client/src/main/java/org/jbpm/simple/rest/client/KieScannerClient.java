package org.jbpm.simple.rest.client;

import org.kie.api.KieServices;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;

public class KieScannerClient {

	public static void main(String[] args) {
		try {
		KieServices kieServices = KieServices.Factory.get();
		KieRepository repo = kieServices.getRepository();
		ReleaseId defaultId = repo.getDefaultReleaseId();
		System.out.println(defaultId.toExternalForm());
		
		ReleaseId releaseId = kieServices.newReleaseId( "com.redhat.waw.bpm", "NumberPortability", "LATEST" );
		KieContainer kContainer = kieServices.newKieContainer( releaseId );
		KieScanner kScanner = kieServices.newKieScanner( kContainer );
		
		//Start the KieScanner polling the Maven repository every 10 seconds
		//kScanner.start( 10000L );
		
		kScanner.scanNow();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
