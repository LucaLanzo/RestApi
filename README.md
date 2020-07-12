# REST API #


__To run the MongoDB database:__\
Download Docker Desktop from the official site:

https://www.docker.com/products/docker-desktop

After installing go to the directory of this project and type (-d flag to run database in the background):

```docker-compose up -d```

Default database username: admin\
Default database password: adminpassword\
Docker will create volume to keep the database saved under the name:\
"pvsexamss2020restapi_mongoSoftSkillsDatabaseVolume"


\
__To run the server:__

```mvn -U clean package```

This builds the jar to the directory 'target'. Change directory to 'target' and from there use command:

```java -jar PVSExamSS2020RestAPI-1.0-SNAPSHOT.jar```

This will run the server.

\
\
__General__\
_Java JDK: 13_\
_Maven: 4.0.0_\
_Tomcat: 8.5.13_\
_Jersey: 2.25.1_

__Plugins__\
_Maven Jar Plugin: 3.2.0_\
_Maven Compiler Plugin: 3.2_

__Dependencies__\
_JUnit:_ 5.7.0-M1\
_Javax Servlet: 4.0.1_\
_Genson: 1.6_\
_Apache Commons: 1.3.2_\
_Javax WS RS: 2.0_\
_OK HTTP: 4.7.2_\
_Commons Lang: 2.6_\
_Mongo Java Driver: 3.12.5_\
_Java XML Bind: 2.3.0_\
_JaxB Runtime: 2.3.0_\
_JavaX Activation: 1.1.1_



