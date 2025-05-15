
# Background

Rules are composed of an antecedent $A$ and a consequent $C$. The intutition is that the antecedent leads to the consequent.

The antecedent is a set of conditions, which are, in turn, values of attributes. When some of them hold, some consequent is found. The consequent is a single value. 


Consider, for example, the table below, which shows some the rules extracted from thr wheather dataset. 

| Antecedent                                                 | Consequent |
|-------------------------------------------------------------|----------|
| *Outlook* is **sunny** and *Humidity* is **high**           | **No**   |
| *Outlook* is **sunny** and *Humidity* is **normal**         | **Yes**  |
| *Outlook* is **overcast**                                   | **Yes**  |
| *Outlook* is **rainy** and *Windy* is **TRUE**              | **No**   |
| *Outlook* is **rainy** and *Windy* is **FALSE**             | **Yes**  |


The existing attributes and their possible values are in the table below:

| Attribute  | Possible Values          |
|------------|---------------------------|
| *Outlook*  | `sunny`, `overcast`, `rainy` |
| *Humidity* | `high`, `normal`             |
| *Windy*    | `TRUE`, `FALSE`              |



breastTumor dataset

| Condições                                                                                              | Valor Previsto |
|--------------------------------------------------------------------------------------------------------|----------------|
| *inv-nodes* = **0** and *breast-quad* = **left-lower**                                                 | 22.56          |
| *inv-nodes* = **0** and *breast-quad* = **right-lower**                                                | 16.57          |
| *inv-nodes* = **0** and *breast-quad* = **left-upper**                                                 | 23.96          |
| *inv-nodes* = **0** and *breast-quad* = **right-upper** and *age* < **48.5**                           | 30.63          |
| *inv-nodes* = **0** and *breast-quad* = **right-upper** and **48.5** ≤ *age* < **56.5** and *breast* = **right** | 14.33          |
| *inv-nodes* = **0** and *breast-quad* = **right-upper** and **48.5** ≤ *age* < **56.5** and *breast* = **left**  | 26.25          |
| *inv-nodes* = **0** and *breast-quad* = **right-upper** and *age* ≥ **56.5**                           | 32.42          |
| *inv-nodes* = **0** and *breast-quad* = **central**                                                    | 12.09          |
| *inv-nodes* = **2**                                                                                    | 27.68          |
| *inv-nodes* = **3**                                                                                    | 27.5           |


| Atributo        | Valores Possíveis                        |
|------------------|------------------------------------------|
| *inv-nodes*     | **0**, **2**, **3**                      |
| *breast-quad*   | **left-lower**, **right-lower**, **left-upper**, **right-upper**, **central** |
| *age*           | numeric                                  |
| *breast*        | **left**, **right**                      |


## Representation of antecedent and consequent
The consequent may be <em>boolean</em>, <em>categorical</em>, or <em>numerical</em>

- <em>boolean</em>: the antecedent leads to a consequence which is either <em>true</em> or <em>false</em>. For example, in the well-known <em>wheather</em> dataset, wheather conditions (outlook, temperature, etc) lead to a decision on doing (or <em>play</em>) or not some activity.

- <em>categorical</em>: the antecedent leads to a consequence which is framed in a class. For example, in the <em>nursery</em> dataset, a set of conditions lead to classifications of applications for nursery schools (<em>not recommended</em>, <em>recommended</em>, <em>very recommended</em>, <em>priority</em>, <em>high priority</em>).

- <em>numeric</em>: the antecedent leads to a consequence related to a numeric value. Such relation may be equality (i.e. an exact value) as well as some relation with respect to a value (lower, greater, between, etc.). For example, in the <em>carPrice</em> dataset, a set of car features (weight, horsepower, etc.) lead to an estimated price of the vehicle.


# Model
A logical representation of rules requires a more formal definitions and representations of the rule components and semantics. 

The usual claim that the antecedent leads to a consequent does not consider the evaluation of the truth with respect to the elements involved in the rule. Logics, on the other hand, relies on the evaluation of truth. Thus, this claim can be rephrased to <em>"if the antecedent is true, then the consequent is also true"</em>, or, formally, $A\rightarrow C$. 

The problem then moves to evaluating the truth with respect to both $A$ and $C$.

