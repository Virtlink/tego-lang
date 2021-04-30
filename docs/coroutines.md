# Coroutines
The trick to performing lazy (and possibly asynchronous) computation in Java is to explicitly encode the control flow. Instead of letting a function simply run to its end and return, we rewrite the method to use continuation passing style and provide it with a continuation that handles the result. This way, we can pause execution any time we want, and call the continuation as soon as we want to continue (as the name implies).

Imagine a function `updatePerson`, which loads a person's record using its ID, updates its name, and saves the record again. The function returns the timestamp of when the record was saved. 

```java
long updatePerson(int id, String name) {
    System.out.println("Updating person with ID: " + id);
    Person person = personRepository.get(id);
    person.name = name;
    long timestamp = personRepository.save(id, person);
    System.out.println("Updated at: " + timestamp);
    return timestamp;
}
```

We would call this function like this:

```java
long result = updatePerson(10, "Aya");
```

Now we want to rewrite `updatePerson` as a coroutine, i.e. a computation that can be suspended. We do this using continuation-passing style (CPS). We explicitly provide a continuation that the function should call to continue execution. The continuation represents the rest of the computation to be performed once this computation is done. So we change the signature to accept the continuation as the last parameter. Also, the function should no longer return its result though `return`, but instead by calling the continuation.

```java
void updatePerson(int id, String name, Consumer<Long> onCompletion) {
    System.out.println("Updating person with ID: " + id);
    Person person = personRepository.get(id);
    person.name = name;
    long timestamp = personRepository.save(id, person);
    System.out.println("Updated at: " + timestamp);
    onCompletion.accept(timestamp);
}
```

We can call this modified `updatePerson` function by providing the continuation:

```java
long result;
updatePerson(10, "Daniel", r -> { result = r; });
```

Of course, this coroutine is not very lazy yet. It can call other coroutines, so let's assume that the methods on the `personRepository` are also coroutines. Therefore, we need to provide them with continuations. We will tackle this next.

## CPS
If `personRepository.get()` and `personRepository.save()` are coroutines, we need to provide them with a continuation that they can call. We can do this by splitting our function up into separate _function steps_ like this:

```java
void updatePerson(int id, String name, Consumer<Long> onCompletion) {
    // Start
    System.out.println("Updating person with ID: " + id);
    personRepository.get(id,
        (result) -> updatePerson2(id, name, onCompletion, result)
    );
}

void updatePerson2(int id, String name, Consumer<Long> onCompletion, Person result){
    // Step 2
    Person person = result;
    person.name = name;
    personRepository.save(id, person,
        (result) -> updatePerson3(id, name, person, onCompletion, result)
    );
}

void updatePerson3(int id, String name, Person person, Consumer<Long> onCompletion, long result){
    // Step 3
    long timestamp = result;
    System.out.println("Updated at: " + timestamp);
    onCompletion.accept(timestamp);
}
```

Note that the coroutine is called as the last action in each function step. This means that they can take advantage of tail call optimization to avoid creating a new stack frame for each coroutine or continuation call. But tail call optimization has its own downsides: it will appear as if the calls in between never happened, which makes debugging, stack tracing, and access control more difficult. Also note that the above example creates a new closure for every continuation, and finally but most importantly, Java does not support tail call optimization at all.

## State Machine
Let's see if we can solve some of these problems. Firstly, let's reduce the number of objects that are needed for representing continuations, because if we were to execute a coroutine in a loop, we would end up with a lot of continuation closure objects.

One way to solve this is to create a state machine, which takes the place of each of the continuations. To call the correct function step, we need to track which step we expect to execute next. Additionally, to provide the arguments and local variables to the call, we store them in the state machine.

```java
class updatePerson_StateMachine implements Consumer<Object> {
    // Arguments
    Test $this;
    int id;
    String name;

    // Local variables
    Person person;
    long timestamp;
    
    // State Machine
    updatePerson_State state;
    Function<Object, Object> onCompletion;

    updatePerson_StateMachine(Function<Object, Object> onCompletion) {
        this.onCompletion = onCompletion;
    }

    @Override
    public void accept(Object result) {
        switch (state) {
            case Start: throw new IllegalStateException();
            case Step2: $this.updatePerson2(id, name, this, result); break;
            case Step3: $this.updatePerson3(id, name, person, this, result); break;
            default: throw new IllegalStateException();
        }
    }
}

enum updatePerson_State {
    Start,
    Step2,
    Step3,
    Done,
}
```

