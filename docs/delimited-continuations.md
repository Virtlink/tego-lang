# Delimited Continuations
To model `yield` and lazy sequences, we need delimited continuations. The _call/cc_ control operator captures the rest of the whole program, which is an example of an _undelimited continuation_. However, _delimited continuation_ only capture a slice of the program, and allow us to return a value.




## See Also
- Fischer – [Laymans explanation of delimited continuations](https://gist.github.com/sebfisch/2235780)
- Andy Wingo — [Guile and Delimited Continuations](https://wingolog.org/archives/2010/02/26/guile-and-delimited-continuations)