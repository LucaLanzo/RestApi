# Soft Skills REST API #


__To run the MongoDB database:__\
Download Docker Desktop from the official site:

https://www.docker.com/products/docker-desktop

After installing go to the directory of this project and type (-d flag to run database in the background):

```docker-compose up -d```

Default database username: admin\
Default database password: adminpassword\
Docker will create volume to keep the database saved under the docker volume name:\
"pvsexamss2020restapi_mongoSoftSkillsDatabaseVolume"

\
\
__To run the server:__

```mvn -DskipTests=true package```

This builds the jar to the directory 'target':

```java -jar target/PVSExamSS2020RestAPI-0.0.1-jar-with-dependencies.jar```

This will run the server.

\
\
__To POST a course ressource:__
```
{
    "courseName":"Teammanagement",
    "courseDescription":"Learn how to manage teams.",
    "maximumStudents":50
}
```

__To POST an event ressource:__
```
{
    "startTime":"2020-07-18--18:00:00",
    "endTime":"2020-07-18--20:00:00",
    "courseId":"5f0b776b1b0edf0238c0f502",
    "signedUpStudents":["k1111", "k22222"]
}
```
\
\
__Additional Dependency__\
_Mongo Java Driver: 3.12.5_



