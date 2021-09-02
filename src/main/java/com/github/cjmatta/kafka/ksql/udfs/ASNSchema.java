package com.github.cjmatta.kafka.ksql.udfs;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;

public class ASNSchema {

    public Schema getASNSchema(){

        Schema ASNSchema = SchemaBuilder.struct()
                .field("ASN", Schema.OPTIONAL_INT32_SCHEMA)
                .field("ORG", Schema.OPTIONAL_STRING_SCHEMA)
                .optional()
                .build();

        return ASNSchema;
    }
}
