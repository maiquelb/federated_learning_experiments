!start. //initial goal


//plan to satisfy the goal "start"
+!start
    <- .print("hello world.");
       .j48("../datasets/weather.arff", Result); //run j48 
       .send(alice,tell,rules(Result)). //inform the agent alice about the new rules
       