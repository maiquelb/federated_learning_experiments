This application illustrates a Jason agent that runs a j48 algorithm. 

## Application Scenario

As soon it is launched, the agent [<em>bob</em>](src/agt/bob.asl) runs the learning algorithm. When new rules are learned, [<em>bob</em>](src/agt/bob.asl) shares its new knwoledge with  [<em>alice</em>](src/agt/bob.asl).

The learning capability of the agent is implemented through the internal action [`j48`](src/java/jason/stdlib/j48.java).

The default dataset is in the file [`weather.arff`](weather.arff). To use another dataset, set the proper file path in the code of the agent [<em>bob</em>](src/agt/bob.asl).

### Running the application

Requirements:
- Java JRE >= 17
  
In a shell, type `./gradlew run` (unix-based systems) or `gradlew run`(Windows)