The state machine class is relatively straightforward. Being a state machine, it has to store the current state in its `state` field. The state determines at which function step will resume when the state machine is used as a continuation. Because we reuse the state machine object, it will get return values of different types after each suspending function. Therefore, it has type `Consumer<Object>`, accepting any object (including `null`).

It would be used as a continuation like this:

```java
void updatePerson(int id, String name, Consumer<Long> onCompletion) {
    // Start
    
    // Create state machine
    updatePerson_StateMachine cont = new updatePerson_StateMachine(onCompletion);
    
    System.out.println("Updating person with ID: " + id);

    // Store arguments
    cont.id = id;
    cont.name = name;

    // Call coroutine
    cont.state = updatePerson_State.Step2;
    personRepository.get(id, cont);
}

void updatePerson2(int id, String name, updatePerson_StateMachine cont, Person result){
    // Step 2
    Person person = result;
    person.name = name;

    // Store local variables
    cont.person = person;    

    // Call coroutine
    cont.state = updatePerson_State.Step3;
    personRepository.save(id, person, cont);
}

void updatePerson3(int id, String name, Person person, updatePerson_StateMachine cont, long result){
    // Step 3
    long timestamp = result;
    System.out.println("Updated at: " + timestamp);
    cont.onCompletion.accept(timestamp);
}
```

Here we make the implicit assumption that the state machine continuation is only called zero or one time. If the same continuation would be called more than once, then the stored local variables and next `state` might go wrong. 

## Single Function
Our coroutine is still split across functions, which makes it harder to debug and stack trace. We can improve this by putting the different function steps back into one function, and using the state machine's state to jump to the correct piece of code.

The function needs to determine whether it is being called for the first time (using some other continuation), or being resumed (using the state machine as the continuation). If it is being resumed, it will be given the state machine as its continuation. Otherwise, if this is the first call, it will be given another continuation, and we should instantiate a new state machine and store the arguments and continuation in it.

```java
void updatePerson(int id, String name, Consumer<Object> onCompletion) {

    // Get the state machine
    updatePerson_StateMachine cont;
    if (onCompletion instanceof updatePerson_StateMachine) {
        // Resumed from a previous call
        cont = (updatePerson_StateMachine)onCompletion;
    } else {
        // Called for the first time
        cont = new updatePerson_StateMachine(onCompletion);
        // Store the arguments
        cont.$this = this;
        cont.id = id;
        cont.name = name;
    }
```

??? bug "Recursive calls"
How do we distinguish a direct recursive call to the coroutine (providing the state machine of the parent call as the continuation) from a resumed call (providing the state machine of this call as the continuation)?

The next step is to decide where to resume to. If we somehow ended up in an illegal state, we throw an exception.

```java
    Person person;
    long timestamp;

    Object result = cont.result;

    // Decide where to resume
    if (cont.state == Start) goto block0;
    if (cont.state == Step1) goto block1;
    if (cont.state == Step2) goto block2;
    throw new IllegalStateException();
```

Each resumption point has to start with restoring the local variables from the saved state.

```java
    block0:
        // Restore local variables
        person = cont.person;
        timestamp = cont.timestamp;
```

The next code is just the sequential code in between two coroutines. Since we reinstated the arguments and restored local variables, this should be straightforward.

```java
        System.out.println("Updating person with ID: " + id);
```

Now we end the block with a call to another coroutine. Just before the call we should store the changed local variables (if any), and update the `state` field to get us to the next step once the continuation is called.

```java
        // Call coroutine
        cont.state = updatePerson_State.Step1;
        personRepository.get(id, cont);     // coroutine
```

We can exit this function now. Execution will resume from the next block when the continuation is called.

```java
        // Done.
        return;
```

Upon entering the next step, it should restore the local variables (if any) and handle the result provided to the continuation, the result returned by the coroutine that was called previously. Note that since the result is not provided explicitly as an argument to the function, we store it in the state machine continuation instead.

```java
    block1:
        // Handle the previous result
        person = (Person)result;
```

