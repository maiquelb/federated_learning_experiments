!start. //initial goal


//plan to satisfy the goal "start"
+!start
    <- .print("hello world.");
       .j48("weather.arff", Result); //run j48
       -+rules(Result);//update the belief corresponding to the known rules
       .send(alice,tell,rules(Result)). //inform the agent alice about the new rules
       

+rules(R)  //plan to be triggered whenever the known rules change
   <- !print_rules(R).

+!print_rules([]).   

+!print_rules([H|T])       
   <- .print(H);
      !print_rules(T).