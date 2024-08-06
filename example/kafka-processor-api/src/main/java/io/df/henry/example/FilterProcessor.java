package io.df.henry.example;


import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;

public class FilterProcessor implements Processor<String, String> { // deprecated 되어 최신화 필요.
  /**
   * 클래스 프로세서에 대한 정보를 가짐.
   * 클래스로 생성된 인스턴스로 현재 스트림 처리 중인 토폴로지의 토픽 정보, 앱 아이디를 조회.
   */
  private ProcessorContext context;

  /**
   * 생성자
   * 프로세싱 처리에 필요한 리소스를 선언하는 구문이 들어감.
   * @param processorContext the context; may not be null
   */
  @Override
  public void init(ProcessorContext processorContext) {
    this.context = processorContext;
  }

  /**
   * 데이터 처리 로직이 들어가는 부분
   * 하나의 레코드를 받는 것을 가정하고 아래와 같이 key, value를 파라미터로 받음.
   * 필터링 처리 경우 {@link ProcessorContext#forward(Object, Object)} 를 사용.
   * 처리가 완료된 경우 {@link ProcessorContext#commit()} 을 사용.
    * @param key the key for the record
   * @param value the value for the record
   */
  @Override
  public void process(String key, String value) {
    if(value.length() > 5) {
      context.forward(key, value);
    }
    context.commit();
  }


  /**
   * 종료되기 전에 호출되는 메서드
   * 리소스 해제등의 구문이 들어감.
   */
  @Override
  public void close() {
  }
}
