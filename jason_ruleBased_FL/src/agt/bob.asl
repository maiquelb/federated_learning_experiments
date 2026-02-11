/**
   This agent processes a dataset and sends the results to the agent any known learning server.
   
   A known learning servers *Ag* are given by the belief learning_server(Ag).
  
   This processing is done by using the internal action .process_dataset, which returns a rules set

**/


//As soon as the agent discovers a server for the learning process, it process a dataset and shares the results 
+learning_server(Ag) 
    <- .wait(1000);
      //  .process_dataset("/mnt/1C4C766F4C764414/maiquel/git/federated_learning_experiments/RuleBasedFederateLearing/RulesTemp/breast-cancer-50.J48datasetmetrics", 
      //                   "/mnt/1C4C766F4C764414/maiquel/git/federated_learning_experiments/RuleBasedFederateLearing/RulesTemp/breast-cancer-50.J48rules", 
      //                   "/mnt/1C4C766F4C764414/maiquel/git/federated_learning_experiments/RuleBasedFederateLearing/RulesTemp/breast-cancer-50.J48rulesmetrics", 
      //     X);

      .process_dataset("../FL_Rules_exp/outdir/breastcancer-train-1-of-10-d-no50.J48datasetmetrics", 
                       "../FL_Rules_exp/outdir/breastcancer-train-1-of-10-d-no50.J48rules", 
                       "../FL_Rules_exp/outdir/breastcancer-train-1-of-10-d-no50.J48rulesmetrics", 
          X);


       .print("Sending dataset to server (alice): ", X);
       .send(Ag,achieve,process_dataset(X));
       .