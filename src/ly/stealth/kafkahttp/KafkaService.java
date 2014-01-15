package ly.stealth.kafkahttp;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;

public class KafkaService extends Service<KafkaConfiguration> {
    public static void main(String[] args) throws Exception {
        new KafkaService().run(args);
    }

    @Override
    public void initialize(Bootstrap<KafkaConfiguration> bootstrap) {
        bootstrap.setName("kafka-http");
    }

    @Override
    public void run(KafkaConfiguration configuration, Environment environment) {
        environment.addResource(new MessageResource(configuration));
    }
}
