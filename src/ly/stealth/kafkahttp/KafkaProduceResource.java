package ly.stealth.kafkahttp;

import com.yammer.metrics.annotation.Timed;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Properties;
import java.util.Random;

@Path("/produce")
@Produces(MediaType.APPLICATION_JSON)
public class KafkaProduceResource {
    @POST
    @Timed
    public Response produce(
            @QueryParam("topic") String topic,
            @QueryParam("async") boolean async
    ) {
        // todo parse message from post body
        Random rnd = new Random();

        Properties props = new Properties();
        props.put("metadata.broker.list", "localhost:9092");
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        props.put("producer.type", async ? "async" : "sync");

        ProducerConfig config = new ProducerConfig(props);
        Producer<String, String> producer = new Producer<String, String>(config);

        String ip = "192.168.2." + rnd.nextInt(255);
        String msg = "1";

        KeyedMessage<String, String> data = new KeyedMessage<>(topic, ip, msg);
        producer.send(data);

        producer.close();

        return Response.ok().build();
    }
}
