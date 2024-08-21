package io.df.henry.example;

import io.df.henry.example.consumer.ConsumerWorker;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;

@Slf4j
public class HdfsSinkApplication {
  public static final String BOOTSTRAP_SERVERS = "localhost:9092";
  public static final String TOPIC_NAME = "select-color";
  public static final String GROUP_ID = "color-hdfs-save-consumer-group";
  public static final int CONSUMER_COUNT = 3;
  public static final List<ConsumerWorker> workers = new ArrayList<>();

  public static void main(String[] args){
    // 안전한 컨슈머 종료를 위해 셧다운 훅을 선언
    // JVM 종료 될때 특정 작업을 실행하기 위해 사용.
    // 보통 자원을 해제 하거나 파일을 닫는 등의 정리 작업을 수행.
    Runtime.getRuntime().addShutdownHook(new ShutdownThread());
    Properties configs = new Properties();
    configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
    configs.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
    configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

    // 컨슈머 스레드를 스레드 풀로 관리하기 위해..
    // Executors는 스레드풀을 생성하기 위해 정적 팩토리 메서드를 제공 함.
    // newCachedThreadPool 특징
    // 재사용 가능한 스레드: 이전에 생성된 스레드가 있다면 재사용합니다.
    // 동적 크기: 스레드 풀의 크기가 고정되어 있지 않습니다. 60초 동안 사용되지 않은 스레드는 종료되고 제거
    // 비동기 작업 처리: submit() 또는 execute() 메서드를 통해 작업을 제출하면, 해당 작업이 비동기적으로 처리
    ExecutorService executorService = Executors.newCachedThreadPool();
    for (int i=0; i< CONSUMER_COUNT; i++) {
      // 컨슈머 스레드를 CONSUMER_COUNT 카운트 만큼 생성.
      workers.add(new ConsumerWorker(configs, TOPIC_NAME, i));
    }
    // 해당 컨슈머 스레드를 스레드풀에 포함시키는 작업.
    workers.forEach(executorService::execute);
  }

  static class ShutdownThread extends Thread {
    @Override
    public void run() {
      log.info("Shutdown hook");
      // 각 컨슈머 스레드에 종료를 알리도록 명시적으로 stopAndWakeup 메서드를 호출
      workers.forEach(ConsumerWorker::stopAndWakeup);
    }
  }

}
