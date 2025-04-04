!start. //initial goal

/* Plans */

+!start
    <- .print("hello world.");
       //.j48("/home/maiquel/temp/decision_tree/app/tempo.arff", Result); //run j48
       .j48("weather.arff", Result); //run j48
       !print_rules(Result).

+!print_rules([]).   

+!print_rules([H|T])       
   <- .print(H);
      !print_rules(T).

{ include("$jacamo/templates/common-cartago.asl") }
{ include("$jacamo/templates/common-moise.asl") }

// uncomment the include below to have an agent compliant with its organisation
//{ include("$moise/asl/org-obedient.asl") }
