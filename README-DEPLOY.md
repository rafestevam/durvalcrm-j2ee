# DurvalCRM J2EE - Instruções de Deploy

## Deploy via Maven

O projeto está configurado com o plugin `wildfly-maven-plugin` para realizar deploy direto no servidor WildFly.

### Comandos de Deploy

```bash
# Build e deploy da aplicação
mvn clean package wildfly:deploy

# Apenas deploy (após build)
mvn wildfly:deploy

# Redesploy (atualiza aplicação já deployada)
mvn wildfly:redeploy

# Undeploy (remove aplicação do servidor)
mvn wildfly:undeploy
```

### Configuração do Servidor

O plugin está configurado para conectar em:
- **Host**: 20.127.155.169
- **Porta**: 9990 (Management Port)
- **Protocolo**: HTTPS-Remoting
- **Usuário**: admin
- **Senha**: wildfly@2025

### Opções Adicionais

Para usar credenciais diferentes das configuradas no pom.xml:

```bash
mvn wildfly:deploy -Dwildfly.username=seu_usuario -Dwildfly.password=sua_senha
```

Para especificar um servidor diferente:

```bash
mvn wildfly:deploy -Dwildfly.hostname=outro.servidor -Dwildfly.port=9990
```

### Troubleshooting

Se encontrar problemas de certificado SSL (devido ao certificado auto-assinado):
- O plugin já está configurado com `<skip-ssl-validation>true</skip-ssl-validation>`
- Se ainda houver problemas, adicione ao comando Maven:
  ```bash
  mvn wildfly:deploy -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true
  ```

### Verificação do Deploy

Após o deploy, a aplicação estará disponível em:
- **URL**: https://20.127.155.169:8443/durvalcrm-j2ee

### Logs

Para verificar os logs do deploy:
```bash
mvn wildfly:deploy -X  # Debug mode
```

Os logs do servidor podem ser visualizados no console de administração do WildFly:
- https://20.127.155.169:9990

## Endpoints da API

| Endpoint | URL |
|----------|-----|
| Health | `/api/health` |
| Associados | `/api/associados` |
| Mensalidades | `/api/mensalidades` |
| Doações | `/api/doacoes` |
| Vendas | `/api/vendas` |
| Dashboard | `/api/dashboard` |

## Build e Deploy Rápido

```bash
# Para desenvolvimento
mvn clean package wildfly:deploy

# Para produção (skip tests)
mvn clean package wildfly:deploy -DskipTests -Pprod
```

## Configuração Alternativa via Settings.xml

Você pode configurar as credenciais no arquivo `~/.m2/settings.xml` para não expor senhas no pom.xml:

```xml
<settings>
  <servers>
    <server>
      <id>wildfly-remote</id>
      <username>admin</username>
      <password>wildfly@2025</password>
    </server>
  </servers>
</settings>
```

E então referenciar no pom.xml:
```xml
<configuration>
  <id>wildfly-remote</id>
  ...
</configuration>
```