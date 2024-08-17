FROM ubuntu:22.04


# Install dependencies
RUN apt-get update && apt-get install -y \
    openjdk-8-jdk-headless \
    wget \
    curl \
    vim \
    net-tools \
    ssh \
    rsync \
    sudo

# Create a Hadoop user
RUN useradd -m -s /bin/bash hadoop

# Set JAVA_HOME environment variable
RUN update-alternatives --config java

# Get the actual JAVA_HOME path
RUN echo "JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))" >> /etc/environment
ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-arm64

# Set HADOOP version
ENV HADOOP_VERSION=3.4.0

# Download and install Hadoop
RUN wget https://downloads.apache.org/hadoop/common/hadoop-$HADOOP_VERSION/hadoop-$HADOOP_VERSION.tar.gz \
    && tar -xzvf hadoop-$HADOOP_VERSION.tar.gz \
    && mv hadoop-$HADOOP_VERSION /usr/local/hadoop \
    && rm hadoop-$HADOOP_VERSION.tar.gz

# Set Hadoop environment variables
ENV HADOOP_HOME=/usr/local/hadoop
ENV PATH=$PATH:$HADOOP_HOME/bin:$HADOOP_HOME/sbin

# Set HDFS and YARN users
ENV HDFS_NAMENODE_USER=hadoop
ENV HDFS_DATANODE_USER=hadoop
ENV HDFS_SECONDARYNAMENODE_USER=hadoop
ENV YARN_RESOURCEMANAGER_USER=hadoop
ENV YARN_NODEMANAGER_USER=hadoop

# Create the logs directory and set the correct permissions
RUN mkdir -p /usr/local/hadoop/logs \
    && chown -R hadoop:hadoop /usr/local/hadoop/logs

# Switch to Hadoop user
USER hadoop

# SSH without key
RUN ssh-keygen -t rsa -P '' -f /home/hadoop/.ssh/id_rsa \
   && cat /home/hadoop/.ssh/id_rsa.pub >> /home/hadoop/.ssh/authorized_keys

# Switch back to root to adjust permissions
USER root
RUN chown -R hadoop:hadoop /home/hadoop/.ssh

# Expose ports
EXPOSE 9870 8088 19888 50070 10020

# Start SSH and Hadoop services
CMD service ssh start && \
    $HADOOP_HOME/bin/hdfs namenode -format -force && \
    $HADOOP_HOME/sbin/start-dfs.sh && \
    $HADOOP_HOME/sbin/start-yarn.sh && \
    bash