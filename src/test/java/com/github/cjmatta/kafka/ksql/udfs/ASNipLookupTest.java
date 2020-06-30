package com.github.cjmatta.kafka.ksql.udfs;

import org.apache.kafka.connect.data.Struct;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasToString;

public class ASNipLookupTest {

  private ASNipLookup udf;

  @Before
  public void setUp() {
    udf = new ASNipLookup();
    File mmdb = new File("src/test/resources/GeoLite2-ASN.mmdb");
    configure(mmdb.getAbsolutePath());
  }

  @Test
  public void ASNipLookupHappyPathTest() {
    ASNSchema ASNSchema = new ASNSchema();

    Struct asn = new Struct(ASNSchema.getASNSchema())
            .put("ASN", 217)
            .put("ORG", "UMN-SYSTEM");

    assertThat(udf.getasnforip("128.101.101.101"), hasToString(asn.toString()));

  }

/*
  @Test
  public void ASNipLookupNullIPTest() {
    assertThat(udf.getasnforip(null), hasToString());
  }

  @Test
  public void ASNipLookupNotIPAddressTest() {
    assertThat(udf.getasnforip("not an IP address"), hasToString("Struct{}"));
  }

  @Test
  public void ASNipLookupPrivateIPAddressTest() {
    assertThat(udf.getasnforip("10.0.1.14"), hasToString("Struct{}"));
  }
*/

  private void configure(String mmdbPath) {
    Map<String, String> config = new HashMap<String, String>();
    config.put("ksql.functions.getasnforip.geocity.db.path", mmdbPath);
    udf.configure(Collections.unmodifiableMap(new LinkedHashMap<String, String>(config)));
  }

}

