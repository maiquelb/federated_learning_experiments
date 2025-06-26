
# Background

Rules are composed of an antecedent $A$ and a consequent $C$. The intutition is that the antecedent leads to the consequent.

The antecedent is a set of conditions, which are, in turn, values of attributes. When some of them hold, some consequent is found. The consequent is a single value. 


Consider, for example, the table below, which shows some rules extracted from the *weather* dataset. 

| # | Antecedent                                                 | Consequent |
---|-------------------------------------------------------------|----------|
| 1 | *Outlook* is **sunny** and *Humidity* is **high**           | **No**   |
| 2 | *Outlook* is **sunny** and *Humidity* is **normal**         | **Yes**  |
| 3 | *Outlook* is **overcast**                                   | **Yes**  |
| 4 | *Outlook* is **rainy** and *Windy* is **TRUE**              | **No**   |
| 5 | *Outlook* is **rainy** and *Windy* is **FALSE**             | **Yes**  |


While the consequent may be either *true* or *false*, the attributes and their possible values are in the table below:

| Attribute  | Possible Values          |
|------------|---------------------------|
| *Outlook*  | `sunny`, `overcast`, `rainy` |
| *Humidity* | `high`, `normal`             |
| *Windy*    | `TRUE`, `FALSE`              |





The tables below show another example, considering the *breast tumor* dataset.

| #  | Antecedent                                                                                                       | Consequent |
| -- | ---------------------------------------------------------------------------------------------------------------- | ---------- |
| 1  | *inv-nodes* = **0** and *breast-quad* = **left-lower**                                                           | 22.56      |
| 2  | *inv-nodes* = **0** and *breast-quad* = **right-lower**                                                          | 16.57      |
| 3  | *inv-nodes* = **0** and *breast-quad* = **left-upper**                                                           | 23.96      |
| 4  | *inv-nodes* = **0** and *breast-quad* = **right-upper** and *age* < **48.5**                                     | 30.63      |
| 5  | *inv-nodes* = **0** and *breast-quad* = **right-upper** and **48.5** ≤ *age* < **56.5** and *breast* = **right** | 14.33      |
| 6  | *inv-nodes* = **0** and *breast-quad* = **right-upper** and **48.5** ≤ *age* < **56.5** and *breast* = **left**  | 26.25      |
| 7  | *inv-nodes* = **0** and *breast-quad* = **right-upper** and *age* ≥ **56.5**                                     | 32.42      |
| 8  | *inv-nodes* = **0** and *breast-quad* = **central**                                                              | 12.09      |
| 9  | *inv-nodes* = **2**                                                                                              | 27.68      |
| 10 | *inv-nodes* = **3**                                                                                              | 27.5       |


In this case, the consequent assumes numeric values while the possible values of the conditions of the antecedent are listed below.

| Attribute        | Possible values                        |
|------------------|------------------------------------------|
| *inv-nodes*     | **0**, **2**, **3**                      |
| *breast-quad*   | **left-lower**, **right-lower**, **left-upper**, **right-upper**, **central** |
| *age*           | numeric                                  |
| *breast*        | **left**, **right**                      |


## Representation of values
The conditions in both antecedent and consequent may be <em>boolean</em>, <em>categorical</em>, or <em>numerical</em>

- <em>boolean</em>: values may be either <em>true</em> or <em>false</em>. For example, in the well-known <em>wheather</em> dataset, wheather conditions (outlook, temperature, etc) lead to a decision on doing (or <em>play</em>) or not some activity.

- <em>categorical</em>: values are framed in a set of classes. For example, in the <em>weather</em> dataset, the *outlook* condition may be either *sunny* or *overcast* or *rainy*.

- <em>numeric</em>: values are related to a numeric value. Such relation may be equality (i.e. an exact value) as well as some relation with respect to a value (lower, greater, between, etc.). For example, in the <em>breast tumor</em> dataset, the value of age may be either equal or less than or greater than a certain number.


# Model
A logical representation of rules requires more formal definitions and representations of the rule components and semantics. 

The usual claim that the antecedent leads to a consequent does not consider the evaluation of the truth with respect to the elements involved in the rule. Logics, on the other hand, relies on the evaluation of truth. Thus, this claim can be rephrased to <em>"if the antecedent is true, then the consequent is also true"</em>, or, formally, $A\rightarrow C$. 

The problem then moves to evaluating the truth with respect to both $A$ and $C$.

## Logical representation of the antecedent
Informally, the antecedent is a set of conditions which lead to a consequence. Moving to a more logical perspective, it is possible to say that, when some conditions hold, the consequence also holds. Or, even formally, the consequence holds when certain conditions are <em>true</em>.

**Definition**: Once it is assumed that (i) the concequent is true when the antecedent is true and (ii) the antecedent is a set of conditions, then (i) the antecedent is a set of propositions and (ii) the consequent is a proposition.

**Definition**: Let $A=\{a_1, \cdots, a_n\}$. Then $\bigwedge_{i=1}^{|A|}a_i\rightarrow C$.

Knowledge representation and reasoning in cognitive agents requires means to check the truth about every condition in the antecedent. Thus, in logic-based agents, they must be represented through propositions. 

 In particular, in the case of BDI agents, it is necessary to have means to evaluate the truth of every condition with respect to the belief base of the agent. If the beliefs are represented through predicates, the conditions must be represented by logical formulae. The form of these formulae depend on the type of the condition value:

