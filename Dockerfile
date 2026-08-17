# Build stage: compiles the WAR with Maven and the exact JDK the project targets.
FROM maven:3.9-eclipse-temurin-8 AS build
WORKDIR /workspace

# Copy the pom first so Maven's dependency cache layer only invalidates when
# dependencies change, not on every source edit.
COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B clean package -DskipTests

# Runtime stage: Tomcat 9 (javax.* namespace, matches JSF 2.3/CDI 2.0 here),
# no Maven or build tools shipped to production.
FROM tomcat:9.0-jdk8-temurin AS runtime

RUN rm -rf /usr/local/tomcat/webapps/ROOT \
    && groupadd --system tomcat \
    && useradd --system --gid tomcat --home /usr/local/tomcat tomcat \
    && chown -R tomcat:tomcat /usr/local/tomcat
USER tomcat

COPY --from=build --chown=tomcat:tomcat /workspace/target/jsf_tutorial.war \
    /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]
