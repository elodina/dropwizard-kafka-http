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

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.util.*;

@Path("/message")
@Produces(MediaType.APPLICATION_JSON)
public class MessageResource {
    private Producer<String, String> producer;
    private KafkaConfiguration.Consumer consumerCfg;

    public MessageResource(Producer<String, String> producer, KafkaConfiguration.Consumer consumerCfg) {
        this.producer = producer;
        this.consumerCfg = consumerCfg;
    }

    @POST
    @Timed
    public Response produce(
            @FormParam("topic") String topic,
            @FormParam("key") List<String> keys,
            @FormParam("message") List<String> messages
    ) {
        List<String> errors = new ArrayList<String>();
        if (Strings.isNullOrEmpty(topic)) errors.add("Undefined topic");

        if (keys.isEmpty()) errors.add("Undefined key");
        if (messages.isEmpty()) errors.add("Undefined message");
        if (keys.size() != messages.size()) errors.add("Messages count != keys count");

        if (!errors.isEmpty())
            return Response.status(400)
                    .entity(errors)
                    .build();

        assert keys != null;
        assert messages != null;

        List<KeyedMessage<String, String>> keyedMessages = new ArrayList<KeyedMessage<String, String>>();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String message = messages.get(i);
            keyedMessages.add(new KeyedMessage<String, String>(topic, key, message));
        }

        producer.send(keyedMessages);
        return Response.ok().build();
    }

    @GET
    @Timed
    public Response consume(
            @QueryParam("topic") String topic,
            @QueryParam("timeout") Integer timeout
    ) {
        if (Strings.isNullOrEmpty(topic))
            return Response.status(400)
                    .entity(new String[]{"Undefined topic"})
                    .build();

        Properties props = consumerCfg.asProperties(timeout);
        ConsumerConfig config = new ConsumerConfig(props);
        ConsumerConnector connector = Consumer.createJavaConsumerConnector(config);

        Map<String, Integer> streamCounts = Collections.singletonMap(topic, 1);
        Map<String, List<KafkaStream<byte[], byte[]>>> streams = connector.createMessageStreams(streamCounts);
        KafkaStream<byte[], byte[]> stream = streams.get(topic).get(0);

        List<Message> messages = new ArrayList<Message>();
        try {
            for (MessageAndMetadata<byte[], byte[]> messageAndMetadata : stream)
                messages.add(new Message(messageAndMetadata));
        } catch (ConsumerTimeoutException ignore) {
        } finally {
            connector.commitOffsets();
            connector.shutdown();
        }

        return Response.ok(messages).build();
    }

    public static class Message {
        public String topic;

        public String key;
        public String message;

        public int partition;
        public long offset;

        public Message(MessageAndMetadata<byte[], byte[]> message) {
            this.topic = message.topic();

            try {
                this.key = new String(message.key(), "UTF-8");
                this.message = new String(message.message(), "UTF-8");
            } catch (UnsupportedEncodingException impossible) { /* ignore */ }

            this.partition = message.partition();
            this.offset = message.offset();
        }
    }
}
