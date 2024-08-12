package io.df.henry.example;

import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.kafka.support.SendResult;

@SpringBootApplication
@Slf4j
public class KafkaSpringApplication implements CommandLineRunner {
  private static String TOPIC_NAME = "test";

  private KafkaTemplate<String, String> customKafkaTemplate;

  @Autowired
  public KafkaSpringApplication(
      @Qualifier("customKafkaTemplate") KafkaTemplate<String, String> customKafkaTemplate) {
    this.customKafkaTemplate = customKafkaTemplate;
  }

  public static void main(String[] args) {
    SpringApplication.run(KafkaSpringApplication.class, args);
  }

  /**
   * 컨슈머로 확인 하는 방법
   *
   * <pre>
   * bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 \
   * --topic test --from-beginning
   * Test0
   * Test1
   * Test2
   * Test3
   * Test4
   * Test5
   * Test6
   * Test7
   * Test8
   * Test9
   * </pre>
   *
   * @param args
   * @throws Exception
   */
  @Override
  public void run(String... args) throws Exception {
    CompletableFuture<SendResult<String, String>> future =
        customKafkaTemplate.send(TOPIC_NAME, "test99999");
    future
        .thenAccept(
            result -> { // 전송 성공시 콜백
              log.info(
                  "Message sent successfully: " + result.getProducerRecord().value());
            })
        .exceptionally(
            ex -> { // 예외 처리
              log.error("Message failed to send: " + ex.getMessage());
              return null;
            });



    customKafkaTemplate.setProducerListener(
        new ProducerListener<>() {
          @Override
          public void onSuccess(
              ProducerRecord<String, String> producerRecord, RecordMetadata recordMetadata) {
            System.out.println(
                "ProducerListener: Message sent successfully: " + producerRecord.value());
          }

          @Override
          public void onError(
              ProducerRecord<String, String> producerRecord,
              RecordMetadata recordMetadata,
              Exception exception) {
            System.err.println(
                "ProducerListener: Message failed to send: " + exception.getMessage());
          }
        });
    Thread.sleep(1000); // 비동기로 로그를 출력하므로
    System.exit(0);
  }
}
