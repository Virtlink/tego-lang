package com.virtlink.tego.runtime

class Test {
    @JvmField var personRepository: PersonRepository = PersonRepository()

    suspend fun updatePerson(id: Int, name: String): Long {
        System.`out`.println("Updating person with ID: " + id)
        val person: Person = personRepository.get(id)
        person.name = name
        val timestamp: Long = personRepository.save(id, person)
        System.`out`.println("Updated at: " + timestamp)
        return timestamp
    }
}

class PersonRepository {
    suspend fun get(id: Int): Person {
        return Person()
    }
    suspend fun save(id: Int, person: Person): Long {
        return 10;
    }
}

class Person {
    @JvmField var name: String = ""
}