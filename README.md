# Invoice Processing API

This Spring Boot application provides a REST API for processing XML invoices. The application accepts Base64-encoded XML invoices, validates them against an XSD schema, extracts specific data fields, and persists the information to a PostgreSQL database.

## Features

- **REST API Endpoint**: `POST /api/invoices` for processing invoice data
- **Base64 Decoding**: Automatic decoding of Base64-encoded XML content
- **XML Validation**: Validates XML against XSD schema (if provided)
- **Data Extraction**: Extracts NIP, P_1, and P_2 fields from invoice XML
- **Database Persistence**: Stores extracted data using Spring Data JPA
- **Exception Handling**: Comprehensive error handling with appropriate HTTP status codes
- **Logging**: Detailed logging for debugging and monitoring

## Technology Stack

- Java 24
- Spring Boot 3.5.3
- Spring Web
- Spring Data JPA
- PostgreSQL
- JAXB for XML processing
- Lombok for reducing boilerplate code
- Maven for dependency management

## Prerequisites

- Java 24 or higher
- Maven 3.6 or higher
- PostgreSQL database running on localhost:5432
- PostgreSQL database named 'postgres' with empty password for 'postgres' user

## Setup Instructions

### 1. Clone the Repository

```bash
git clone <repository-url>
cd invoice-processing-api
```

### 2. Database Setup

Ensure PostgreSQL is running and create the database if it doesn't exist:

```sql
CREATE DATABASE postgres;
```

The application is configured to connect to PostgreSQL with the following default settings:

- Host: localhost
- Port: 5432
- Database: postgres
- Username: postgres
- Password: (empty)

### 3. Configure Application Properties

Update `src/main/resources/application.properties` if your database configuration differs:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=your_password
```

### 4. Add XSD Schema (Optional)

If you have an XSD schema file for validation:

1. Create directory: `src/main/resources/xsd/`
2. Place your XSD file as: `src/main/resources/xsd/faktura.xsd`

### 5. Build and Run

```bash
# Build the application
mvn clean compile

# Run the application
mvn spring-boot:run
```

The application will start on port 8080 by default.

## API Usage

### Process Invoice

**Endpoint**: `POST /api/invoices`

**Request Body**:

```json
{
  "base64xml": "PEZha3R1cmEgeG1sbnM9Imh0dHA6Ly9jcmQuZ292LnBsL3d6b3IvMjAyMy8wNi8yOS8xMjY0OC8iPjxQb2RtaW90MT48RGFuZUlkZW50eWZpa2FjeWpuZT48TklQPjk3ODEzOTkyNTk8L05JUD48L0RhbmVJZGVudHlmaWthY3lqbmU+PC9Qb2RtaW90MT48RmE+PFBfMT4yMDIzLTA4LTMxPC9QXzE+PFBfMj5GSzIwMjMvMDgvMzE8L1BfMj48L0ZhPjwvRmFrdHVyYT4="
}
```

**Success Response** (HTTP 201 Created):

```json
{
  "message": "Invoice saved successfully"
}
```

**Error Response** (HTTP 400 Bad Request):

```json
{
  "error": "Invalid request",
  "message": "Detailed error message"
}
```

### Example with Sample XML

To test the API with the provided sample XML, first encode it to Base64:

```bash
# Linux/Mac
base64 -i sample.xml

# Windows
certutil -encode sample.xml sample_base64.txt
```

Then use the Base64 string in your API request.

## Testing

Run the unit tests with:

```bash
mvn test
```

The test suite includes:

- Service layer unit tests
- Success and failure scenarios
- Mock-based testing for isolated unit testing

## Database Schema

The application creates an `invoices` table with the following structure:

```sql
CREATE TABLE invoices (
    id SERIAL PRIMARY KEY,
    nip VARCHAR(20) NOT NULL,
    p1 VARCHAR(255) NOT NULL,
    p2 VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL
);
```

## Project Structure

```
src/
├── main/
│   ├── java/com/invoice/invoice/
│   │   ├── controller/      # REST controllers
│   │   ├── dto/            # Data transfer objects
│   │   ├── entity/         # JPA entities
│   │   ├── exception/      # Exception handlers
│   │   ├── repository/     # JPA repositories
│   │   ├── service/        # Business logic services
│   │   └── InvoiceApplication.java
│   └── resources/
│       ├── xsd/            # XSD schema files (optional)
│       └── application.properties
└── test/
    └── java/com/invoice/invoice/
        └── service/        # Unit tests
```

## Configuration Files

### application.properties

The main configuration file contains database and JPA settings. Key properties:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

## Logging

The application uses SLF4J with Logback for logging. Log levels can be configured in `application.properties`:

```properties
# Logging configuration (add these to application.properties if needed)
logging.level.com.invoice.invoice=DEBUG
logging.level.org.springframework.web=INFO
logging.level.org.hibernate.SQL=DEBUG
```

## Error Handling

The application implements comprehensive error handling:

- **400 Bad Request**: Invalid Base64, XML parsing errors, validation failures
- **500 Internal Server Error**: Unexpected application errors
- **Validation Errors**: Field-specific validation error messages

## Extending the Application

### Adding New Fields

To extract additional fields from the XML:

1. Add new columns to the `Invoice` entity
2. Create extraction methods in `XmlService`
3. Update the `InvoiceService.processInvoice()` method
4. Update database migration if needed

### Adding XSD Validation

1. Place your XSD file in `src/main/resources/xsd/faktura.xsd`
2. The application will automatically use it for validation
3. If validation fails, a 400 Bad Request response is returned

### Custom Exception Handling

Add new exception handlers in `GlobalExceptionHandler` for specific error scenarios.

## Troubleshooting

### Common Issues

1. **Database Connection Issues**

   - Ensure PostgreSQL is running
   - Verify connection parameters in `application.properties`
   - Check firewall settings

2. **XML Parsing Errors**

   - Verify Base64 encoding is correct
   - Ensure XML structure matches expected format
   - Check namespace declarations

3. **Missing Fields in XML**
   - Verify XML contains required elements: NIP, P_1, P_2
   - Check XML namespace and structure

### Debug Mode

Enable debug logging by adding to `application.properties`:

```properties
logging.level.com.invoice.invoice=DEBUG
```

## Production Considerations

- **Security**: Add authentication and authorization
- **Monitoring**: Integrate with monitoring tools (Micrometer, Actuator)
- **Database**: Use connection pooling and optimize queries
- **Validation**: Add more comprehensive XML validation rules
- **Documentation**: Consider adding OpenAPI/Swagger documentation

## Sample Request with curl

```bash
curl -X POST http://localhost:8080/api/invoices \
  -H "Content-Type: application/json" \
  -d '{
    "base64xml": "PEZha3R1cmEgeG1sbnM9Imh0dHA6Ly9jcmQuZ292LnBsL3d6b3IvMjAyMy8wNi8yOS8xMjY0OC8iPjxQb2RtaW90MT48RGFuZUlkZW50eWZpa2FjeWpuZT48TklQPjk3ODEzOTkyNTk8L05JUD48L0RhbmVJZGVudHlmaWthY3lqbmU+PC9Qb2RtaW90MT48RmE+PFBfMT4yMDIzLTA4LTMxPC9QXzE+PFBfMj5GSzIwMjMvMDgvMzE8L1BfMj48L0ZhPjwvRmFrdHVyYT4="
  }'
```

## Support

For issues or questions regarding this application:

1. Check the logs for detailed error messages
2. Verify your XML structure matches the expected format
3. Ensure all dependencies are properly installed
