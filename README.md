# dropwizard-kafka-http

This project creates a REST kafka http endpoint for producing/consuming messages.

## Running Project

1) Install Vagrant [http://www.vagrantup.com/](http://www.vagrantup.com/)  
2) Install Virtual Box [https://www.virtualbox.org/](https://www.virtualbox.org/)  

In the main kafka folder  

1) vagrant up  
2) `curl -d "topic=http&message=hello&key=0" "http://192.168.22.10:8080/message"`
3) `curl "http://192.168.22.10:8080/message?topic=http"`

once this is done 
* Zookeeper will be running 192.168.86.5
* Broker 1 on 192.168.86.10
* All the tests in src/test/scala/* should pass  

If you want you can login to the machines using vagrant ssh <machineName> but you don't need to.

You can access the brokers and zookeeper by their IP from your local without having to go into vm.

e.g.

bin/kafka-console-producer.sh --broker-list 192.168.86.10:9092 --topic <topic name>

bin/kafka-console-consumer.sh --zookeeper 192.168.86.5:2181 --topic <topic name> --from-beginning

## Api methods

### Produce messages
```
POST /message?topic=$topic&async={true|false}&message=$message0&key=$key0,
              message=$message1,key=$key1, ...

Produces messages on topic.

Parameters:
topic   - required topic name
async   - optional true|false value indicating should the producer be async
key     - required key text. Multiple values are possible
message - required message text. Multiple values are possible

Note: passed keys count should match messages count.

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

