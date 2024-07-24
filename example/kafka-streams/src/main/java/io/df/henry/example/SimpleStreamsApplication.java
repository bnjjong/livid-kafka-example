package io.df.henry.example;

import java.util.Properties;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;

public class SimpleStreamsApplication {
  public static final String APPLICATION_NAME = "streams-application"; // 애플리케이션 아이디 중복 x
  public static final String BOOTSTRAP_SERVERS = "localhost:9092";
  public static final String STREAM_LOG = "stream_log";
  public static final String STREAM_LOG_COPY = "stream_log_copy";

  public static void main(String[] args){
    Properties props = new Properties();
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, APPLICATION_NAME);
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);

    // 메시지 값의 역직렬화 직렬화 String 포맷으로 지정함.
    props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

    StreamsBuilder builder = new StreamsBuilder();
    KStream<String, String> streamLog = builder.stream(STREAM_LOG);
    // stream_log 토픽을 담은 KStream 객체를 다른 토픽으로 전송하기 위해 to 메소드 사용
    // KStream 인스턴스의 데이터들을 특정 토픽으로 저장하기 위한 용도로 사용한다.
    streamLog.to(STREAM_LOG_COPY);

    // 이 스트림즈 애플리케이션은 stream_log 토픽의 데이터를 stream_log_copy 토픽으로 전달 한다.
    KafkaStreams streams = new KafkaStreams(builder.build(), props);
    streams.start();
  }
}
