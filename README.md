# 🚀 HubSpot Integration API

## 📌 **Descrição do Projeto**
Este projeto é uma API REST desenvolvida em **Java 17** com **Spring Boot 3.2.5** para integrar-se com a API do HubSpot. A aplicação permite autenticação via **OAuth 2.0 (Authorization Code Flow)**, criação de contatos no CRM e recebimento de notificações via **webhooks**.

---

## 📂 **Arquitetura e Tecnologias Utilizadas**

### 🛠 **Tecnologias**
- **Java 17**
- **Spring Boot 3.2.5**
- **Spring Security OAuth2 Client** (para autenticação via OAuth 2.0)
- **Spring WebFlux** (para chamadas assíncronas à API do HubSpot)
- **Redis** (para armazenamento de tokens OAuth2)
- **Spring Data Redis** (para integração com Redis)
- **Spring Boot Actuator** (para monitoramento da aplicação)
- **JUnit 5 e Spring Test** (para testes de integração)

---

## 📌 **Configuração do Redis**

O Redis é utilizado para armazenar os tokens OAuth2 com segurança, evitando reautenticações desnecessárias.

### 1️⃣ **Instalando o Redis**

#### **🔧 No Linux (Ubuntu/Debian)**
```bash
sudo apt update
sudo apt install redis-server
sudo systemctl enable redis
sudo systemctl start redis
```

#### **🐳 Usando Docker**
```bash
docker run --name redis -p 6379:6379 -d redis
```

---

## 📌 **Endpoints Implementados**

### 🔑 **1. Geração da Authorization URL**
**Endpoint:** `GET /auth/authorize`  
**Descrição:** Retorna a URL de autorização para iniciar o fluxo OAuth2 com o HubSpot.

```json
{
    "authorization_url": "https://app.hubspot.com/oauth/authorize"
}
```

### 🔄 **2. Processamento do Callback OAuth**
**Endpoint:** `GET /auth/callback?code={code}`  
**Descrição:** Recebe o código de autorização, troca pelo token de acesso e armazena no Redis.

```json
{
    "access_token": "abc123",
    "refresh_token": "xyz456",
    "expires_in": 3600
}
```

### 📝 **3. Criação de Contatos**
**Endpoint:** `POST /contacts`  
**Descrição:** Cria um contato no HubSpot respeitando as políticas de rate limit.

```json
{
    "email": "usuario@email.com",
    "first_name": "João",
    "last_name": "Silva",
    "phone": "+5511999999999"
}
```

### 🛠️ **4. Atualização de Contatos**
**Endpoint:** `PUT /contacts/{contactId}`  
**Descrição:** Atualiza as informações de um contato existente no HubSpot.

```json
{
    "email": "usuario_novo@email.com",
    "first_name": "João",
    "last_name": "Silva",
    "phone": "+5511888888888"
}
```

### 📬 **5. Webhook para Criação de Contatos**
**Endpoint:** `POST /webhooks`  
**Descrição:** Processa eventos `contact.creation` enviados pelo HubSpot.

```json
{
    "event": "contact.creation",
    "contactId": "123456",
    "properties": {
        "email": "usuario@email.com",
        "first_name": "João",
        "last_name": "Silva"
    }
}
```

---

## 📌 **Executando a Aplicação**

### 🔹 **Pré-requisitos**
- Java 17 instalado
- Docker
- Redis em execução

### 🔹 **Rodando a aplicação**

1. Clone o repositório:
   ```bash
   git clone https://github.com/lobatopedro/hubspot-integration.git
   cd hubspot-integration
   ```

2. Configure as variáveis de ambiente no `.env`:
   ```bash
   HUBSPOT_CLIENT_ID=your_client_id
   HUBSPOT_CLIENT_SECRET=your_client_secret
   HUBSPOT_REDIRECT_URI=http://localhost:8080/auth/callback
   REDIS_HOST=localhost
   REDIS_PORT=6379
   ```

3. Execute a aplicação com Maven:
   ```bash
   ./mvnw spring-boot:run
   ```

4. Acesse a API em `http://localhost:8080`

---

## 📌 **Executando com Docker Compose**

Para rodar a aplicação com Redis usando **Docker Compose**, utilize:

```yaml
version: '3.8'

services:
  redis:
    image: redis
    container_name: redis-container
    ports:
      - "6379:6379"

  hubspot-api:
    build: .
    container_name: hubspot-integration
    ports:
      - "8080:8080"
    environment:
      - HUBSPOT_CLIENT_ID=your_client_id
      - HUBSPOT_CLIENT_SECRET=your_client_secret
      - HUBSPOT_REDIRECT_URI=http://localhost:8080/auth/callback
      - REDIS_HOST=redis
      - REDIS_PORT=6379
    depends_on:
      - redis
```

Para iniciar os containers:
```bash
docker-compose.yml up -d
```

Isso iniciará a aplicação e o Redis automaticamente.

---

## 📌 **Testes**

Para executar os testes automatizados:

```bash
./mvnw test
```

---

