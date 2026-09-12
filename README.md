# HistData application

Synthetic Spring Boot application for learning a production-style DevOps flow.
It lists historical `.dat` files, streams a selected file to an authenticated
client, and writes the result to Oracle as an audit record.

## Local architecture

```text
Browser -> Spring Boot -> local-data/*.dat
                      -> Oracle container (users, subscriptions, downloads)
```

Production replaces the local filesystem with S3. The same application binary
is used; only configuration changes.

## Prerequisites

- Java 17
- Maven 3.9+
- Docker with Compose

## Run locally

```bash
docker compose up -d
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

Or run both Oracle and the containerized application:

```bash
docker compose --profile full up --build
```

Open `http://localhost:8080` and use one of the local lab accounts:

- Client: `client01` / value of `SEED_CLIENT_PASSWORD` (default `ChangeMeClient1!`)
- Admin: `admin01` / value of `SEED_ADMIN_PASSWORD` (default `ChangeMeAdmin1!`)

Choose `2026-09-12` to see the included sample files.

These defaults are only for an isolated local lab. Do not use them in AWS.

## Run tests and build

```bash
mvn clean test
mvn clean package
docker build -t histdata-app:local .
```

## Important environment variables

| Variable | Purpose |
| --- | --- |
| `SPRING_PROFILES_ACTIVE` | `local` uses files; `aws` uses S3 |
| `DB_URL` | Oracle JDBC URL |
| `DB_USERNAME` / `DB_PASSWORD` | Oracle credentials |
| `HISTDATA_S3_BUCKET` | Historical data bucket |
| `AWS_REGION` | AWS region |
| `HISTDATA_LOCAL_ROOT` | Local data directory |

In EKS, AWS credentials are not environment variables. The AWS SDK uses the
pod's ServiceAccount and IRSA web-identity credentials.

## S3 key convention

```text
CM/2026-09-12/cm_trade.dat
CD/2026-09-12/cd_trade.dat
FO/2026-09-12/fo_trade.dat
```

## Health and metrics

- Liveness: `/actuator/health/liveness`
- Readiness: `/actuator/health/readiness`
- Prometheus: `/actuator/prometheus`

## Current baseline scope

- Database-backed login and roles
- Client subscription/entitlement check
- CM/CD/FO file listing by date
- Streaming download without writing the complete object to pod disk
- Oracle success/failure audit trail
- Admin audit-history page
- Structured application logs and actuator metrics

For AWS, first create a dedicated Oracle schema user with the grants shown in
`docs/oracle-app-user.sql.example`; store those credentials in Secrets Manager.

