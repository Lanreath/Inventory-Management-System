# Developer Guide

## Table of Contents

## Setting Up

### Prerequisites

- Java 8 (Use any suitable JDK version)
- Gradle (Use any suitable build tool e.g. Maven)
- JDBC drivers (Oracle and SQLite)

### Configuration

- Database connection details (found in `sync\Oracle.java` and `db\Database.java`)

## Design

### Architecture

The diagram below shows the architecture of the application.

![Architecture](images/architecture.png)

The application is split into 4 main components: databases, models, logic, and UI.

### Databases

The application connects to 2 databases: Oracle and SQLite. The Oracle database is where the company stores its data. The SQLite database is used to store the application's data.

#### Oracle

The class `Oracle` is used to connect to the Oracle database. It uses the JDBC driver to connect to the database. The class `Oracle` is a singleton class, which means that there is only one instance of the class. This is to ensure that there is only one connection to the database at any time.

Its class methods are used to validate the connection upon data synchronization.

The class `ReadUtil` contains methods to read data from the Oracle database. It uses the `Oracle` class to connect to the database and execute queries.

#### SQLite

The class `Database` is used to connect to the SQLite database. It uses the JDBC driver to connect to the database. The class `Database` is a singleton class, which means that there is only one instance of the class. This is to ensure that there is only one connection to the database at any time.

Its class methods are used to validate the connection upon startup, data synchronization and user operations.

The class `CRUDUtil` contains methods to create, read, update, and delete data from the SQLite database. It uses the `Database` class to connect to the database and execute queries.

### Models

#### Customer

#### Product

#### Part

#### Transfer

### Logic

#### Data Synchronization

#### Data Access Objects

#### Filters

### UI

#### MainWindow

#### Action Bar

#### Tables

#### Summary
