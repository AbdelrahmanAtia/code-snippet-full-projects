1. build the gradle project
>**gradlew build**

2. build all services docker images
>**docker compose build**

3. verify that u can see the docker images
>* use wsl commandline for running this command"
> * the name of the docker image will be  [the folder Name that contains the docker compose file + the service host name that is defined inside the docker-compose.yml file] thats why we search by "compse" keyword which is part of the folder name
**docker images | grep compose**

4.  start all the microservices  containers in detached mode
> the compose command is not supported by wsl!! run it only on windows
commandline
>** docker compose up -d**

5. see the logs from all microservice 
>** docker compose logs -f **

6. test services by calling docker compose service
> curl localhost:8080/product-composite/123

7. shutdown all containers (microservices)
> docker compose down
