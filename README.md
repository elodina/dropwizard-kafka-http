# dropwizard-kafka-http

This project creates an Apache Kafka HTTP endpoint for producing and consuming messages.

## Running Project

1) Install Vagrant [http://www.vagrantup.com/](http://www.vagrantup.com/)  
2) Install Virtual Box [https://www.virtualbox.org/](https://www.virtualbox.org/)  
3) git clone https://github.com/stealthly/dropwizard-kafka-http
4) cd dropwizard-kafka-http
5) vagrant up  
6) `curl -d "topic=http&message=hello&key=0" "http://192.168.22.10:8080/message"`    
7) `curl "http://192.168.22.10:8080/message?topic=http"`    

Your original message produced from the consumer `[{"topic":"http","key":"0","message":"hello","partition":0,"offset":0}]`

* Zookeeper will be running on 192.168.22.5
* dropwizard-kafka-http is built cleanly before Zookeeper installs in `vagrant/zk.sh`
* Broker One is running on 192.168.22.10
* dropwizard-kafka-http launches on 192.168.22.10 after the Kafka Broker starts in `vagrant/broker.sh`

If you want you can login to the machines using `vagrant ssh zookeeper` and `vagrant ssh brokerOne`.    

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

