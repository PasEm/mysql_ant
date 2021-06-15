# MySQL_stress_testing

MySQL Ant is an executable Java application that performs load testing on MySQL databases.
This benchmark works iteratively and creates a test database user with a needed count of connections before each testing.
It works in two modes: repeat the SQL script definite count or work during the definite time.  
The application analyzed the performance of the database and uses MySQL Performance Schema. 
So, the system collects data for these metrics:

1. **TPS.**
2. **Latency.**
3. **Transactions exceeded max execution time.**
4. **OPS by Performance Schema.**
5. **I/Os by Performance Schema.**
6. **Current memory by Performance Schema.**
7. **Total memory by Performance Schema.**
8. **Count of table scans by Performance Schema.**
9. **Latency by Performance Schema.**

## Configuration

The system uses a YAML file for work and contains two logical blocks:
1. **Benchmark**
```yaml
benchmark:
  client:
    count: Integer
    max: Integer
    step: Integer
  transaction:
    count: Integer
    max: Integer
    step: Integer
  time:
    count: Long
    max: Long
    step: Long
  execution:
    maxTime: Long
    timeout: Integer
    parallel: Boolean
  script:
    name: String
  report:
    path: String
    name: String
```
The application uses milliseconds as a time unit in the **time** block.
**time.max** field is a value in milliseconds that responds to count all transactions which exceed it.
MySQL Ant reads the SQL file by **script.name** field and saves results to the HTML file - **report.path/report.name**.
2. **MySQL datasource configuration**
```yaml
mysql:
  username: 
  password: 
  driverName: 
  url: 
```
## Using

To start the application you need to execute these commands:
1. **Create a JAR archive.**
```shell
mvn clean package
```
2. **Create configuration file - application.yaml in the same directory with the archive.**
3. **Create an SQL file that you specify in the configuration.**
4. **Run the application by this command:**
```shell
java -jar mysql_ant.jar
```

## Results

MySQL Ant generates results as an HTML file specified in the configuration. 
It consists of 3D graphics for each analyzed metric.