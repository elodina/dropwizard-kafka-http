package ly.stealth.kafkahttp;

public class Message {
    private String topic;
    private String content;

    public Message(String topic, String content) {
        this.topic = topic;
        this.content = content;
    }

    public String getTopic() { return topic; }

    public String getContent() { return content; }
}
