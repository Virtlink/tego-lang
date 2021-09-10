# Milestones

## Milestone 1: Constant Strategies
```tego
module A

  def foo1: Int -> Int
  foo1 = !10.
```

## Milestone 2: Constant Rules
```tego
module A

  def foo2: Int -> Int
  foo2 :- a -> !10.
```

## Milestone 3: Constant Values
```tego
module A

  val bar1: Int = 10.
```

## Milestone 4: Sequence Operator
```tego
module A

  def foo3: Int -> Int
  foo3 = !10; !20.
```

## Milestone 5: Applying an External Strategy
```tego
module A

  def foo4: Int -> Int
  foo4 = !10; inc.

  extern def inc: Int -> Int
```

## Milestone 6: Using the Input Argument of a Strategy
```tego
module A

  def foo5: Int -> Int
  foo5 = inc.

  extern def inc: Int -> Int
```

## Milestone 7: Using the Input Argument of a Rule
```tego
module A

  def foo6: Int -> Int
  foo6 :- a -> <inc> a.

  extern def inc: Int -> Int
```

## Milestone 8: Building and Using Tuples
```tego
module A

  def foo7: Int -> Int
  foo7 :- a -> <add> (a, 2).

  extern def add: (Int, Int) -> Int
```

## Milestone 9: Calling a Generic Strategy
```tego
module A

  def foo8: Int -> Int
  foo8 = !10; id2.

  extern def id2<T>: T -> T
```