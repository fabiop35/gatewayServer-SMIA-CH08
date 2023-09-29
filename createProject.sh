curl https://start.spring.io/starter.zip -o gatewayServer-SMIA-CH08.zip -d type=maven-project -d dependencies=devtools,actuator,cloud-config-client,cloud-eureka,cloud-gateway -d groupId=smia -d description="SMIA: Spring Cloud Gateway CH08" -d bootVersion=2.7.11 -d packageName=com.smia.gatewayserver -d artifactId=gatewayServer-CH08 -d name=SpringGatewayServer


#List of all available dependencies
curl -H 'Accept: application/json' https://start.spring.io | jq '.dependencies.values[].values[].id'
