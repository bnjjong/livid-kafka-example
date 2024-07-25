package io.df.henry.example;

import java.util.List;
import java.util.Map;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;

public class TestSourceTask extends SourceTask {

  /**
   * 버전을 리턴 보통 커넥터와 동일한 버전을 리턴 함.
   * @return
   */
  @Override
  public String version() {
    return "";
  }


  /**
   * 태스크가 시작할 때 필요한 로직을 정의 함.
   * @param props initial configuration
   */
  @Override
  public void start(Map<String, String> props) {

  }

  /**
   * 소스 애플리케이션, 소스 파일등에서 데이터를 읽어오는 로직을 작성.
   * 토픽으로 보낼 데이터는 {@link SourceRecord} 로 정의 한다.
   * @return 소스 레코드
   * @throws InterruptedException
   */
  @Override
  public List<SourceRecord> poll() throws InterruptedException {
    return List.of();
  }

  /**
   * 태스크 종료 시 필요한 로직을 정의 함.
   * ex) JDBC 커넥션등과 같은 자원 종료.
   */
  @Override
  public void stop() {

  }
}
