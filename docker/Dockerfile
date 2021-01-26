FROM openjdk:11 as talkdesk-challenge-build

COPY . /usr/local/project/
COPY ./docker/run.sh /
WORKDIR /usr/local/project/

RUN ./mvnw clean install

FROM openjdk:11

ARG module

COPY --from=talkdesk-challenge-build /usr/local/project/${module}/module/target/${module}-module-1.0.jar app.jar
COPY --from=talkdesk-challenge-build /run.sh run.sh
COPY ./docker/config/root-config.json root-config.json
COPY ./${module}/docker/config/config.json config.json

RUN chmod +x run.sh

ENTRYPOINT ["./run.sh"]
