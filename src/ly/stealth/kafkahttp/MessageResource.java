package ly.stealth.kafkahttp;

import com.google.common.base.Strings;
import com.yammer.metrics.annotation.Timed;
import kafka.api.OffsetRequest;
import kafka.consumer.*;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.javaapi.producer.Producer;
import kafka.message.MessageAndMetadata;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.util.*;

@Path("/message")
@Produces(MediaType.APPLICATION_JSON)
public class MessageResource {
    @POST
    @Timed
    public Response produce(
            @QueryParam("topic") String topic,
            @QueryParam("async") boolean async
    ) {
        // todo parse message from post body
        Random rnd = new Random();

        Properties props = new Properties();
        props.put("metadata.broker.list", "localhost:9092"); // todo configure
        props.put("serializer.class", "kafka.serializer.StringEncoder"); // todo configure
        props.put("producer.type", async ? "async" : "sync");

        ProducerConfig config = new ProducerConfig(props);
        Producer<String, String> producer = new Producer<>(config);

        String ip = "192.168.2." + rnd.nextInt(255); // todo use message
        String msg = "1";

        KeyedMessage<String, String> data = new KeyedMessage<>(topic, ip, msg);
        producer.send(data);

        producer.close(); // todo close in finally

        return Response.ok().build();
    }

    @GET
    @Timed
    public Response consume(
            @QueryParam("topic") String topic
    ) {
        if (Strings.isNullOrEmpty(topic))
            return Response.status(400).build();

        Properties props = new Properties();
        props.put("zookeeper.connect", "localhost:2181"); // todo configure
        props.put("group.id", "group"); // todo configure
        props.put("auto.offset.reset", OffsetRequest.SmallestTimeString()); // todo what's this ?
        props.put("consumer.timeout.ms", "100"); // todo configure

        ConsumerConfig config = new ConsumerConfig(props);
        ConsumerConnector connector = Consumer.createJavaConsumerConnector(config);

        Map<String, Integer> streamCounts = Collections.singletonMap(topic, 1);
        Map<String, List<KafkaStream<byte[], byte[]>>> streams = connector.createMessageStreams(streamCounts);
        KafkaStream<byte[], byte[]> stream = streams.get(topic).get(0);

        List<Message> messages = new ArrayList<>();
        try {
            for (MessageAndMetadata<byte[], byte[]> messageAndMetadata : stream) {
                Message message = new Message(messageAndMetadata.topic(), new String(messageAndMetadata.message(), "UTF-8"));
                messages.add(message);
            }
        } catch (ConsumerTimeoutException | UnsupportedEncodingException ignore) {
        } finally {
            connector.commitOffsets();
            connector.shutdown();
        }

        return Response.ok(messages).build();
    }
}
