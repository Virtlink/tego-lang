package com.virtlink.tego;

import java.util.function.Function;
/*
class Test {
    private PersonRepository personRepository;

    Object updatePerson(int id, String name, Function<Object, Object> compl) {
        updatePerson_StateMachine cont;
        if (compl instanceof updatePerson_StateMachine) {
            cont = (updatePerson_StateMachine)compl;
        } else {
            cont = new updatePerson_StateMachine(compl);
        }

        Object result = cont.result;
        switch (cont.state) {
            case Start: {
                // Handle the previous result
                Coroutines.throwOnFailure(result);

                // Store the arguments
                cont.$this = this;
                cont.id = id;
                cont.name = name;

                // Call coroutine
                cont.state = updatePerson_State.Step1;
                result = personRepository.get(id, cont);
                if (result == Coroutines.COROUTINE_SUSPENDED) return Coroutines.COROUTINE_SUSPENDED;
                // Fall through to the next step
            }
            case Step1: {
                // Restore arguments
                this = cont.this;
                id = cont.id;
                name = cont.name;

                // Handle the previous result
                Coroutines.throwOnFailure(result);
                Person person = (Person)result;

                // Call coroutine
                cont.state = updatePerson_State.Step2;
                result = personRepository.save(id, person, cont);
                if (result == Coroutines.COROUTINE_SUSPENDED) return Coroutines.COROUTINE_SUSPENDED;
                // Fall through to the next step
            }
            case Step2:
                // Handle the previous result
                Coroutines.throwOnFailure(result);
                long timestamp = (long)result;

                cont.state = updatePerson_State.Done;
                return timestamp;
                // Done.
            default:
                throw new IllegalStateException();
        }
    }

    class updatePerson_StateMachine implements Function<Object, Object> {
        // Arguments
        Test $this;
        int id;
        String name;

        // State Machine
        Object result;
        updatePerson_State state;
        Function<Object, Object> compl;

        updatePerson_StateMachine(Function<Object, Object> compl) {
            this.compl = compl;
        }

        @Override
        public Object apply(Object result) {
            this.result = result;
            return updatePerson(0, null, this);
        }

    }

    enum updatePerson_State {
        Start,
        Step1,
        Step2,
        Done,
    }
}

class PersonRepository {
    Object get(int id, Function<Object, Object> compl) {
        return null;
    }
    Object save(int id, Person person, Function<Object, Object> compl) {
        return null;
    }
}

class Person {
    String name;
}

class Coroutines {
    public static Object COROUTINE_SUSPENDED;

    public static void throwOnFailure(Object result) {

    }
}

 */