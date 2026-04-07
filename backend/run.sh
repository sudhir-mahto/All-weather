#!/usr/bin/env bash
# Run Spring Boot using the jenv-local Java 17 without changing global JAVA_HOME
JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home \
  mvn spring-boot:run
