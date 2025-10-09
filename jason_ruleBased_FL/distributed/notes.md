# Abstract representation

A dataset is a set $R$ of rules.

$r=\{a_1, \cdots, a_n\}\times\text{s}\times\text{e}$

s.t.:
- $_n$ is the number of attributes;
- $s\in \mathbb{R}^+_0$ is the support of the rule, i.e. the amount of examples the rule covers;
- $e\in \mathbb{R}^+_0$ is the error, i.e. the amount of examples wrongly classified.


# Jason representation
A dataset is represented as a list of rules.
Each rule is a list including its $n$ attributes, plus the predicates $support(S)$ and $error(E)$, where $S$ and $V$ are, respectively, the support and the error of the rule.


alice: 8081
bob: 8082
tom: 8083
carol: 8084