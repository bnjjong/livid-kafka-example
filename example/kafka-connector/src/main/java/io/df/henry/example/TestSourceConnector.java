package io.df.henry.example;

import java.util.List;
import java.util.Map;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;

public class TestSourceConnector extends SourceConnector {

  /**
   * 커넥터의 버전을 리턴 함.
   * @return 버전 정보
   */
  @Override
  public String version() {
    return "";
  }

  /**
   * json, 또는 config 파일 형태의 입력한 설정값을 초기화하는 메소드이다.
   * 만약 올바른 값이 아니라면 ConnectException을 발생시킨다.
   * @param map configuration settings
   */
  @Override
  public void start(Map<String, String> map) {}

  /**
   * 이 커넥터가 사용할 커넥터 클래스를 정의
   * @return
   */
  @Override
  public Class<? extends Task> taskClass() {
    return null;
  }


  /**
   * 태스크 개수가 여러개 일때 설정값을 여러개 설정할때 사용.
   * @param i maximum number of configurations to generate
   * @return
   */
  @Override
  public List<Map<String, String>> taskConfigs(int i) {
    return List.of();
  }


  /**
   * 커넥터가 사용할 설정에 대한 정보를 받음
   * {@link ConfigDef} 클래스를 통해 설정의 이름 값, 중요도 등을 정의 할 수 있다.
    */
  @Override
  public ConfigDef config() {
    return null;
  }

  /**
   * 커넥터가 종료될때 필요한 로직을 정의
   */
  @Override
  public void stop() {

  }




}
