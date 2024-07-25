package io.df.henry.example;

import java.util.Collection;
import java.util.Map;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.apache.kafka.connect.transforms.Transformation;

public class TestSinkTask extends SinkTask {

  /**
   * 버전을 리턴 보통 커넥터와 동일한 버전을 리턴 함.
   * @return
   */
  @Override
  public String version() {
    return "";
  }

  /**
   * 태스크가 시작할 때 필요한 로직 정의
   * 처리에 필요한 리소스를 여기서 초기화 한다.
   * @param props initial configuration
   */
  @Override
  public void start(Map<String, String> props) {

  }

  /**
   * 데이터 토픽을 주기적으로 가져오는 메서드
   * @param records the collection of records to send
   */
  @Override
  public void put(Collection<SinkRecord> records) {

  }

  /**
   * <pre>
   * put 메서드를 통해 가져온 데이터를 저장할때 사용하는 로직.
   * 만약 JDBC 커넥션을 맺고 MySQL에서 데이터를 저장한다면
   * put 에서 insert 를 하고 flush 에서 commit을 한다.
   * </pre>
   * @param currentOffsets the current offset state as of the last call to {@link #put(Collection)}, provided for
   *                       convenience but could also be determined by tracking all offsets included in the
   *                       {@link SinkRecord}s passed to {@link #put}. Note that the topic, partition and offset
   *                       here correspond to the original Kafka topic partition and offset, before any
   *                       {@link Transformation transformations} have been applied. These can be tracked by the task
   *                       through the {@link SinkRecord#originalTopic()}, {@link SinkRecord#originalKafkaPartition()}
   *                       and {@link SinkRecord#originalKafkaOffset()} methods.
   */
  @Override
  public void flush(Map<TopicPartition, OffsetAndMetadata> currentOffsets) {
    super.flush(currentOffsets);
  }

  /**
   * 태스크 종료 시 필요한 로직을 정의 함.
   * ex) JDBC 커넥션등과 같은 자원 종료.
   */
  @Override
  public void stop() {

  }
}
