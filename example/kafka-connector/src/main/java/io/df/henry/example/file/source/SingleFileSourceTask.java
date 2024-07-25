package io.df.henry.example.file.source;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SingleFileSourceTask extends SourceTask {
  Logger logger = LoggerFactory.getLogger(SingleFileSourceTask.class);
  // 아래 두개의 키를 기준으로 오프셋 스토리지에 읽은 위치를 저장
  public final String FILENAME_FIELD = "filename";
  public final String POSITION_FIELD = "position";
  
  // 오프셋 스토리지에 데이터를 저장하고 읽을 때 Map 자료구조에 담은 데이터를 사용.
  // filename이 키, 커넥터가 읽는 파일이름이 값으로 저장됨.
  private Map<String, String> fileNamePartition;
  private Map<String, Object> offset;
  private String topic;
  private String file;
  
  // 읽은 파일 위치를 커넥터 멤버 변수로 지정.
  private long position = -1;




  @Override
  public String version() {
    return "1.0";
  }

  @Override
  public void start(Map<String, String> props) {
    // init
    try{
      // config 정보에서 토픽명과 파일명을 사용
      SingleFileSourceConnectorConfig config =
          new SingleFileSourceConnectorConfig(props);
      topic = config.getString(SingleFileSourceConnectorConfig.TOPIC_NAME);
      file = config.getString(SingleFileSourceConnectorConfig.DIR_FILE_NAME);

      fileNamePartition = Collections.singletonMap(FILENAME_FIELD, file);

      // 오프셋 스토리지에서 현재 읽고자 하는 파일 정보를 가져온다.
      // 실제 데이터가 저장되는 곳으로 단일 모드 커넥트는 로컬 파일로 저장
      // 분산 모드의 경우 내부 토픽에 저장됨.
      offset = context.offsetStorageReader().offset(fileNamePartition);
      if (offset != null) {
        // 오프셋 스토리지에 가져온 마지막 처리한 지점을 가져옴.
        Object lastReadFileOffset = offset.get(POSITION_FIELD);
        if (lastReadFileOffset != null) {
          position = (long) lastReadFileOffset;
        } else {
          // 파일을 처리한 적이 없다는 의미로 0을 할당함.
          position = 0;
        }
      }
    } catch (Exception e) {
      throw  new ConnectException(e.getMessage(), e);
    }

  }

  /**
   * 지속적으로 데이터를 가져오기 위해 반복 호출 됨.
   * 메서드 내부에서 소스파일을 데이터를 읽어서 토픽으로 데이터를 보내야 함.
   * @return List 토픽으로 보낼 레코드.
   * @throws InterruptedException
   */
  @Override
  public List<SourceRecord> poll() throws InterruptedException {
    List<SourceRecord> results = new ArrayList<>();
    try{
      Thread.sleep(1000);
      // 한줄씩 읽어오는 과정
      List<String> lines = getLines(position);
      if (lines.size() > 0) {
        lines.forEach(line -> {
          Map<String, Long> sourceOffset = Collections.singletonMap(POSITION_FIELD, ++position);
          SourceRecord sourceRecord = new SourceRecord(fileNamePartition, sourceOffset, topic,
              Schema.STRING_SCHEMA, line);
          results.add(sourceRecord);
        });
      }
      return results;
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      throw new ConnectException(e.getMessage(), e);
    }
  }

  private List<String> getLines(long readLine) throws IOException {
    BufferedReader reader = Files.newBufferedReader(Paths.get(file));
    return reader.lines().skip(readLine).collect(Collectors.toList());
  }


  @Override
  public void stop() {
  }
}
