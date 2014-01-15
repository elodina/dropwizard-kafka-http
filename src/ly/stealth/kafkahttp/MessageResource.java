package ly.stealth.kafkahttp;

import com.google.common.base.Strings;
import com.yammer.metrics.annotation.Timed;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerTimeoutException;
import kafka.consumer.KafkaStream;
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
    private KafkaConfiguration configuration;

    public MessageResource(KafkaConfiguration configuration) {
        this.configuration = configuration;
    }

    @POST
    @Timed
    public Response produce(
            @QueryParam("topic") String topic,
            @QueryParam("async") Boolean async
    ) {
        Properties props = configuration.producer.asProperties();
        if (async != null) props.put("producer.type", async ? "async" : "sync");
        ProducerConfig config = new ProducerConfig(props);

        String key = "key";
        String msg = "message";
        KeyedMessage<String, String> data = new KeyedMessage<>(topic, key, msg);

        Producer<String, String> producer = new Producer<>(config);
        try { producer.send(data); }
        finally { producer.close(); }

        return Response.ok().build();
    }

    @GET
    @Timed
    public Response consume(
            @QueryParam("topic") String topic
    ) {
        if (Strings.isNullOrEmpty(topic))
            return Response.status(400).build();

        ConsumerConfig config = new ConsumerConfig(configuration.consumer.asProperties());
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
