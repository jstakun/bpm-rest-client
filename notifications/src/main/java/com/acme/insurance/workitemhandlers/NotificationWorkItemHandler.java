package com.acme.insurance.workitemhandlers;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class NotificationWorkItemHandler implements WorkItemHandler {

  public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

    // extract parameters

    String from = (String) workItem.getParameter("From");
    String to = (String) workItem.getParameter("To");
    //String message = (String) workItem.getParameter("Message");
    String priority = (String) workItem.getParameter("Priority");


    // send email
    //EmailService service = ServiceRegistry.getInstance().getEmailService();
    //service.sendEmail(from, to, "Notification", message);
    
    System.out.println("Sending message from: " + from + " to: " + to + " with priority: " + priority);

    // notify manager that work item has been completed

    manager.completeWorkItem(workItem.getId(), null);

  }


  public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {

    // Do nothing, notifications cannot be aborted

  }


}
