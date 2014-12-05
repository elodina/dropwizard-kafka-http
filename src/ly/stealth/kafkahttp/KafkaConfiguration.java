package ly.stealth.kafkahttp;

import io.dropwizard.Configuration;
import kafka.api.OffsetRequest;

import java.util.Properties;

public class KafkaConfiguration extends Configuration {
    public Consumer consumer;
    public Producer producer;

    public static class Producer {
        public String metadataBrokerList;
        public String serializerClass;
        public String producerType;

        public Properties asProperties() {
            Properties p = new Properties();

            p.put("metadata.broker.list", metadataBrokerList);
            p.put("serializer.class", serializerClass);
            p.put("producer.type", producerType);
            p.put("producer.type", "async");

            return p;
        }
    }

    public static class Consumer {
        public String zookeeperConnect;
        public String groupId;
        public int consumerTimeoutMs;

        public Properties asProperties(Integer timeoutMs) {
            Properties p = new Properties();

            p.put("zookeeper.connect", zookeeperConnect);
            p.put("group.id", groupId);
            p.put("auto.offset.reset", OffsetRequest.SmallestTimeString());
            p.put("consumer.timeout.ms", "" + (timeoutMs != null ? timeoutMs : consumerTimeoutMs));

            return p;
        }
    }
}
