KSQL example UDF for looking up an ASN for an IP address.
### Pre-requisite
You'll need to supply your own GeoLite2 ASN database, specify it's location using the `function.getgeoforip.geocity.db.path` property in your ksql-server.properties before starting KSQL.

For an RPM-based install of KSQL, I set the following properties in `/etc/ksql/ksql-server.properties`:

    ksql.extension.dir=/opt/ksql-extension
    ksql.functions.getgeoforip.geocity.db.path=/opt/mmdb/GeoIP2-City.mmdb

### Build
```
mvn clean package
```

and move the jar `target/ksql-udf-asnip-1.0-SNAPSHOT-jar-with-dependencies.jar` into the `ksql.extension.dir` directory in your KSQL installation. 

### Usage
```sql
ksql> select ip, getasnforip(ip) as geo from WEBLOG;

