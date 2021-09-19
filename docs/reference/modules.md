# Modules
A module in Tego is declared using the `module` keyword, followed by a fully-qualified identifier. The fully-qualified identifier consists of one or more identifiers separated by forward slashes. For example:

```tego
module org/metaborg/tego
```

!!! note "Modules and Java Packages"
    The name of a module determines the Java package in which the generated classes are placed. In the example above, any strategies are generated into the `org.metaborg.tego` Java package.

!!! tip "Extending Modules"
    It is allowed to have multiple declarations of the same module name. Their classes will be generated into the same packages. However, it is not allowed to have duplicate or overlapping declarations in different modules. So it is not possible to provide additional definitions of a strategy in a separate module, even if they have the same name.


## External Modules
External modules are modules whose members are not defined here, but somewhere else (usually in Java directly). They are defined using the `extern` modifier, and contain (implicitly) `extern` declarations of their members such that Tego can resolve them properly. For example, the following declares the Java `Class` class:

```tego
extern module java/lang
  class Class
```

