package com.github.cjmatta.kafka.ksql.udfs;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.DatabaseReader.Builder;
import io.confluent.ksql.function.udf.Udf;
import io.confluent.ksql.function.udf.UdfDescription;
import io.confluent.ksql.function.udf.UdfParameter;
import org.apache.kafka.common.Configurable;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.connect.data.Struct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;

@UdfDescription(
        name = "getasnforip",
        description = "Function to lookup ip -> geo information "
)
public class ASNipLookup implements Configurable {
    private DatabaseReader reader;
    private Logger log = LoggerFactory.getLogger(ASNipLookup.class);

    public void configure(Map<String, ?> props) {
        String KSQL_FUNCTIONS_GETASNFORIP_GEOCITY_DB_PATH_CONFIG = "ksql.functions.getasnforip.geocity.db.path";
        if (!props.containsKey(KSQL_FUNCTIONS_GETASNFORIP_GEOCITY_DB_PATH_CONFIG)) {
            throw new ConfigException("Required property " + KSQL_FUNCTIONS_GETASNFORIP_GEOCITY_DB_PATH_CONFIG + " not found!");
        } else {
            String geoCityDbPath = (String) props.get(KSQL_FUNCTIONS_GETASNFORIP_GEOCITY_DB_PATH_CONFIG);

            try {
                File database = new File(geoCityDbPath);
                this.reader = (new Builder(database)).build();
                this.log.info("Loaded ASN database from " + geoCityDbPath);
            } catch (IOException e) {
                this.log.error("Problem loading ASN database: " + e);
                throw new ExceptionInInitializerError(e);
            }
        }
    }

    @Udf(
            description = "returns ASN for an IP address",
            schema="STRUCT<ASN INT, ORG VARCHAR>")

    public Struct getasnforip(@UdfParameter(
            value = "ip",
            description = "the IP address to lookup in the asn database") String ip) {

        Integer SYSTEM_NUMBER;
        try {
            SYSTEM_NUMBER = this.reader.asn(InetAddress.getByName(ip)).getAutonomousSystemNumber();
        } catch (Exception e) {
            SYSTEM_NUMBER = 0;
        }

        String org;
        try {
            org = this.reader.asn(InetAddress.getByName(ip)).getAutonomousSystemOrganization();
        } catch (Exception e) {
            org = null;
        }

        ASNSchema asnSchema = new ASNSchema();
        Struct asn_return = new Struct(asnSchema.getASNSchema())
                .put("ASN", SYSTEM_NUMBER)
                .put("ORG", org);

        return asn_return;
    }

}
