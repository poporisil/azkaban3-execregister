FROM frolvlad/alpine-oraclejdk8:slim

## Define default runtime ENV #################################

ENV SPRING_LOGLEVEL		info
ENV DATASOURCE_URL		jdbc:mysql://localhost:3306/azkaban
ENV DATASOURCE_USERNAME	azkaban
ENV DATASOURCE_PASSWORD	azkaban
ENV ETCD_URL			http://localhost:4001
ENV ETCD_EXECUTOR_KEY	/containers/poporisil/azkaban3-execservcer
ENV AZK_REST_URL		http://localhost:8081
ENV AZK_REST_USERNAME	admin
ENV AZK_REST_PASSWORD	azkaban
ENV REGISTER_INTERVAL	10000


## Install Application ################################

ADD build/libs/azkaban3-execregister.jar app.jar
RUN sh -c 'touch /app.jar'



## Run container ###############################################

ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Dspring.logLevel=$SPRING_LOGLEVEL -Dspring.datasource.url=$DATASOURCE_URL -Dspring.datasource.username=$DATASOURCE_USERNAME -Dspring.datasource.password=$DATASOURCE_PASSWORD -Detcd.url=$ETCD_URL -Detcd.executorKey=$ETCD_EXECUTOR_KEY -Dazkaban.rest.url=$AZK_REST_URL -Dazkaban.rest.username=$AZK_REST_USERNAME -Dazkaban.rest.password=$AZK_REST_PASSWORD -Dregister.interval=$REGISTER_INTERVAL -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]