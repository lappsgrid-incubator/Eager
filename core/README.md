# Core Utilities

The core module includes common utility classes for:

1. JMX
1. JSON serialization
1. Solr document models
1. SSL 
1. RabbitMQ configuration

## JMX

The `org.lappsgrid.eager.mining.core.jmx.Registry` class exposes the most common JMX instruments from [dropwizard.io](https://metrics.dropwizard.io/3.1.0/manual/core/):

1. Counter
1. Meter
1. Timer

Use *Counter* objects to keep a count of events, objects, etc.  A *Meter* is like a Counter but is used to measure the rate at which events occur. Use *Timer* object to measure the duration of events or operations.

#### MBeans

To register a application with JMX it must provide a class that implements a `MBean` interface, that is, any interface name that ends with the literal string *MBean*.

```groovy
import org.lappsgrid.eager.mining.core.jmx.*

interface MainMBean {
	void stop();
}

class Main implements MainMBean {
    volatile boolean running = false
    
    void start() {
        running = true
        while (running) {
            // do stuff
        }
    }
    
    void stop() {
        running = false
    }
    
    static void main(String[] args) {
        Main app = new Main()
        Registry.register(app, "org.lappsgrid.example.Main:type=Main")
        Registry.startJmxReporter()
        app.start()  
        Registry.stopJmxReporter()  
    }
}
```
**Note** the name of the class and interface is important.  If the class is named `Foo` the interface **must** be named `FooMBean`.

#### Meters, Counters, and Timers

The `Registry` class provides static factory methods for creating `Counter`, `Meter`, and `Timer` objects.  Each of the factory methods accepts one or more Strings that will be joined together and appended to the String `org.lappsgrid.eager.mining` to generate the name of the JMX componenet.
  

```groovy
import com.codahale.metrics.*
import org.lappsgrid.eager.mining.core.jmx.Registry

class Worker {
    
    Meter workDone
    Counter errors
    Timer timer
    
    Worker() {
        workDone = Registry.meter('work')
        errors = Registry.counter('errors')
        timer = Registry.timer('duration')
    }
    
    void work() {
        workDone.mark()
        Timer.Context context = timer.time()
        try {
            doTheWork()
        }
        catch (Exception e) {
            errors.inc()
        }
        finally {
           context.stop() 
        }
    }
}
```
Now when the application is running you will be able to connect to it with JConsole or VisualVM to get instrument readings or run commands via the MBean.

#### Starting Your Application

Java requires a number of values to be defined with the `-D` command line flag:
1. -Dcom.sun.management.jmxremote 
1. -Dcom.sun.management.jmxremote.authenticate=false 
1. -Dcom.sun.management.jmxremote.ssl=false 
1. -Dcom.sun.management.jmxremote.port=$PORT 
1. -Djava.rmi.server.hostname=$IP 
1. -Dcom.sun.management.jmxremote.rmi.port=$PORT

Where:

- **$IP** is the IP address or host name of the machine running the application.
- **$PORT** is the port number you want JMX to use. 

```bash
IP=`curl ipinfo.io/ip`
PORT=11111
OPTS="-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.port=$PORT -Djava.rmi.server.hostname=$IP -Dcom.sun.management.jmxremote.rmi.port=$PORT"
java $OPTS -jar my.jar

```

## JSON Serialization

The `org.lappsgrid.eager.mining.core.json.Serializer` class is a simple copy/paste of the code from the `org.lappsgrid.serialization.Serializer` class.  This class will be deprecated and removed in future releases.

## Solr Document Models

The `org.lappsgrid.eager.mining.core.solr` package contains two classes:

1. **Fields** provides `static final String` definitions for the fields indexed in each `SolrDocument`
1. **LappsDocument** provides helper methods for initializing the fields of a `SolrDocument`.

## SSL

Call the `SSL.enable()` method to allow a service to accept `https` connections.

## RabbitMQ Configuration

TBD