## Logical representation of the antecedent
Informally, the antecedent is a set of conditions which lead to a consequence. Moving to a more logical perspective, it is possible to say that, when some conditions hold, the consequence also holds. Or, even formally, the consequence holds when certain conditions are <em>true</em>.

**Definition**: Once it is assumed that (i) the concequent is true when the antecedent is true and (ii) the antecedent is a set of conditions, then (i) the antecedent is a set of propositions and (ii) the consequent is a proposition.

**Definition**: Let $A=\{a_1, \cdots, a_n\}$. Then $\bigwedge_{i=1}^{|A|}a_i\rightarrow C$.

For knowledge representation and reasoning in cognitive agents, each condition in the antecedent must be an explicit relation between the attribute and its value. This relation can be stated as the value being a property of the attribute (e.g. *sunny* is a poperty of *outlook*, *high* is a property of *temperature* ).  This way, it is possible to relate each condition of the antecedent with a belief of the agent with respect to the properties of elements of the world (e.g. the beliefs of the agent about the *outlook* and *temperature*). From this, it is possible to define the proper representation sof these conditions according to the type of its value:

- *boolean*. Conditions whose type is *boolean* are either true or false (e.g. *windy* in the weather scenario). The holding of this kind of condition is conditioned to the agent having a belief whose identifier matches with the identifier of the belief. Thus, this condition is represented by a predicate of arity zero.

- *categorical*. Categorical conditions have values framed within a set of classes. The classes determine a *property* of the condition. For instance, in the weather scenario, *outlook* may have the properties *sunny*, *overcast*, and *rainy*, while *humidity* may have the values *high* and *normal*. Thus, categorical condtions are represented as a predicate *v(c)*, where *c* is the condition identifier and *v* is the condition value (e.g. *sunny(outlook)*, *high(temperature)*). The holding of these conditions is conditioned to the agent having the corresponding predicates in its belief base.

- *numeric*. Numeric conditions are related to numeric values. This relation may be equality (i.e. a certain condition has a value) but may be also relational (i.e. a certain condition is lower, higher, etc. that some value). In the case of equality relation, the condition identifier may me seen as a property of a number (e.g. *age(45)*). In the relational case, the condition identifier may be seen as a property of a number that follows the given relation (e.g. $age(X)\wedge X\leq 45$).

<!-- - The condition identifier is a *term* of first-order logic. For example, the weather scenario has the terms *outlook*, *temperature*, and *windy*.

- The value of an attribute can be seen as its property (e.g. *sunny* is a property of *outlook*).  

- A condition holds *iff* a condition has some property. The holding of an condition --- evaluated with respect to the beliefs of the agent --- may be either *true* or *false*. Thus, it can represented by a *formula*.

- The formula representing a condition is subject to the kind of data of its value:

    - *boolean*
    - *categorical*
    - *numeric*


-->

Thus, *predicate* is the proper representation of a condition in the antecedent of a rule since it allows both (i) to represent properties of elements of the world and (ii) check the truth with respet to the holding of such properties

**Definition**: Let $a$ be a condition belonging to the antecedent $A$ and let $v_a$ be the value of this condition. The representation of this condition in logical-based cognitive agents is $v_a(a)$.





From a logical perspective, it is possible to say that $A\rightarrow C$ (i.e. if the antecedent is true, then the consequent is also true).

have the form $antecedent\rightarrow consequent$. The $antecedent$ is a set of conditions. If all the conditions are true, then the antecedent is true and, then, the consequent is also true.

- Rule conditions are logical formulae
    

    
    Given a rule $$antecedent\rightarrow consequent$$ 
    
    s.t. 

    $$antecedent=\{c_1,\cdots,c_n\}$$

    and
    
      
     $$antecedent=   \bigwedge _c $$

    -   each clause of the rule results in a formula
        $$attribute(V) \wedge V <rel\_op> value$$


From Logical and
Relational Learning - pag 75

[Left-Weight <= 2), Right-Weight <= 2), Left-Distance <= 2), Right-Distance <= 2), Left-Weight <= 1),                 Left-Distance <= 1)], result(Class,R,4.0)

        $$balance.scale(Left.Weight, Right.Weight, Left.Distance, Right.Distance, Left.Weight,Left.Distance)\wedge Left.Weight\leq 2 \wedge Right.Weight\leq 2\rightarrow B$$
        

        