dropwizard-kafka-http
=====================

This project creates a REST kafka http endpoint for producing/consuming messages.

The API methods are:

Produce messages
----------------
```
POST /message?topic=$topic&async={true|false}&message=$message0&key=$key0,message=$message1,key=$key1, ...
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

Consume messages
----------------
```
GET /message?topic=$topic&timeout=$timeout
Returns consumed messages available on topic.

Parameters:
topic   - required topic name
timeout - optional timeout in ms

Errors:
400     - wrong parameters passed
```

