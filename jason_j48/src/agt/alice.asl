+rules(R)  //plan to be triggered whenever the known rules change
   <- !print_rules(R).

+!print_rules([]).   

+!print_rules([H|T])       
   <- .print("Receiving rule: ", H);
      !print_rules(T).
