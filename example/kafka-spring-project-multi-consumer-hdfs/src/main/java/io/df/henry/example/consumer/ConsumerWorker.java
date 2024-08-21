package io.df.henry.example.consumer;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.webapp.hamlet2.Hamlet.P;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;

@Slf4j
public class ConsumerWorker implements Runnable{
  // 컨슈머 poll 메서드를 통해 전달받은 데이터의 임시 저장하는 버퍼
  // static 이므로 다수의 쓰레드에서 접근
  // 멀티 스레드에 환경에서 안전하게 사용가능한 ConcurrentHashMap 사용.
  private static Map<Integer, List<String>> bufferString = new ConcurrentHashMap<>();

  // 오프셋 값을 저장하고 파일 이름을 저장할 때 오프셋 번호를 붙이는데 사용.
  private static Map<Integer, Long> currentFileOffset = new ConcurrentHashMap<>();

  private final static int FLUSH_RECORD_COUNT = 10;
  private Properties prop;
  private String topic;
  private String threadName;
  private KafkaConsumer<String, String> consumer;

  public ConsumerWorker(Properties prop, String topic, int number) {
    log.info("Generate consumer worker!");
    this.prop = prop;
    this.topic = topic;
    this.threadName = "consumer-thread="+number;
  }

  public void stopAndWakeup() {
    log.info("stop and wakeup");
    consumer.wakeup();
    saveRemainBufferToHdfsFile();
  }

  private void saveRemainBufferToHdfsFile() {
    bufferString.forEach((p, v) -> this.save(p));
  }

  @Override
  public void run() {
    Thread.currentThread().setName(threadName);
    consumer = new KafkaConsumer<>(prop);
    consumer.subscribe(Arrays.asList(topic)); // 해당 토픽을 구독
    try{
      while(true) {
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1)); //여기서 에러 발생하나봄.

        for (ConsumerRecord<String, String> record : records) {
          // 버퍼에 쌓기 위해 아래 메서드를 호출
          addHdfsFileBuffer(record);
        }
        // polling이 완료되고 버퍼에 쌓이고 난뒤 아래 메서드를 호출하여 HDFS에 저장 하는 로직을 수행
        saveBufferToHdfsFile(consumer.assignment()); // 컨슈머 스레드에 할당된 파티션에 대한 버퍼 데이터만 적재
      }
    } catch (WakeupException e) {
      log.warn("wakeup consumer");
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    } finally{
      consumer.close();
    }
  }

  private void saveBufferToHdfsFile(Set<TopicPartition> partitions) {
    partitions.forEach(p -> checkFlushCount(p.partition()));

  }

  private void checkFlushCount(int partitionNo) {
    if (bufferString.get(partitionNo) != null) {
      if (bufferString.get(partitionNo).size() > FLUSH_RECORD_COUNT -1) {
        save(partitionNo);
      }
    }
  }

  private void save(int partitionNo) {
    if (bufferString.get(partitionNo).size() > 0) {
      try {
        String fileName = "/data/color-"+partitionNo+"-"+currentFileOffset.get(partitionNo)+ ".log";
        Configuration configuration = new Configuration();
        configuration.set("fs.defaultFS", "hdfs://localhost:9000");
        FileSystem hdfsFileSystem = FileSystem.get(configuration);
        FSDataOutputStream outputStream = hdfsFileSystem.create(new Path(fileName));
        outputStream.writeBytes(StringUtils.join(bufferString.get(partitionNo),"\n"));
        outputStream.close();

        bufferString.put(partitionNo, new ArrayList<>());

      } catch (IOException e) {
        log.error(e.getMessage(), e);
      }
    }
  }

  private void addHdfsFileBuffer(ConsumerRecord<String, String> record) {
    List<String> buffer = bufferString.getOrDefault(record.partition(), new ArrayList<>());
    buffer.add(record.value());
    bufferString.put(record.partition(),buffer);

    if (buffer.size() == 1) {
      currentFileOffset.put(record.partition(), record.offset());
    }
  }
}
