#!/bin/sh
echo "========== Starting App =========="
echo "SPRING_DATASOURCE_URL: $SPRING_DATASOURCE_URL"
echo "SPRING_DATASOURCE_USERNAME: $SPRING_DATASOURCE_USERNAME"
echo "SPRING_DATASOURCE_PASSWORD: [HIDDEN]"
echo "JWT_SECRET: [HIDDEN]"

# Usar perfil prod por defecto si no está definido
PROFILE=${SPRING_PROFILES_ACTIVE:-prod}
echo "Starting with Spring profile: $PROFILE"

exec java -jar app.jar --spring.profiles.active=$PROFILE
