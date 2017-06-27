# Paragraph Framework

Framework for gamebooks. Content is written in a simple XML.

## Running

Run this using [sbt](http://www.scala-sbt.org/):

```
sbt run
```

And then go to http://localhost:9000 to see the running web application.

## Example game
File bramkaBaldura.xml contains an example game for the Paragraph framework (in Polish). Just paste its content to the box in Paragraph's main page.


## Scripting reference
You can find examples of most usages in an example game. Scripts in the framework can be divided into three categories:

### Conditions
Representing conditions for displaying whole answer options in paragraphs. They are placed ```<answer next="26" condition="HERE" >```.

Simple tests can take one of three forms:
* ```variableName=someValue``` - a variable ```variableName``` has a value of ```someValue``` (number or text),
* ```variableName>someValue``` - a numeric variable ```variableName``` has a value greater than ```someValue```. In this usage, if the variable is not defined, it's treated as 0 (which is NOT so in the previous form, where a lack of variable is an empty text!),
* ```variableName<someValue``` - a numeric variable ```variableName``` has a value lesser than ```someValue```. In this usage, if the variable is not defined, it's treated as 0.

Bear in mind, that in your XML you have to escape ```<``` and ```>``` to ```$lt;``` and ```&gt;``` accordingly.

Any number of tests can be joined with theese conjunctions:
* ```:``` – logical OR,
* ```&``` – logical AND.

No parentheses are allowed, but using these two conjunctions, every condition is possible to write (AND has a greater priority than OR).

### Effects
Describing effects of choosing a particular answer in a paragraph. They are placed ```<answer next="26" effect="HERE" >``` and can be used with or without a ```condition``` attribute.

Simple effects can take one of three forms:
* ```variableName=someValue``` – define a variable ```variableName``` with a value ```someValue``` (numeric or textual). If it was already defined, it will be overwritten – it doesn't matter if it contained a number or a text.
* ```variableName@someValue``` – numeric variable ```variableName``` is increased by ```someValue```. If the variable wasn't defined, it is equivalent to ```variableName=someValue```
* ```variableName-someValue``` – numeric variable ```variableName``` is decreased by ```someValue```. If the variable wasn't defined, it is equivalent to ```variableName=-someValue```. Negative numbers aren't a problem.

You can declare many effects by joining clauses with ```&```.

### Injections
You can access variables in paragraph's description's and answers' texts. There are three variants of this:
* ```@variable@``` as in ```Hello @name@, my old friend.``` is used to substitute variable name between ```@``` signs with it's value,
* ```@condition#literal@``` as in ```Hello darkness@knownAlready=1#, my old friend@.``` is used to print a ```literal``` only if the ```condition``` (which is the same condition as in the beginning of this scripting reference) is met.
* ```@condition#literal1#literal2@``` as in ```And there are @pathsAmount>3#many#just some@ paths to tread``` works similarily to the previous version but if the condition is not met, it prints ```literal2```.
## Authors

Agnieszka Warchoł and Paweł Taborowski