This is also where we would assign it to a variable. Note that the initial step is not expecting any results yet (because we didn't end up here through calling a coroutine), but later steps do.

The rest of the code is straightforward:

```java
        person.name = name;

        // Store local variables
        cont.person = person;
        
        // Call coroutine
        cont.state = updatePerson_State.Step2;
        personRepository.save(id, person, cont);
        // Done.
        return;
    block2:
        // Restore local variables
        Person person = cont.person;
        
        // Handle the previous result
        timestamp = (long)result;

        System.out.println("Updated at: " + timestamp);
```

However, at the end of the last block, we call the _original_ continuation that we were provided with. Of course, we store the state first.

```java
        // Store local variables
        cont.timestamp = timestamp;

        // Call continuation
        cont.onComplete.accept(timestamp);
        // Done.
        return;
}
```

!!! tip "Coroutines returning `void`"
A coroutine that returns no value (`void`) can be implemented by having it pass `null` to the continuation (of type `Void`).


## Non-suspending Computations
When the coroutine doesn't suspend, it can simply continue its computation. However, if we model this using continuations, for example in a loop, then we might overflow the stack. Instead, it would be better for both stack usage and performance if we could simply skip calling the continuation whenever execution is sequential.

One of three things can happen in a coroutine that calls another coroutine: its continuation gets called immediately, its continuation gets called at some later point, or its continuation gets never called. In all cases does the above implementation simply return.

Since we are sure a continuation is executed either once or never, we can optimize for the case where the coroutine immediately calls the continuation once. We can have the coroutine just return the result and continue normal execution. However, we then also need a way to distinguish this case from the situation where the coroutine _doesn't_ call the continuation, so we can exit early. While we could adopt `null` to represent this case, `null` can also be a valid return value. Therefore, in Kotlin, they represent this by returning the special `COROUTINE_SUSPENDED` constant.

The new code becomes:

```java
Object updatePerson(int id, String name, Consumer<Object> onCompletion) {

    // Get the state machine
    updatePerson_StateMachine cont;
    if (onCompletion instanceof updatePerson_StateMachine) {
        // Resumed from a previous call
        cont = (updatePerson_StateMachine)onCompletion;
    } else {
        // Called for the first time
        cont = new updatePerson_StateMachine(onCompletion);
        // Store the arguments
        cont.$this = this;
        cont.id = id;
        cont.name = name;
    }

    Person person;
    long timestamp;

    Object result = cont.result;
    
    // Decide where to resume
    if (cont.state == Start) goto block0;
    if (cont.state == Step1) goto block1;
    if (cont.state == Step2) goto block2;
    throw new IllegalStateException();
    
    block0:
        // Restore local variables
        person = cont.person;
        timestamp = cont.timestamp;

        System.out.println("Updating person with ID: " + id);

        // Call coroutine
        cont.state = updatePerson_State.Step1;
        result = personRepository.get(id, cont);     // coroutine
        if (result == COROUTINE_SUSPENDED) return COROUTINE_SUSPENDED;
        // Fall through
    block1:
        // Handle the previous result
        person = (Person)result;
        
        person.name = name;

        // Store local variables
        cont.person = person;
        
        // Call coroutine
        cont.state = updatePerson_State.Step2;
        result = personRepository.save(id, person, cont);
        if (result == COROUTINE_SUSPENDED) return COROUTINE_SUSPENDED;
        // Fall through
    block2:
        // Restore local variables
        Person person = cont.person;
        
        // Handle the previous result
        timestamp = (long)result;

        System.out.println("Updated at: " + timestamp);
                
        // Store local variables
        cont.timestamp = timestamp;

        // Done.
        return timestamp;
}
```

Note that the state machine didn't change.

```java
class updatePerson_StateMachine implements Consumer<Object> {
    // Arguments
    Test $this;
    int id;
    String name;

    // Local variables
    Person person;
    long timestamp;
    
    // State Machine
    updatePerson_State state;
    Consumer<Object> onCompletion;
    Object result;

    updatePerson_StateMachine(Consumer<Object> onCompletion) {
        this.onCompletion = onCompletion;
    }

    @Override
    public void accept(Object result) {
        this.result = result;
        $this.updatePerson(id, name, this);
    }
}

enum updatePerson_State {
    Start,
    Step2,
    Step3,
    Done,
}
```

## Handling Exceptions
There is also the issue of handling exceptions. Whenever an exception occurs, there are two possibilities: the code was executing sequentially, and the exception can just propagate up the stack; or the exception should be passed to the continuation.

To allow for this, we make the continuation not just accept any value `T`, but a `Result<T>`, which is either a value of `T` or an exception.

```java
class Result<T> {
    static <T> Result<T> success(T value);
    static <T> Result<T> failure(Throwable exception);
    
    boolean isSuccess();
    boolean isFailure();
    
    @Nullable T getOrNull();
    @Nullable Throwable exceptionOrNull();
}
```

The signature for the continuation changes from `Function<Object, Object>` to `Function<Result<T>, Object>`. To model this even better, we introduce a `Continuation` interface:

```java
interface Continuation<T> extends Consumer<Result<T>> {
    void resumeWith(Result<T> result);

    @Override default void accept(Result<T> result) { resumeWith(result); }
    
    // Convenience methods:
    default void resume(T value) {
        resumeWith(Result.success(value));
    }
    default void resumeWithException(Throwable exception) {
        resumeWith(Result.failure(exception));
    }
}
```

The basic implementation of `Continuation<T>` stores another continuation and calls it with the returned result.

```java
abstract class ContinuationImpl<T> implements Continuation<T> {
    private final Consumer<Result<T>> continuation;
    
    public ContinuationImpl(Consumer<Result<T>> continuation) {
        this.continuation = continuation;
    }

    @Override void resumeWith(Result<T> result) {
        Result<T> newResult;
        try {
            T outcome = invokeSuspend(result);
            if (outcome == COROUTINE_SUSPENDED) return;
            newResult = Result.success(outcome);
        } catch (Throwable exception) {
            newResult = Result.failure(exception);
        }
        if (continuation != null) continuation.accept(newResult);
    }

    abstract Object invokeSuspend(Result<T> result);
}
```

!!! tip "Optimization"
    As long as the `continuation` we call is an instance of `ContinuationImpl`, we could instead use a loop, where we invoke `invokeSuspend` on the new continuation and use the previous result as the new continuation argument. Of course, this forces the base class of `ContinuationImpl` to be `Continuation<Any>`. 

The implementation of the state machine would be:

```java
class updatePerson_StateMachine extends ContinuationImpl<Object> {
    // Arguments
    Test $this;
    int id;
    String name;

    // Local variables
    Person person;
    long timestamp;
    
    // State Machine
    updatePerson_State state;
    Consumer<Result<Object>> onCompletion;
    Result<Object> result;

    updatePerson_StateMachine(Consumer<Result<Object>> onCompletion) {
        this.onCompletion = onCompletion;
    }

    @Override
    public Object invokeSuspend(Result<Object> result) {
        this.result = result;
        return $this.updatePerson(id, name, this);
    }
}
```

And a special `Coroutines.throwOnFailure(Result<Any> result)` will assert that the result is actually a successful value. Therefore, to handle the result of a coroutine call, it should first check whether the result is actually valid and not an exception.




## See Also

- KEEP — [Kotlin Coroutines](https://github.com/Kotlin/KEEP/blob/master/proposals/coroutines.md)
- Jorge Castillo — [Kotlin Continuations](https://jorgecastillo.dev/digging-into-kotlin-continuations)
- Ashish Kumar — [Suspend functions under the hood](https://proandroiddev.com/how-do-coroutines-work-under-the-hood-803e6e9da8bb)
- Luciano Almeida — [An Overview on Kotlin Coroutines](https://medium.com/@lucianoalmeida1/an-overview-on-kotlin-coroutines-d55e123e137b)
- Adrian Bukros — [Diving deep into Kotlin Coroutines](https://www.kotlindevelopment.com/deep-dive-coroutines/)
- esoco GmbH — [Coroutines in pure Java](https://medium.com/@esocogmbh/coroutines-in-pure-java-65661a379c85)
- [COROUTINE_SUSPENDED and suspendCoroutineOrReturn in Kotlin](https://stackoverflow.com/a/46103773/146622)
- [Suspending functions, coroutines and state machines](https://labs.pedrofelix.org/guides/kotlin/coroutines/coroutines-and-state-machines)