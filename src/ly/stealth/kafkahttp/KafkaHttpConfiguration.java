package ly.stealth.kafkahttp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;

public class KafkaHttpConfiguration extends Configuration {
    @JsonProperty
    private String dummy;
}
