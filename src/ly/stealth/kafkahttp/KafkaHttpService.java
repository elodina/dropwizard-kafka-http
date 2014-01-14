package ly.stealth.kafkahttp;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;

public class KafkaHttpService extends Service<KafkaHttpConfiguration> {
    public static void main(String[] args) throws Exception {
        new KafkaHttpService().run(args);
    }

    @Override
    public void initialize(Bootstrap<KafkaHttpConfiguration> bootstrap) {
        bootstrap.setName("kafka-http");
    }

    @Override
    public void run(KafkaHttpConfiguration configuration, Environment environment) {
        environment.addResource(new KafkaProduceResource());
    }
}
