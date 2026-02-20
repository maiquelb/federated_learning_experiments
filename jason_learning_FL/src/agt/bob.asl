/**
   This agent processes a dataset and sends the results to the agent any known learning server.
   
   A known learning servers *Ag* are given by the belief learning_server(Ag).
  
   This processing is done by using the internal action .process_dataset, which returns a rules set

**/

//As soon as the agent discovers a server for the learning process, it process a dataset and shares the results 
+learning_server(Ag) 
    <- .wait(1000);
       //run the Decision Table algorithm. Resulting rules and metrics are bound in the variable X
       .decisionTable("datasets/breastcancer-train-1-of-10-d-no50.arff", X);
       .print("Sending dataset to server (alice): ", X);

        //share the resulting rules and metrics with the server agent
       .send(Ag,achieve,process_dataset(X));
       .