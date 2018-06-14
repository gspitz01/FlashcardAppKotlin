/*
 * Copyright (C) 2018 Greg Spitz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gregspitz.flashcardappkotlin

import com.gregspitz.flashcardappkotlin.data.model.Flashcard

/**
 * Initial data for the database
 */
object InitialData {
    val flashcards = listOf(Flashcard(category = "Design Patterns: Creational", front = "Abstract Factory", back = "Provide an interface for creating families of related or dependent objects without specifying their concrete classes."),
            Flashcard(category = "Design Patterns: Creational", front = "Builder", back = "Separate the construction of a complex object from its representation, allowing the same construction process to create various representations."),
            Flashcard(category = "Design Patterns: Creational", front = "Dependency injection", back = "A class accepts the objects it requires from an injector instead of creating the objects directly."),
            Flashcard(category = "Design Patterns: Creational", front = "Factory method", back = "Define an interface for creating a single object, but let subclasses decide which class to instantiate. Factory Method lets a class defer instantiation to subclasses."),
            Flashcard(category = "Design Patterns: Creational", front = "Lazy initialization", back = "Tactic of delaying the creation of an object, the calculation of a value, or some other expensive process until the first time it is needed. This pattern appears in the GoF catalog as \"virtual proxy\", an implementation strategy for the Proxy pattern."),
            Flashcard(category = "Design Patterns: Creational", front = "Multiton", back = "Ensure a class has only named instances, and provide a global point of access to them."),
            Flashcard(category = "Design Patterns: Creational", front = "Object pool", back = "Avoid expensive acquisition and release of resources by recycling objects that are no longer in use. Can be considered a generalisation of connection pool and thread pool patterns."),
            Flashcard(category = "Design Patterns: Creational", front = "Prototype", back = "Specify the kinds of objects to create using a prototypical instance, and create new objects from the 'skeleton' of an existing object, thus boosting performance and keeping memory footprints to a minimum."),
            Flashcard(category = "Design Patterns: Creational", front = "Resource acquisition is initialization (RAII)", back = "Ensure that resources are properly released by tying them to the lifespan of suitable objects."),
            Flashcard(category = "Design Patterns: Creational", front = "Singleton", back = "Ensure a class has only one instance, and provide a global point of access to it."),
            Flashcard(category = "Design Patterns: Structural", front = "Adapter, Wrapper, or Translator", back = "Convert the interface of a class into another interface clients expect. An adapter lets classes work together that could not otherwise because of incompatible interfaces. The enterprise integration pattern equivalent is the translator."),
            Flashcard(category = "Design Patterns: Structural", front = "Bridge", back = "Decouple an abstraction from its implementation allowing the two to vary independently."),
            Flashcard(category = "Design Patterns: Structural", front = "Composite", back = "Compose objects into tree structures to represent part-whole hierarchies. Composite lets clients treat individual objects and compositions of objects uniformly."),
            Flashcard(category = "Design Patterns: Structural", front = "Decorator", back = "Attach additional responsibilities to an object dynamically keeping the same interface. Decorators provide a flexible alternative to subclassing for extending functionality."),
            Flashcard(category = "Design Patterns: Structural", front = "Extension object", back = "Adding functionality to a hierarchy without changing the hierarchy."),
            Flashcard(category = "Design Patterns: Structural", front = "Facade", back = "Provide a unified interface to a set of interfaces in a subsystem. Facade defines a higher-level interface that makes the subsystem easier to use."),
            Flashcard(category = "Design Patterns: Structural", front = "Flyweight", back = "Use sharing to support large numbers of similar objects efficiently."),
            Flashcard(category = "Design Patterns: Structural", front = "Front controller", back = "The pattern relates to the design of Web applications. It provides a centralized entry point for handling requests."),
            Flashcard(category = "Design Patterns: Structural", front = "Marker", back = "Empty interface to associate metadata with a class."),
            Flashcard(category = "Design Patterns: Structural", front = "Module", back = "Group several related elements, such as classes, singletons, methods, globally used, into a single conceptual entity."),
            Flashcard(category = "Design Patterns: Structural", front = "Proxy", back = "Provide a surrogate or placeholder for another object to control access to it."),
            Flashcard(category = "Design Patterns: Structural", front = "Twin", back = "Twin allows modeling of multiple inheritance in programming languages that do not support this feature."),
            Flashcard(category = "Design Patterns: Behavioral", front = "Blackboard", back = "Artificial intelligence pattern for combining disparate sources of data (see blackboard system)"),
            Flashcard(category = "Design Patterns: Behavioral", front = "Chain of responsibility", back = "Avoid coupling the sender of a request to its receiver by giving more than one object a chance to handle the request. Chain the receiving objects and pass the request along the chain until an object handles it."),
            Flashcard(category = "Design Patterns: Behavioral", front = "Command", back = "Encapsulate a request as an object, thereby allowing for the parameterization of clients with different requests, and the queuing or logging of requests. It also allows for the support of undoable operations."),
            Flashcard(category = "Design Patterns: Behavioral", front = "Interpreter", back = "Given a language, define a representation for its grammar along with an interpreter that uses the representation to interpret sentences in the language."),
            Flashcard(category = "Design Patterns: Behavioral", front = "Iterator", back = "Provide a way to access the elements of an aggregate object sequentially without exposing its underlying representation."),
            Flashcard(category = "Design Patterns: Behavioral", front = "Mediator", back = "Define an object that encapsulates how a set of objects interact. Mediator promotes loose coupling by keeping objects from referring to each other explicitly, and it allows their interaction to vary independently."),
            Flashcard(category = "Design Patterns: Behavioral", front = "Memento", back = "Without violating encapsulation, capture and externalize an object's internal state allowing the object to be restored to this state later."),
            Flashcard(category = "Design Patterns: Behavioral", front = "Null object", back = "Avoid null references by providing a default object."),
            Flashcard(category = "Design Patterns: Behavioral", front = "Observer or Publish/subscribe", back = "Define a one-to-many dependency between objects where a state change in one object results in all its dependents being notified and updated automatically."),
            Flashcard(category = "Design Patterns: Behavioral", front = "Servant", back = "Define common functionality for a group of classes."),
            Flashcard(category = "Design Patterns: Behavioral", front = "Specification", back = "Recombinable business logic in a Boolean fashion."),
            Flashcard(category = "Design Patterns: Behavioral", front = "State", back = "Allow an object to alter its behavior when its internal state changes. The object will appear to change its class."),
            Flashcard(category = "Design Patterns: Behavioral", front = "Strategy", back = "Define a family of algorithms, encapsulate each one, and make them interchangeable. Strategy lets the algorithm vary independently from clients that use it."),
            Flashcard(category = "Design Patterns: Behavioral", front = "Template method", back = "Define the skeleton of an algorithm in an operation, deferring some steps to subclasses. Template method lets subclasses redefine certain steps of an algorithm without changing the algorithm's structure."),
            Flashcard(category = "Design Patterns: Behavioral", front = "Visitor", back = "Represent an operation to be performed on the elements of an object structure. Visitor lets a new operation be defined without changing the classes of the elements on which it operates."),
            Flashcard(category = "Design Patterns: Concurrency", front = "Active Object", back = "Decouples method execution from method invocation that reside in their own thread of control. The goal is to introduce concurrency, by using asynchronous method invocation and a scheduler for handling requests."),
            Flashcard(category = "Design Patterns: Concurrency", front = "Balking", back = "Only execute an action on an object when the object is in a particular state."),
            Flashcard(category = "Design Patterns: Concurrency", front = "Binding properties", back = "Combining multiple observers to force properties in different objects to be synchronized or coordinated in some way."),
            Flashcard(category = "Design Patterns: Concurrency", front = "Blockchain", back = "Decentralized way to store data and agree on ways of processing it in a Merkle tree, optionally using digital signature for any individual contributions."),
            Flashcard(category = "Design Patterns: Concurrency", front = "Compute kernel", back = "The same calculation many times in parallel, differing by integer parameters used with non-branching pointer math into shared arrays, such as GPU-optimized Matrix multiplication or Convolutional neural network."),
            Flashcard(category = "Design Patterns: Concurrency", front = "Double-checked locking", back = "Reduce the overhead of acquiring a lock by first testing the locking criterion (the 'lock hint') in an unsafe manner; only if that succeeds does the actual locking logic proceed. Can be unsafe when implemented in some language/hardware combinations. It can therefore sometimes be considered an anti-pattern."),
            Flashcard(category = "Design Patterns: Concurrency", front = "Event-based asynchronous", back = "Addresses problems with the asynchronous pattern that occur in multithreaded programs."),
            Flashcard(category = "Design Patterns: Concurrency", front = "Guarded suspension", back = "Manages operations that require both a lock to be acquired and a precondition to be satisfied before the operation can be executed."),
            Flashcard(category = "Design Patterns: Concurrency", front = "Join", back = "Join-pattern provides a way to write concurrent, parallel and distributed programs by message passing. Compared to the use of threads and locks, this is a high-level programming model."),
            Flashcard(category = "Design Patterns: Concurrency", front = "Lock", back = "One thread puts a \"lock\" on a resource, preventing other threads from accessing or modifying it."),
            Flashcard(category = "Design Patterns: Concurrency", front = "Messaging design pattern (MDP)", back = "Allows the interchange of information (i.e. messages) between components and applications."),
            Flashcard(category = "Design Patterns: Concurrency", front = "Monitor object", back = "An object whose methods are subject to mutual exclusion, thus preventing multiple objects from erroneously trying to use it at the same time."),
            Flashcard(category = "Design Patterns: Concurrency", front = "Reactor", back = "A reactor object provides an asynchronous interface to resources that must be handled synchronously."),
            Flashcard(category = "Design Patterns: Concurrency", front = "Read-write lock", back = "Allows concurrent read access to an object, but requires exclusive access for write operations."),
            Flashcard(category = "Design Patterns: Concurrency", front = "Scheduler", back = "Explicitly control when threads may execute single-threaded code."),
            Flashcard(category = "Design Patterns: Concurrency", front = "Thread pool", back = "A number of threads are created to perform a number of tasks, which are usually organized in a queue. Typically, there are many more tasks than threads. Can be considered a special case of the object pool pattern."),
            Flashcard(category = "Design Patterns: Concurrency", front = "Thread-specific storage", back = "Static or \"global\" memory local to a thread."))
}
