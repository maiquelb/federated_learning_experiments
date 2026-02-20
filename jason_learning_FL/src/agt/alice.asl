/**
   This agent is a server in the learning process. 
   As soon it reecives a request for processing rules, a new ruleset is produced, merging the received rule set with the already known rules.


   This agent has 2 default beliefs, which are related to its activity as learning server:
      - test_dataset(D): the learning process requires a test dataset D, which is known by this agent.
      - processing_count(X): X counts the amount of processed rule sets. 
      - print_after_processing(N): the agent prints the rules after processing N rule sets.

   
   The main goals that this agent can achieve are:
      - process_dataset(D)[source(S)]: process a rule set D received from an agent S. 
      - print_dataset(D): print a rule set D.
      - announce_as_server: continuosly broadcast a message announcing itself as the server for the learning process.
      - 
**/

test_dataset("/mnt/1C4C766F4C764414/maiquel/git/federated_learning_experiments/RuleBasedFederateLearing/RulesTemp/breast-cancer-test.arff").
processing_count(0).
print_after_processing(3).



!announce_as_server. //goal to announce itself a learning server

+!start 
   <- .print("Hello").


+!announce_as_server : .my_name(Me)
   <- .broadcast(tell,learning_server(Me));
      .wait(10000);
      !announce_as_server.

+!process_dataset(dataset(Metrics, Rules))[source(S)] : test_dataset(T)
   <- .print("****** Processing dataset from ", S, " ******");
      .process_dataset_as_server(Metrics, Rules, T, Result);      
      !print_dataset(Result);
      .

+!print_dataset(dataset(dataset_metrics(nsamples(ListNSamples),nclasses(NClasses),classnames(ListClassnames),classdist(ListClassdist),natt(Natt)),rules(ListRules))[confusion_matrix(CM), summary(SMR)])
 :processing_count(X) & 
  print_after_processing(N) & 
  ((X+1) mod N )==0
    <- 
       .print("+++ nsamples: ", ListNSamples);
       .print("+++ nclasses: ", NClasses);
       .print("+++ classnames: ", ListClassnames);
       .print("+++ classdist: ", ListClassdist);
       .print("+++ natt: ", Natt);
       .print("+++ rules: ");
       !print_list(ListRules);
       .print("+++ confusion_matrix: ", CM);
       .print("+++ summary: ", SMR);
       .
+!print_dataset(D) : processing_count(X) 
   <- -+processing_count(X+1);
      .



+!print_dataset_metrics(dataset_metrics(nsamples(ListNSamples),nclasses(Nclasses),classnames(ListCClassnames),classdist(ListClassdist),natt(Natt)))
   <- .print(">> nsamples: ");
      !list_to_str(ListNSamples).
      


+!list_to_str(S,[]).

+!list_to_str(S,[H|T])
   <- .concat(S, ",", H, Result);
      !list_to_str(T).


+!print_list([]).

+!print_list([H|T])
   <- .print(H);
      !print_list(T).
