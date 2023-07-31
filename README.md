# Inventory Management System

This is a simple inventory management system I have designed for my summer 2023 internship project at Thales. The graphical interface is intended for a small logistics team working in a production department on a local machine.

## Quick Start

1. Ensure you have Java 8 or below installed. Check by running `java -version` in the command line.

2. Download the latest release of the application.

3. Create a `database.properties` file in the same directory as the application. The file should contain the following lines:

```
oracle.username= ********
oracle.password= ********
oracle.url= @ip:port:SID
sqlite.location= database.db
export.location= export.csv
enable_offline= true/false
```

4. Open the application by double-clicking on it or running `java -jar ILS.jar` in the command line.

## Resources

- [User Guide](/User%20Guide.md)
- [Developer Guide](/Developer%20Guide.md)
