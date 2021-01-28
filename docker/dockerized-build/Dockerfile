FROM openjdk:11

ARG module
ARG modulePath
ARG suffix

COPY --from=talkdesk-challenge-builder /usr/local/project/${modulePath}/target/${module}${suffix}-1.0.jar app.jar
COPY ./docker/extra/run.sh run.sh
COPY ./docker/extra/root-config.json root-config.json
COPY ./${module}/docker/config/config.json config.json

RUN chmod +x run.sh

ENTRYPOINT ["./run.sh"]
