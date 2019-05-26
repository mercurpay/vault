FROM openjdk:8u171-alpine3.7
RUN apk --no-cache add curl
COPY target/vault-*.jar vault.jar
CMD java ${JAVA_OPTS} -jar vault.jar