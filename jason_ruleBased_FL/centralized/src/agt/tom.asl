/**
   This agent process a dataset and sends the results to the agent *alice*, which acts as a server in the federeated learning process.
  
   This processing is done by using the internal action .process_dataset, which returns a rules set

**/

!start. //initial goal


//plan to satisfy the goal "start"
+!start
    <- .wait(200);
       .process_dataset("/mnt/1C4C766F4C764414/maiquel/git/federated_learning_experiments/RuleBasedFederateLearing/RulesTemp/breast-cancer-70.J48datasetmetrics", 
                        "/mnt/1C4C766F4C764414/maiquel/git/federated_learning_experiments/RuleBasedFederateLearing/RulesTemp/breast-cancer-70.J48rules", 
                        "/mnt/1C4C766F4C764414/maiquel/git/federated_learning_experiments/RuleBasedFederateLearing/RulesTemp/breast-cancer-70.J48rulesmetrics", 
       X);
       .print(X);
       .send(alice,achieve,process_dataset(X));
       .