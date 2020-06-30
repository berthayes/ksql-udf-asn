KSQL example UDF for looking up an ASN for an IP address.
### Pre-requisite
You'll need to supply your own GeoLite2 ASN database, specify it's location using the `function.getasnforip.geocity.db.path` property in your ksql-server.properties before starting KSQL.

For an RPM-based install of KSQL, I set the following properties in `/etc/ksql/ksql-server.properties`:

    ksql.extension.dir=/opt/ksql-extension
    ksql.functions.getasnforip.geocity.db.path=/opt/mmdb/GeoIP2-ASN.mmdb

### Build
```
mvn clean package
```

and move the jar `target/ksql-udf-asnip-1.0-SNAPSHOT-jar-with-dependencies.jar` into the `ksql.extension.dir` directory in your KSQL installation. 

### Usage

```sql
 SELECT "id.orig_h" AS LOCAL_IP, \
 "id.resp_h" as REMOTE_IP, \
 getasnforip("id.resp_h") as ASN \
 FROM CONN_STREAM \
 WHERE "id.resp_h" NOT LIKE '192.168.1.%' \
 EMIT CHANGES;
+---------------------------------------+---------------------------------------+---------------------------------------+
|LOCAL_IP                               |REMOTE_IP                              |ASN                                    |
+---------------------------------------+---------------------------------------+---------------------------------------+
|192.168.1.215                          |62.210.93.139                          |{ASN=12876, ORG=Online S.a.s.}         |
|192.168.1.215                          |62.210.93.139                          |{ASN=12876, ORG=Online S.a.s.}         |
|192.168.1.215                          |62.210.93.139                          |{ASN=12876, ORG=Online S.a.s.}         |
|192.168.1.215                          |208.90.189.156                         |{ASN=36489, ORG=NETSOLUS-NETWORKS}     |
|192.168.1.215                          |208.90.189.156                         |{ASN=36489, ORG=NETSOLUS-NETWORKS}     |
|192.168.1.215                          |1.1.1.1                                |{ASN=13335, ORG=CLOUDFLARENET}         |
|192.168.1.215                          |104.198.97.195                         |{ASN=15169, ORG=GOOGLE}                |
|192.168.1.215                          |104.198.97.195                         |{ASN=15169, ORG=GOOGLE}                |
 
 
 
