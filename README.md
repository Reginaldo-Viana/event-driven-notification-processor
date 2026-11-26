# Event-Driven Notification Processor

Sistema de notificações assíncrono desenvolvido em Java com Spring Boot. Os eventos são publicados no Apache Kafka, processados por um consumidor resiliente e controlados com Redis para evitar processamento duplicado.

## Funcionalidades

- API REST para criação de notificações.
- Processamento assíncrono com Apache Kafka.
- Idempotência com Redis, usando o `eventId` como chave.
- Retry e Circuit Breaker com Resilience4j.
- Dead Letter Queue para eventos que não puderem ser processados.
- Métricas e health checks com Spring Boot Actuator.
- Ambiente local completo com Docker Compose.
- Testes de unidade e integração com suporte a Testcontainers.

## Arquitetura

```text
Cliente HTTP
    ↓
NotificationController
    ↓
NotificationPublisher
    ↓
Kafka (notifications)
    ↓
NotificationConsumer
    ↓
Redis (idempotência)
    ↓
ExternalNotificationService
    ↓
Entrega realizada ou evento enviado ao DLQ
```

### Tópicos Kafka

- `notifications`: eventos aguardando processamento.
- `notifications.dlq`: eventos que falharam no processamento.

## Tecnologias

- Java 21
- Spring Boot 4.2
- Apache Kafka
- Redis
- Resilience4j
- Maven
- Docker e Docker Compose
- JUnit 5 e Testcontainers

## Pré-requisitos

- Java 21
- Docker Desktop
- Maven, opcionalmente via Maven Wrapper incluído no projeto

## Executando com Docker Compose

Compile o projeto e inicie toda a infraestrutura:

```powershell
.\mvnw.cmd clean package -DskipTests
docker compose up --build
```

Serviços disponíveis:

- API: `http://localhost:8080`
- Kafka: `localhost:9092`
- Redis: `localhost:6379`

Para interromper e remover os containers:

```powershell
docker compose down
```

## Executando pelo GitHub Actions

O workflow **Notification Processor CI** valida automaticamente pushes e pull requests. Pela execução manual em **Actions > Notification Processor CI > Run workflow**, escolha o modo:

- `testes-selecionados`: executa apenas as classes ou métodos informados no campo `test_selector`.
- `todos-os-testes`: executa toda a suíte automatizada.
- `testes-e-build`: executa os testes, empacota a aplicação e executa um teste de aceitação com Kafka e Redis no Docker Compose.

O workflow **Run project** também pode ser iniciado manualmente em **Actions > Run project > Run workflow** para executar somente a aplicação com dependências, somente Kafka e Redis, ou a stack completa. Os containers sobem apenas durante o job do GitHub Actions e são encerrados no final; isso valida a execução, mas não deixa a aplicação hospedada continuamente.

## Criando uma notificação

```powershell
curl.exe -X POST http://localhost:8080/api/notifications `
  -H "Content-Type: application/json" `
  -d '{"recipient":"user@example.com","channel":"EMAIL","message":"Welcome"}'
```

Resposta:

```json
{
  "eventId": "123e4567-e89b-12d3-a456-426614174000",
  "status": "PENDING"
}
```

O HTTP `202 Accepted` indica que o evento foi aceito para processamento assíncrono.

### Testando o fluxo de falha e DLQ

Use o canal especial `FAIL`:

```powershell
curl.exe -X POST http://localhost:8080/api/notifications `
  -H "Content-Type: application/json" `
  -d '{"recipient":"user@example.com","channel":"FAIL","message":"Test DLQ"}'
```

Após as tentativas de retry, o evento será publicado no tópico `notifications.dlq`.

## Executando sem Docker

Se Kafka e Redis já estiverem disponíveis localmente:

```powershell
.\mvnw.cmd spring-boot:run
```

Configurações alternativas podem ser informadas por variáveis de ambiente:

```properties
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
REDIS_HOST=localhost
REDIS_PORT=6379
```

## Executando os testes

```powershell
.\mvnw.cmd test
```

Os testes cobrem a inicialização do contexto Spring e a criação de notificações pela API.

## Endpoints operacionais

Com o Spring Boot Actuator:

- `http://localhost:8080/actuator/health`
- `http://localhost:8080/actuator/metrics`
- `http://localhost:8080/actuator/prometheus`
- `http://localhost:8080/actuator/circuitbreakers`
- `http://localhost:8080/actuator/retries`

## Estrutura principal

```text
src/main/java/com/example/notificationprocessor/
├── EventDrivenNotificationProcessorApplication.java
├── KafkaConfiguration.java
├── NotificationController.java
├── NotificationPublisher.java
├── NotificationConsumer.java
├── NotificationEvent.java
├── CreateNotificationRequest.java
├── IdempotencyService.java
├── ExternalNotificationService.java
└── NotificationDeliveryException.java

src/test/java/com/example/notificationprocessor/
├── EventDrivenNotificationProcessorApplicationTests.java
└── NotificationControllerTest.java
```

## Ideias de evolução

- Consumir eventos reais de uma API de transações ou pagamentos.
- Persistir o estado das notificações em PostgreSQL.
- Implementar retentativas por backoff exponencial.
- Adicionar um consumidor dedicado ao DLQ.
- Integrar um provedor real de e-mail ou SMS.
- Usar OpenTelemetry para rastreamento distribuído.
- Publicar a aplicação em um cluster Kubernetes.
# event-driven-notification-processor
