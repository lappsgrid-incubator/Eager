# RabbitMQ Module

[RabbitMQ](https://www.rabbitmq.com) is a lightweight, easy to use, open source message broker that can be used to send messages between processes even when those processes are running in different JVMs, on different machines, or possibly even in different data-centers.  Above all, RabbitMQ is dead simple to get up and running and start using.

The `org.lappsgrid.eager.mining.rabbitmq` module provides a simplified API for a subset of the RabbitMQ features.  Namely:

1. **Task Queues**<br/>messages are distributed to subscribed workers using a fair round robin algorithm.
1. **Publish/Subscribe**<br/>broadcasters send messages to all subscribed listeners.
1. **Routing**<br/>point-to-point communication between sender and receiver.

## The RabbitMQ Server

Many of the included tests expect a RabbitMQ server to be available on localhost.  The easiest way to achieve this is to use the Docker image:

```bash
docker run -d -p 5672:5672 -p 15672:15672 --hostname rabbit --name rabbit rabbit:3-management
```

The RabbitMQ administration console will be available on http://localhost:15672 (username: *guest*, password: *guest*).

## RabbitMQ

All of the message queue types extend the `RabbitMQ` base class which handles the common tasks of opening a connection, creating a channel, and registering `Consumers`.  All queues have at least two constuctors; 

```
public RabbitMQ(String name) { ... }
public RabbitMQ(String name, String host) { ... }
```

If the `host` parameter is not specified then *localhost* is assumed.

### Messages

In RabbitMQ messages are simple byte arrays allowing any data to be transmitted.  However, in the `org.lappsgrid.eager.rabbitmq` classes all messages are considered to be sequences of UTF-8 encoded characters (i.e. Strings).  In most cases the message will be an *application/json* payload of some kind. 

### Creating Queues

Nothing special needs to be done to create a message queue on the RabbitMQ server, simply accesing the queue will cause it to be created if it does not already exist.

**NOTE** If the message queue already exists then programs **must** use the same settings (*durable* and/or *fair*) when connecting to the queue. Otherwise RabbitMQ will throw an exception.

```
TaskQueue q1 = new TaskQueue('example', 'localhost', true, true) // ok if the queue doesn't exist or has been created with the same parameters.
TaskQueue q2 = new TaskQueue('example', 'localhost', false, true) // exception thrown.
```

### Registering Consumers

There are two ways to add a `Consumer` to a message queue:

1. Use one of the `RabbitMQ.register(Consumer)` or `RabbitMQ.register(Closure)` methods.
1. Use one of the classes that extend `DefaultConsumer` provided with each queue type.
   - `TaskQueue` -> `Worker`
   - `Publisher` -> `Subscriber`
   - `PostOffice` -> `MailBox`

**NOTE** Due to the way that RabbitMQ wires together exchanges and queues the `register` methods can not be used with `PostOffice` instances.  The only way to receive messages from a `PostOffice` is to extend the `MailBox` class and implement the `recv(String)` method.


#### RabbitMQ.register()

The `RabbitMQ` class provides two overloaded `register` methods that can be used to add consumers to a message queue; one takes a Groovy Closure and the other a `DefaultConsumer`:

```
TaskQueue q = new TaskQueue('example')
q.register { String message ->
    System.out.println(message)
}
```

``` 
TaskQueue q = new TaskQueue('example')
Consumer consumer = new DefaultConsumer() { ... }
q.register(consumer)
```

#### Worker Classes

```
    // Task Queue workers.
    TaskQueue q = new TaskQueue('example.queue')
    Worker worker1 = new Worker(q) {
        public void work(String message) {
            System.out.println(message);
        }
    }
    Worker worker2 = new Worker('example.queue) {
        public void work(String message) {
            System.out.println(message);
        }
    }
```

```    
    // Subscribers to publishers (broadcasters)
    Subscriber subscriber = new Subscriber('example.broadcast') {
        public void recv(String message) {
            System.out.println(message)
        }
    }
    Publisher pub = new Publisher('example.broadcast')
    pub.send('Hello world.')
```

```
    // Topic queues (routed messages)
    MailBox box = new MailBox('example.exchange', 'alice') {
        public void recv(String message) {
            System.out.println("alice: $message")
        }
    }
    MailBox box = new MailBox('example.exchange', 'bob') {
        public void recv(String message) {
            System.out.println("bob: $message")
        }
    }
    PostOffice po = new PostOffice('example.exchange')
    po.send('bob', 'Hi Bob')
    po.send('alice', 'Hi Alice')
```

## Task Queues

Task queues are used to distribute work to a pool of workers subscribed to the queue.  If the queue is set to be *fair* then tasks are distributed to workers only when they are available to accept a new task, i.e. they have finished their previous task.  If the queue has not been set to be *fair* then tasks are dealt out in a round-robin fashion which may result in one worker receiving most of the long running tasks leaving the other workers under utilized. 


```
// ExampleQueue.java
TaskQueue queue = new TaskQueue('testing');
queue.send("This is message one.");
queue.send("This is message two.");
```

``` 
// Worker1.java
Worker w = new Worker('testing') {
    public void work(String message) {
        System.out.println("worker 1: " + message);
    }
}
```

``` 
// Worker2.java
Worker w = new Worker('testing') {
    public void work(String message) {
        System.out.println("worker 2: " + message);
    }
}
```

## Publisher/Subscriber

Use the Publisher/Subscriber classes when messages need to be sent to all of the subscribers.

``` 
// In Broadcaster.java
Publisher pub = new Publisher('pub.example');
pub.publish("Message one.");
pub.publish("Message two.");

// In Subscriber1.java
Subscriber sub = new Subscriber('pub.example') {
    public void recv(String message) {
        System.out.println("Sub 1: " + message);
    }
}

// In Subscriber2.java
Subscriber sub = new Subscriber('pub.example') {
    public void recv(String message) {
        System.out.println("Sub 2: " + message);
    }
}
```

## Topic Queues

``` 
// In Main.java
PostOffice office = new PostOffice('stanford');
office.send("tokenizer", "Text to tokenize.");
office.send("splitter", "Text to sentence split.");

// In Tokenizer.java
MailBox box = new MailBox('stanford', 'tokenizer') {
    public void recv(String message) {
        // Tokenize the message.
    }
}

// In Splitter.java
MailBox box = new MailBox('stanford', 'splitter') {
    public void recv(String message) {
        // Sentence split the message.
    }
}
```