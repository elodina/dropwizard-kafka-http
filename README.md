# dropwizard-kafka-http

This project creates an Apache Kafka HTTP endpoint for producing and consuming
messages. It is built on [the Dropwizard framework] that makes Java REST
services operator-friendly and easy to deploy.

## Running in Development with Vagrant

A `Vagrantfile` is included in the project to quickly get up and running with
virtual machines to run ZooKeeper, Kafka, and the HTTP endpoint.

1. Install Vagrant <http://www.vagrantup.com/>
2. Install Virtual Box <https://www.virtualbox.org/>
3. `git clone https://github.com/stealthly/dropwizard-kafka-http`
4. `cd dropwizard-kafka-http`
5. `vagrant up`
6. `curl -d "topic=http&message=hello&key=0" "http://192.168.22.10:8080/message"`
7. `curl "http://192.168.22.10:8080/message?topic=http"`

Your original message produced from the consumer:

    [{"topic":"http","key":"0","message":"hello","partition":0,"offset":0}]

* Zookeeper will be running on 192.168.22.5
* dropwizard-kafka-http is built cleanly before Zookeeper installs in `vagrant/zk.sh`
* Kafka Broker One is running on 192.168.22.10
* dropwizard-kafka-http launches on 192.168.22.10 after the Kafka Broker starts in `vagrant/broker.sh`

If you want you can log in to the machines using `vagrant ssh zookeeper` and `vagrant ssh brokerOne`.

## Running in Your Own Environments

For deployment, you'll need to build the project with Maven. Dropwizard projects
produce "fat jars" so that you'll get a standalone binary ready to distribute
and run with your production Java runtime. Assuming that ZooKeeper and Kafka are
already running, edit the included `kafka-http.yml` to reflect the appropriate
addresses for the HTTP endpoint to connect to these services.

Then, use the [standard `server` Dropwizard command][dw server] to start up the
HTTP server on port 8080, passing the config file as argument:

    $ mvn package
    $ java -jar target/dropwizard-kafka-http-0.0.1-SNAPSHOT.jar server kafka-http.yml
    $ curl -d "topic=http&message=hello&key=0" http://localhost:8080/message
    $ curl "http://localhost:8080/message?topic=http"

You will also find an operator's interface with metrics and other info on port
8081.

It is recommended that you run the process under your preferred init or service
supervision tool. See [the Dropwizard configuration reference] for extensive
customization options for the HTTP server, logging, etc.

## dropwizard-kafka-http api methods

### Produce messages
```
POST /message

topic=$topic

message=$message0
key=$key0

message=$message1
key=$key1, ... etc

Produces messages on topic.

Parameters:
topic   - required topic name
key     - optional key text. Multiple values are possible
message - required message text. Multiple values are possible

Note: if keys are passed, then passed keys count should match messages count.

Errors:
400     - wrong parameters passed
```

### Consume messages
```
GET /message?topic=$topic&timeout=$timeout
Returns consumed messages available on topic.

Parameters:
topic   - required topic name
timeout - optional timeout in ms

Errors:
400     - wrong parameters passed
```

[the Dropwizard framework]: http://dropwizard.github.io/dropwizard/
[dw server]: http://dropwizard.github.io/dropwizard/getting-started.html#running-your-application
[the Dropwizard configuration reference]: http://dropwizard.github.io/dropwizard/manual/configuration.html

