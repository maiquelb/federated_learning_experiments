This application illustrates a Federated Learning process where (i) some agents run a machine learning algorithm to learn a set of decision rules,  sharing the results with a *server agent* and (ii) the *server agent* produces a unified rule set by merging the received ones.

## Application Scenario
The agent [*alice*](src/agt/alice.asl) is the server of the federated learning process. It continuously announces to any agent in the system that itelf plays this role.

As soon as [*bob*](src/agt/bob.asl), [*carol*](src/agt/carol.asl), and [*tom*](src/agt/tom.asl) indentify a federated learning server, they run a machine learning algorithm with their own datasets. As result, they learn some decicion rules and share them with the server. When the server receives a rule set, it runs a merge process to include the received rules in its current rule set.


Each agent may run a different algorithm. In this example, *bob* runs *Decision Table*, *carol* runs *PART*, and *tom* runs *j48*.



### Running the application

Requirements:
- Java JRE >= 21
  
In a shell, type the following commands: 
- `./gradlew alice` (to run *alice*)
- `./gradlew bob` (to run *bob*)
- `./gradlew carol` (to run *carol*)
- `./gradlew tom` (to run *tom*)

OBS: `./gradlew` works in Unix-based systems. In windows, replace it by `gradlew`
