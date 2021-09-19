# Imports
An import allows you to bring declarations from other modules into the scope of the current module. For example, in the module `A` the `foo` strategy of module `B` is imported and used.

```tego
module A

  import B::foo

  val foo2 = foo

module B

  public def foo: Int -> Int
```

!!! note "Public Members"
    Only `public` members of a module are exported. If you where to write `def foo` instead of `public def foo`, then the import of `foo` will fail.


## Wildcard Imports
In many cases you just want to import all the members of a module. In that case, you can use a wildcard import, where instead of a specific name you specify `*`. For example, the above snippet could be written as:

```tego
module A

  import B::*       // wildcard import

  val foo2 = foo

module B

  public def foo: Int -> Int
```

