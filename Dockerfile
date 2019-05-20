FROM oracle/graalvm-ce:1.0.0-rc15 as graalvm
COPY . /home/app/vault
WORKDIR /home/app/vault
RUN native-image --no-server -cp target/vault-*.jar

FROM frolvlad/alpine-glibc
EXPOSE 8080
COPY --from=graalvm /home/app/vault .
ENTRYPOINT ["./vault"]
