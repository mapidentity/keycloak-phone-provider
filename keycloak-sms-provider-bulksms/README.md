# bulksms SMS Sender Provider

**Not verify in Quarkus 19.0.1**

```sh
cp target/providers/keycloak-phone-provider.jar ${KEYCLOAK_HOME}/providers/
cp target/providers/keycloak-phone-provider.resources.jar ${KEYCLOAK_HOME}/providers/
cp target/providers/keycloak-sms-provider-bulksms.jar ${KEYCLOAK_HOME}/providers/


${KEYCLOAK_HOME}/bin/kc.sh build

${KEYCLOAK_HOME}/bin/kc.sh start  --spi-phone-default-service=bulksms \
  --spi-message-sender-service-bulksms-url=${url} \
  --spi-message-sender-service-bulksms-username=${username} \
  --spi-message-sender-service-bulksms-password=${password}
  --spi-message-sender-service-bulksms-from=${from}
```
