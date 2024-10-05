
### Dynamic DB based properties for Spring

Allows to store Spring Application properties in the table of the default Spring JPA database configured in the Spring application.

Properties are cached for a duration specified in an application property 
<code>spring.auto-refresh-scope.refresh-interval</code> and reloaded automatically from the database after the specified duration.

### How to use?

Examples in the code:
- Spring application: <code>com.github.nemunaskaralius.spring.dbprop.sample.SampleApplication</code>
- Sample properties bean: <code>com.github.nemunaskaralius.spring.dbprop.sample.SampleProperties</code>
- REST controller displaying current property values: <code>com.github.nemunaskaralius.spring.dbprop.sample.SampleController</code>

Sample Database:
- <code>docker-compose.yaml</code> - run with docker for a sample MySQL database
- properties table is initialised automatically during Sample application startup with a help of Flyway migration (<code>V1.0__init_db_prop_table.sql</code>)

Run the database, start application and inspect current property values by accessing application REST endpoint:
http://localhost:8080/properties
