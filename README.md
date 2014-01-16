# Dropwizard KafkaHTTP

This project creates a REST Apache Kafka HTTP endpoint for producing/consuming messages.

## Running Project

1) Install Vagrant [http://www.vagrantup.com/](http://www.vagrantup.com/)  
2) Install Virtual Box [https://www.virtualbox.org/](https://www.virtualbox.org/)  
3)
4) vagrant up  
5) `curl -d "topic=http&message=hello&key=0" "http://192.168.22.10:8080/message"`
6) `curl "http://192.168.22.10:8080/message?topic=http"`    

You will see `[{"topic":"http","key":"0","message":"hello","partition":0,"offset":0}]J`

* Zookeeper will be running on 192.168.22.5
* KafkaHTTP is built cleanly before Zookeeper installs in `vagrant/zk.sh`
* Broker One is running on 192.168.22.10
* KafkaHTTP launches on 192.168.22.10 after Kafka Broker starts in `vagrant/broker.sh`

If you want you can login to the machines using `vagrant ssh zookeeper` and `vagrant ssh brokerOne`.    

You can access the brokers and zookeeper also by their IP from your local without having to go into vm.    

## KafkaHTTP API methods

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

