# DurvalCRM - Guia de Testes

## Estrutura de Testes

O projeto possui uma estrutura completa de testes unitários organizados da seguinte forma:

```
src/test/java/br/org/cecairbar/durvalcrm/
├── domain/
│   └── model/
│       ├── AssociadoTest.java
│       ├── MensalidadeTest.java
│       ├── VendaTest.java
│       └── DoacaoTest.java
├── application/
│   ├── doacao/
│   │   └── DoacaoServiceTest.java
│   └── usecase/
│       └── impl/
│           └── AssociadoUseCaseImplTest.java
└── infrastructure/
    └── web/
        └── resource/
            └── AssociadoResourceTest.java
```

## Executando os Testes

### Todos os testes
```bash
mvn test
```

### Testes específicos por categoria

#### Testes de Domínio (Entidades)
```bash
mvn test -Dtest=AssociadoTest,MensalidadeTest,VendaTest,DoacaoTest
```

#### Testes de Serviços
```bash
mvn test -Dtest=DoacaoServiceTest,AssociadoUseCaseImplTest
```

#### Testes de Recursos REST
```bash
mvn test -Dtest=AssociadoResourceTest
```

### Teste individual
```bash
mvn test -Dtest=NomeDaClasseTest
```

### Método específico
```bash
mvn test -Dtest=NomeDaClasseTest#nomeDoMetodo
```

## Cobertura de Código

### Gerar relatório de cobertura
```bash
mvn clean test jacoco:report
```

O relatório será gerado em: `target/site/jacoco/index.html`

### Verificar build completo com testes
```bash
mvn clean verify
```

A verificação de cobertura está desabilitada por padrão para flexibilidade.

### Habilitar verificação de cobertura (opcional)
Para projetos em produção, você pode habilitar a verificação editando o pom.xml:
- Alterar `<phase>none</phase>` para `<phase>verify</phase>` no plugin JaCoCo
- Alterar `<skip>true</skip>` para `<skip>false</skip>`

Configuração de limites: mínimo de 15% de cobertura de linhas.

## Integração Contínua (CI)

O projeto está configurado com GitHub Actions para:

1. **Execução de testes** em cada push/PR
2. **Geração de relatórios** de teste
3. **Análise de cobertura** de código
4. **Build do artefato WAR**
5. **Análise de qualidade** de código (SpotBugs, Checkstyle)

### Pipeline CI

- **test**: Executa todos os testes unitários
- **build**: Constrói o artefato WAR
- **code-quality**: Analisa a qualidade do código

## Tecnologias de Teste

- **JUnit 4**: Framework de testes
- **Mockito**: Mocking de dependências
- **JaCoCo**: Cobertura de código
- **H2 Database**: Banco de dados em memória para testes
- **RESTEasy**: Implementação JAX-RS para testes de recursos REST

## Boas Práticas Implementadas

1. **Isolamento**: Cada teste é independente
2. **Mocking**: Dependências são mockadas para testes unitários
3. **Nomenclatura**: Padrão `testNomeDoMetodo` ou `testCenarioEspecifico`
4. **Assertions**: Verificações completas de comportamento
5. **Exceções**: Testes de casos de erro e exceção
6. **Setup/Teardown**: Uso de `@Before` para preparar dados

## Cobertura Atual

Os testes cobrem:

- ✅ Entidades de domínio (Associado, Mensalidade, Venda, Doacao)
- ✅ Regras de negócio (validações, status, cálculos)
- ✅ Serviços de aplicação (CRUD, operações específicas)
- ✅ Use Cases (fluxos de negócio)
- ✅ Recursos REST (endpoints, responses)
- ✅ Tratamento de exceções
- ✅ Validações de dados

## Executando com Docker

Para executar os testes em um ambiente containerizado:

```bash
docker run -it --rm \
  -v "$(pwd)":/usr/src/app \
  -w /usr/src/app \
  maven:3.9-eclipse-temurin-17 \
  mvn clean test
```

## Troubleshooting

### Erro de dependências JAX-RS
Se encontrar erros relacionados a `RuntimeDelegate`, verifique se a dependência RESTEasy está presente:

```xml
<dependency>
    <groupId>org.jboss.resteasy</groupId>
    <artifactId>resteasy-core</artifactId>
    <version>6.2.5.Final</version>
    <scope>test</scope>
</dependency>
```

### Testes falhando por timeout
Aumente o timeout no surefire plugin:

```xml
<configuration>
    <forkedProcessTimeoutInSeconds>60</forkedProcessTimeoutInSeconds>
</configuration>
```

## Próximos Passos

1. Adicionar testes de integração com Arquillian
2. Implementar testes de contrato com REST Assured
3. Adicionar testes de performance com JMeter
4. Configurar mutation testing com PIT
5. Implementar testes E2E com Selenium