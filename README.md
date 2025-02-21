# 📈 Real-Time Stock Market Backend (Spring Boot + Kafka) 🚀

This is the **backend service** for the real-time stock market simulation system. It processes stock updates from the **operator panel**, publishes them to **Kafka**, and distributes them to the **user dashboard** via **WebSockets**.

---

![WhatsApp Image 2025-02-20 at 19 05 40_7352b39a](https://github.com/user-attachments/assets/ffdfd403-7374-4f88-a116-501e0579d789)

---


![WhatsApp Image 2025-02-20 at 19 06 09_6c5b9a4e](https://github.com/user-attachments/assets/543aacb8-8157-47b2-9f89-db4dd6530202)

---


![image](https://github.com/user-attachments/assets/9a43dce1-befd-4c38-a29c-17d975d6788e)


---

## 🎯 Features

✅ **Kafka-based real-time messaging**  
✅ **WebSockets for live stock updates**  
✅ **Stock price management & API**  
✅ **MySQL database integration**  
✅ **REST API for operator interactions**  

---

## 🏗 Tech Stack

| Technology  | Purpose |
|-------------|---------|
| **Spring Boot** | Backend Framework |
| **Apache Kafka** | Message Broker |
| **Spring WebSockets** | Real-time updates |
| **MySQL** | Database |

---

## 🛠️ Installation & Setup

### 📌 **1️⃣ Clone the Repository**
```bash
git clone https://github.com/yourusername/stock-market-backend.git
cd stock-market-backend
```

---

## 🛢️ **2️⃣ Kafka Setup & Commands**

### 🔹 **Start Zookeeper**
```bash
bin/zookeeper-server-start.sh config/zookeeper.properties
```

### 🔹 **Start Kafka Broker**
```bash
bin/kafka-server-start.sh config/server.properties
```

### 🔹 **Create Kafka Topic**
```bash
bin/kafka-topics.sh --create --topic stock-updates --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
```

### 🔹 **List Kafka Topics**
```bash
bin/kafka-topics.sh --list --bootstrap-server localhost:9092
```

### 🔹 **Describe Kafka Topic**
```bash
bin/kafka-topics.sh --describe --topic stock-updates --bootstrap-server localhost:9092
```

### 🔹 **Produce Test Messages**
```bash
bin/kafka-console-producer.sh --topic stock-updates --bootstrap-server localhost:9092
```
_(Then type a message and press Enter)_

### 🔹 **Consume Messages**
```bash
bin/kafka-console-consumer.sh --topic stock-updates --from-beginning --bootstrap-server localhost:9092
```

---

## 🛢 **3️⃣ MySQL Setup & Commands**

### 🔹 **Start MySQL Server**
```bash
sudo systemctl start mysql
```

### 🔹 **Login to MySQL**
```bash
mysql -u root -p
```
_(Enter your password when prompted)_

### 🔹 **Create Database**
```sql
CREATE DATABASE stock_market;
```

### 🔹 **Update `application.properties`**
```
spring.datasource.url=jdbc:mysql://localhost:3306/stock_market
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
```

---

## 🔌 **4️⃣ Running the Spring Boot Application**

### 🔹 **Using Maven**
```bash
mvn clean install
mvn spring-boot:run
```

### 🔹 **Using Maven Wrapper**
```bash
./mvnw spring-boot:run
```

### 🔹 **Run in the Background (Linux/Mac)**
```bash
nohup mvn spring-boot:run &
```

### 🔹 **Check Logs**
```bash
tail -f logs/app.log
```

---

## 📡 **5️⃣ WebSocket Implementation**

This backend **listens for stock updates** from Kafka and **broadcasts them via WebSockets**:

```java
@Service
public class StockService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = "stock-updates", groupId = "stock-group")
    public void consumeStockUpdate(String stockUpdate) {
        messagingTemplate.convertAndSend("/topic/stocks", stockUpdate);
    }
}
```

---

## 📂 **Project Structure**

```
stock-market-backend/
│── src/main/java/com/stock/
│   ├── controller/       # REST API Controllers
│   ├── service/          # Business logic & Kafka listeners
│   ├── model/            # Stock entity definitions
│   ├── repository/       # Database handling
│   └── config/           # Kafka and WebSocket configurations
│── src/main/resources/
│   ├── application.properties  # Database & Kafka settings
│── pom.xml               # Project dependencies
│── README.md             # Project documentation
```

---

## 📌 **API Endpoints**

| Method | Endpoint             | Description                        |
|--------|----------------------|------------------------------------|
| GET    | `/api/stocks`        | Fetch all stock prices            |
| POST   | `/api/stocks/update` | Operator updates stock price      |
| WS     | `/topic/stocks`      | WebSocket for real-time updates   |

---

## 🛠️ **Debugging & Monitoring Commands**

### 🔍 **Check Kafka Logs**
```bash
tail -f logs/kafkaServer.log
```

### 🔍 **Check Zookeeper Logs**
```bash
tail -f logs/zookeeper.out
```

### 🔍 **Check Application Logs**
```bash
tail -f logs/app.log
```

### 🛠 **Stop Kafka & Zookeeper**
```bash
bin/kafka-server-stop.sh
bin/zookeeper-server-stop.sh
```

---

## Note :- 

You have to make setup of User Side and Operator Side also with this code inorder to make full working Project

