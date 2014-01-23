package ly.stealth.kafkahttp;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.lifecycle.Managed;
import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;

public class KafkaService extends Service<KafkaConfiguration> implements Managed {
    public static void main(String[] args) throws Exception {
        new KafkaService().run(args);
    }

    private Producer<String, String> producer;

    @Override
    public void initialize(Bootstrap<KafkaConfiguration> bootstrap) {
        bootstrap.setName("kafka-http");
    }

    @Override
    public void run(KafkaConfiguration configuration, Environment environment) {
        ProducerConfig config = new ProducerConfig(configuration.producer.asProperties());
        producer = new Producer<String, String>(config);

        environment.manage(this);
        environment.addResource(new MessageResource(producer, configuration.consumer));
    }

    @Override
    public void start() throws Exception {

    }

    @Override
    public void stop() throws Exception {
        producer.close();
    }
}
