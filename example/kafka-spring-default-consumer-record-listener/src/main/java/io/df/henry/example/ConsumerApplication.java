package io.df.henry.example;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;

@SpringBootApplication
@Slf4j
public class ConsumerApplication {
  public static void main(String[] args) {
    SpringApplication.run(ConsumerApplication.class, args);
  }

  /**
   * <pre>
   * 기본적인 리스너 선언.
   * poll이 호출도어 가져온 레코드들은 차례대로 개별 레코드의 메시지 값을 파라미터로 받음.
   * ConsumerRecord로 받으므로 key, value 대한 처리를 수행.
   * </pre>
   * @param record
   */
  @KafkaListener(topics = "test", groupId = "test-group-00")
  public void recordListener(ConsumerRecord<String, String> record) {
    log.info(record.toString());
  }

  /**
   * 메시지 값을 받는 파라미터로 받음.
   * 역직렬화로 StringDeserializer를 사용하므로 String을 메시지 값을 받음.
   * @param messageValue
   */
  @KafkaListener(topics = "test", groupId = "test-group-01")
  public void singleTopicListener(String messageValue) {
    log.info(messageValue);
  }

  /**
   * 개벽 리스너에 컨슈머 옵션을 부여하고 싶다면 properties 옵션을 사용.
   * @param messageValue
   */
  @KafkaListener(
      topics = "test",
      groupId = "test-group-02",
      properties = {"max.poll.interval.ms:60000", "auto.offset.reset:earliest"})
  public void singleTopicWithPropertiesListener(String messageValue) {
    log.info(messageValue);
  }

  /**
   * 2개 이상의 카프카 컨슈머 스레드를 실행하려면 concurrency 옵션을 사용.
   * 해당 값만큼 스레드를 만들어 병렬 처리 한다.
   * @param messageValue
   */
  @KafkaListener(topics = "test", groupId = "test-group-03", concurrency = "3")
  public void concurrentTopicListener(String messageValue) {
    log.info(messageValue);
  }

  /**
   * 특정 토픽만 구독하고 싶다면 topicPartitions 파라미터를 사용
   * PartitionsOffset어노테이션을 활용하면 특정 파티션의 특정 오프셋까지 지정.
   * 이 경우 그룹아이디에 관계 없이 항상 설정한 오프셋의 데이터부터 가져옴.
   * @param record
   */
  @KafkaListener(
      topicPartitions = {
          @TopicPartition(topic = "test01", partitions = {"0","1"}),
          @TopicPartition(topic = "test02", partitionOffsets = @PartitionOffset(partition = "0", initialOffset = "3"))
      },
      groupId = "test-group-04"
  )
  public void listenSpecificPartition(ConsumerRecord<String, String> record) {
    log.info(record.toString());
  }
}