- *boolean*. Conditions whose type is *boolean* are either true or false (e.g. *windy* in the weather scenario). The holding of this kind of condition is conditioned to the agent having a belief whose identifier matches with the identifier of the belief. Thus, this condition is represented by a predicate of arity zero (or its negation in the case of conditions with false values).

- *categorical*. Categorical conditions have values framed within a set of classes. The classes determine a *property* of the condition. For instance, in the weather scenario, *outlook* may have the properties *sunny*, *overcast*, and *rainy*, while *humidity* may have the values *high* and *normal*. Thus, categorical condtions are represented as a predicate *v(c)*, where *c* is the condition identifier and *v* is the condition value (e.g. *sunny(outlook)*, *high(temperature)*). The holding of these conditions is conditioned to the agent having the corresponding predicates in its belief base.

- *numeric*. Numeric conditions are related to numeric values. This relation may be equality (i.e. a certain condition has a value) but may be also relational (i.e. a certain condition is lower, higher, etc. that some value). In the case of equality relation, the condition identifier may me seen as a property of a number (e.g. *age(45)*). In the relational case, the condition identifier may be seen as a property of a number that follows the given relation. This cannot be represented by an atomic formula. Rather, it requires a more complex representation. Let $c$ be a condition identifier, $R$ be a relation $\in\{<,\leq,<,  \geq\}$, $v$ be a number corresponding to the value of $c$, and $V$ be a variable. This condition may pre represented as $c(V)\wedge V\ R\ v$. For example, in the breast tummor scenario,  the condition of age being lower or equal than 45 can be represented as $age(X)\wedge X\leq 45$.



## Logical representation of the consequent

The logical representation of the consequent also depens on its type.

- *boolean*. Boolean consequent of a certain fact as *true* or *false*. This fact is the identifier of the consequent, which is a scenario-dependent value. Thus, the logical representation of *boolean* consequents is a proposition with its identifier (or its negation). For example, in the *weather* dataset, the consequent stands for playing tennis or not. The consequent may be represented by *play_tennis*, for example. 

- *categorical*. Categorical consequents are framed withing a set of values. These values may be seen as a property of the consequent. For example, in the *nursery* dataset, the consequent is the result of application evaluations in a nursery school (i.e., the consequent is the *property* of the result). This result may be *not recommended*,*recommended*,*very recommended*, *priority*,and *special priority*. Thus, the logical representation of categorical consequents is a predicate of arity 1 where the functor is the consequent value and the argument is the consequent identifier (e.g. *recommended(result*)).

- *numeric*. In numeric consequents, some numeric value is assigned to the consequent identifier. For example, in the *breast * dataset, a number is assigned to the *tumor size*. The consequent identifier may be seen as a property of its associated number. Thus, the logical representation of numerical consequents is a predicate of arity 1 where the functor is the consequent identifier and the argument is the consequent value (e.g. *tumor\_size(22.56)*). 



# Examples

Applying these assumptions, and considering the Definition 1, rules can be written as first-order logic expressions where a conjunction implies an atomic formula. 

For example, the rule 4 of *weather* dataset can be written as 

$$rainy(outlook)\wedge windy \rightarrow \neg play.$$


The rule 5 of *breast tumor* dataset can be written as 

$$inv\text{-}nodes(0)\wedge right\text{-}upper(breast\text{-}quad) \wedge age(X) \wedge X\geq 48.5 \wedge X<56.5 \rightarrow tumor\text{-}size(43)$$

<!-- 

From Logical and
Relational Learning - pag 75

[Left-Weight <= 2), Right-Weight <= 2), Left-Distance <= 2), Right-Distance <= 2), Left-Weight <= 1),                 Left-Distance <= 1)], result(Class,R,4.0)

        $$balance.scale(Left.Weight, Right.Weight, Left.Distance, Right.Distance, Left.Weight,Left.Distance)\wedge Left.Weight\leq 2 \wedge Right.Weight\leq 2\rightarrow B$$
        

        -->

# From logical expressions to Jason Rules

Converting the logical expressions in rules is trivial when the consequent of the consequent of the implication is a positive fact (i.e. it is not a negation).
For the corresponding rule of 
$$inv\text{-}nodes(0)\wedge right\text{-}upper(breast\text{-}quad) \wedge age(X) \wedge X\geq 48.5 \wedge X<56.5 \rightarrow tumor\text{-}size(43)$$

is 
`tumor_size(43):-inv_nodes(0) \& right_upper(breast_quad) \& age(X) \& X>=48.5 \& X<56.5`


On the other hand, rules whose consequent is a negated fact require some transformations since Jason (as well as prolog) does not consider negation in the conclusion of a rule. Consider, for instance, the following expression:

$$a\rightarrow \neg c$$

From propositional logic:

$$x\rightarrow \neg y \equiv \neg y\rightarrow \neg x$$

Then, 

$$a\rightarrow \neg c \equiv \neg(\neg c) \rightarrow \neg a$$

and, finally, 

$$a\rightarrow \neg c \equiv c \rightarrow \neg a$$


From this background, a negative rule may me written as a positive consequent being consequence of the negated antecedent. Let $$\{a_1,\cdots,a_n\}$$ be a set of antecedents whose consequent is $$\neg c$$. The corresponding rule is

`
c :- not(a1 & ... & an)
`

Consider, for example, the expression $$rainy(outlook)\wedge windy \rightarrow \neg play.$$. The corresponding rule is 

`
play:- not(rainy(outlook) & windy)
`

