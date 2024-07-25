package io.df.henry.example.file.sink;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;

public class SingleFileSinkTask extends SinkTask {
  private SingleFileSinkConnectorConfig config;
  private File file;
  private FileWriter fileWriter;

  @Override
  public String version() {
    return "1.0";
  }

  @Override
  public void start(Map<String, String> props) {
    config = new SingleFileSinkConnectorConfig(props);
    file = new File(config.getString(SingleFileSinkConnectorConfig.DIR_FILE_NAME));
    try {
      fileWriter = new FileWriter(file, true);
    } catch (IOException e) {
      throw new ConfigException(e.getMessage(), e);
    }

  }

  @Override
  public void put(Collection<SinkRecord> records) {
    try {
      for (SinkRecord record : records) {
        fileWriter.write(record.value().toString()+"\n");
      }
    } catch (IOException e) {
      throw new ConnectException(e.getMessage(), e);
    }
  }

  @Override
  public void flush(Map<TopicPartition, OffsetAndMetadata> currentOffsets) {
    try{
      // 버퍼에 있는 내용을 디스크에 작성
      fileWriter.flush();
    } catch (IOException e) {
      throw new ConnectException(e.getMessage(), e);
    }
  }

  @Override
  public void stop() {
    try {
      fileWriter.close();
    } catch (IOException e) {
      throw new ConnectException(e.getMessage(), e);
    }
  }
}
