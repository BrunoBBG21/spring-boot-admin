# spring-boot-admin
Simple spring-boot-admin fork from https://github.com/codecentric/spring-boot-admin for token autentication


## Client Config
Just put in application.properties:

```
spring.boot.admin.client.enabled=true
spring.boot.admin.url=http://spring-boot-admin.com/
spring.boot.admin.client.metadata.user.name=actuator 
spring.boot.admin.client.metadata.user.password=123
spring.boot.admin.client.metadata.url.login=http://spring-boot-client.com/login
#seconds
spring.boot.admin.client.metadata.token.lifetime=300
spring.boot.admin.client.service-url=http://spring-boot-client.com
```