---
layout: page
title: Developer Guide
---

---

## Table of Contents

- [Table of Contents](#table-of-contents)
- [Setting Up](#setting-up)
  - [Prerequisites](#prerequisites)
  - [Configuration](#configuration)
- [Design](#design)
  - [Architecture](#architecture)
  - [Databases](#databases)
    - [Oracle](#oracle)
    - [SQLite](#sqlite)
  - [Models](#models)
    - [Customer](#customer)
    - [Product](#product)
    - [Part](#part)
    - [Transfer](#transfer)
  - [Logic](#logic)
    - [Data Synchronization](#data-synchronization)
    - [Data Access Objects](#data-access-objects)
    - [Filters](#filters)
  - [UI](#ui)
    - [MainWindow](#mainwindow)
    - [Action Bar](#action-bar)
    - [Tables](#tables)
    - [Summary](#summary)

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

The models are used to represent the data in the application. The models are used to store data from the databases and to pass data between the logic and UI components.

#### Customer

The `Customer` class represents a customer. It contains the following attributes: `customerId`, `customerName`.

Each `Customer` object is mapped to a row in the `customer` table in the SQLite database and a row in the `customer` table in the Oracle database.

#### Product

The `Product` class represents a product. It contains the following attributes: `productId`, `dbName`, `productName`, `customer`, `defaultPart`, `productNotes`.

Each `Product` object is mapped to a row in the `product` table in the SQLite database and a row in the `product` table in the Oracle database.

The database name (`dbName`) is used to identify the product alias from the SQL query results and retrieved from the Oracle database. The product name (`productName`) is used to identify the product alias from the user input. Both the database name and product name are used to identify the product from the SQLite database.

The `customer` attribute is a `Customer` object that represents the customer that the product belongs to. The `defaultPart` attribute is a `Part` object that represents the default part for the product. The `productNotes` attribute is a `String` that represents the notes for the product set by the user.

When a `Product` object is created, the `customer` is necessary. The `defaultPart` is initialized to a new `Part` object `Default`. Both `productName` and `productNotes` are initialized to empty strings.

#### Part

The `Part` class represents a part. It contains the following attributes: `partId`, `partName`, `partName`, `partQuantity`, `product`, `nextPart`, `partNotes`.

Each `Part` object is mapped to a row in the `part` table in the SQLite database and a row in the `part` table in the Oracle database.

The `partQuantity` attribute is an `int` that represents the quantity of the part. The `product` attribute is a `Product` object that represents the product that the part belongs to.

The `nextPart` attribute is a `Part` object that represents the next part to be used for the product starting from the default part.

The `partNotes` attribute is a `String` that represents the notes for the part set by the user.

When a `Part` object is created, the `product` is necessary. The `partQuantity` is initialized to 0. The `nextPart` is initialized to `null`. The `partNotes` is initialized to an empty string.

#### Transfer

The `Transfer` class represents a transfer. It contains the following attributes: `transferId`, `transferDate`, `transferQuantity`, `prevPartQuantity`, `part`, `transferType`.

Each `Transfer` object is mapped to a row in the `transfer` table in the SQLite database and a row in the `transfer` table in the Oracle database.

The `part` attribute is a `Part` object that represents the part that the transfer belongs to. The `transferType` attribute is a `String` that represents the type of the transfer: `RECEIVED`, `WITHDRAW`, `REJECT`, `SAMPLE`.

The `transferDate` attribute is a `Date` object that represents the date of the transfer. The `transferQuantity` attribute is an `int` that represents the quantity of the transfer. The `prevPartQuantity` attribute is an `int` that represents the quantity of the part before the transfer to facilitate quantity operations.

When a `Transfer` object is created, the `part` is necessary. The `transferDate` is initialized to the current date. The `transferQuantity` is given by user input. The `prevPartQuantity` is initialized to the current quantity of the part.

### Logic

#### Data Synchronization

#### Data Access Objects

#### Filters

### UI

#### MainWindow

#### Action Bar

#### Tables

#### Summary
