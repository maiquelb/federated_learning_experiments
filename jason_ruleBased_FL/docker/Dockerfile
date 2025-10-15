FROM eclipse-temurin:21-jdk-alpine

RUN apk add --no-cache git

RUN git clone https://github.com/maiquelb/federated_learning_experiments.git

# Define variáveis de ambiente
ENV GRADLE_VERSION=8.5
ENV GRADLE_HOME=/opt/gradle
ENV PATH=${GRADLE_HOME}/bin:${PATH}

# Baixa e instala o Gradle
RUN wget https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip -P /tmp && \
    unzip -d /opt /tmp/gradle-${GRADLE_VERSION}-bin.zip && \
    mv /opt/gradle-${GRADLE_VERSION} ${GRADLE_HOME} && \
    rm /tmp/gradle-${GRADLE_VERSION}-bin.zip	
    

CMD ["cd /federated_learning_experiments && git pull"]


#sudo docker build -t maiquelb/federated-learning:0.01 .

#sudo docker run -it --rm maiquelb/federated-learning:0.01 /bin/sh



#sudo docker run -it --rm -p8081:8081 -w /federated_learning_experiments/jason_ruleBased_FL/distributed maiquelb/federated-learning:0.01  /bin/sh -c "git pull &&  gradle alice"
#sudo docker run -it --rm -p8082:8082 -w /federated_learning_experiments/jason_ruleBased_FL/distributed maiquelb/federated-learning:0.01  /bin/sh -c "git pull &&  gradle bob"
