# Congruence Operator
The congruence operator `?!` matches the value and applies the strategies,
building a new value with the results of the strategies, or failing if any
of the strategies fail.

```tego
?!MyClass(<inc>, <is-string>, MyOtherClass(3))
```

Desugared:

```tego
is<MyClass> ;
where(MyClass#component1 ; inc ; @c1) ;
where(MyClass#component2 ; is-string ; @c2) ;
where(MyClass#component3 ; is<MyOtherClass> ;
  where(MyOtherClass#component1 ; is<Int> ; Int#equals(3)) ; @c3) ;
\_ -> MyClass(c1, c2, c3).\
```


