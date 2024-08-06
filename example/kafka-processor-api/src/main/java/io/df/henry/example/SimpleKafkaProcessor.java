package io.df.henry.example;

import java.util.Properties;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;

public class SimpleKafkaProcessor {
  public static final String APPLICATION_NAME = "processor-application";
  public static final String BOOTSTRAP_SERVERS = "localhost:9092";
  public static final String STREAM_LOG = "stream_log";
  public static final String STREAM_LOG_FILTER = "stream_log_filter";

  public static void main(String[] args){
    Properties props = new Properties();
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, APPLICATION_NAME);
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

    Topology topology = new Topology();    // 프로세스 API를 사용한 토폴로지를 구성하기 위해 사용.
        topology.addSource("Source"        // 토픽을 가져옴.
            , STREAM_LOG)
        .addProcessor("Process",    // 스트림 프로세스 정의
            () -> new FilterProcessor(),   // 내가 정의한 프로세스
            "Source")      // 부모 노드명
        .addSink("Sink",           // 싱크 프로세스로 데이터를 저장.
            STREAM_LOG_FILTER,            // 저장할 토픽명
            "Process");   // 부모 노드명
    KafkaStreams streaming = new KafkaStreams(topology, props); // 스트림즈 생성 및 실행
    streaming.start();
  }
}
