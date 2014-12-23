package ly.stealth.kafkahttp;

import com.codahale.metrics.health.HealthCheck;
import io.dropwizard.Application;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.apache.kafka.clients.producer.KafkaProducer;

public class KafkaApplication extends Application<KafkaConfiguration> implements Managed {
    public static void main(String[] args) throws Exception {
        new KafkaApplication().run(args);
    }

    private KafkaProducer producer;

    @Override
    public String getName() { return "kafka-http"; }

    @Override
    public void initialize(Bootstrap<KafkaConfiguration> bootstrap) {}

    @Override
    public void run(KafkaConfiguration configuration, Environment environment) {
        producer = new KafkaProducer(configuration.producer);
        environment.jersey().register(new MessageResource(producer, configuration.consumer));
        environment.healthChecks().register("empty", new EmptyHealthCheck());
    }

    @Override
    public void start() throws Exception {}

    @Override
    public void stop() throws Exception {
        producer.close();
    }

    private static class EmptyHealthCheck extends HealthCheck {
        @Override
        protected Result check() throws Exception {
            return Result.healthy();
        }
    }
}
