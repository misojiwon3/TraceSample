FROM openjdk:jre-alpine
WORKDIR /opt/trace
ADD --chown=daemon:daemon opt /opt
EXPOSE 9005
USER daemon
ENTRYPOINT ["/opt/trace/bin/trace"]
CMD []
