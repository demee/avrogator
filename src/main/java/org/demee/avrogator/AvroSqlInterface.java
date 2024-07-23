package org.demee.avrogator;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.calcite.DataContext;
import org.apache.calcite.jdbc.CalciteConnection;
import org.apache.calcite.linq4j.AbstractEnumerable;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Enumerator;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.ScannableTable;
import org.apache.calcite.schema.SchemaFactory;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.schema.Table;
import org.apache.calcite.schema.impl.AbstractSchema;
import org.apache.calcite.schema.impl.AbstractTable;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class AvroSqlInterface {
    private final String avroFilePath;
    private Connection connection;

    public AvroSqlInterface(String avroFilePath) {
        this.avroFilePath = avroFilePath;
    }

    public void init() throws Exception {
        Properties info = new Properties();
        info.setProperty("lex", "JAVA");
        connection = DriverManager.getConnection("jdbc:calcite:", info);
        CalciteConnection calciteConnection = connection.unwrap(CalciteConnection.class);
        SchemaPlus rootSchema = calciteConnection.getRootSchema();

        Map<String, Object> operand = new HashMap<>();
        operand.put("avroFile", avroFilePath);

        SchemaFactory schemaFactory = new AvroSchemaFactory();
        SchemaPlus avroSchema = rootSchema.add("AVRO", schemaFactory.create(rootSchema, "AVRO", operand));
    }

    public ResultSet executeQuery(String sql) throws Exception {
        Statement statement = connection.createStatement();
        return statement.executeQuery(sql);
    }

    public void close() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    // Custom SchemaFactory for Avro files
    public static class AvroSchemaFactory implements SchemaFactory {
        @Override
        public org.apache.calcite.schema.Schema create(SchemaPlus parentSchema, String name, Map<String, Object> operand) {
            String avroFilePath = (String) operand.get("avroFile");
            return new AvroSchema(avroFilePath);
        }
    }

    // Custom Schema for Avro files
    public static class AvroSchema extends AbstractSchema {
        private final String avroFilePath;

        public AvroSchema(String avroFilePath) {
            this.avroFilePath = avroFilePath;
        }

        @Override
        protected Map<String, Table> getTableMap() {
            Map<String, Table> tables = new HashMap<>();
            tables.put("AVRO_TABLE", new AvroTable(avroFilePath));
            return tables;
        }
    }

    // Custom Table for Avro files
    public static class AvroTable extends AbstractTable implements ScannableTable {
        private final String avroFilePath;

        public AvroTable(String avroFilePath) {
            this.avroFilePath = avroFilePath;
        }

        @Override
        public RelDataType getRowType(RelDataTypeFactory typeFactory) {
            try (DataFileReader<GenericRecord> dataFileReader = new DataFileReader<>(new File(avroFilePath), new GenericDatumReader<>())) {
                Schema avroSchema = dataFileReader.getSchema();
                RelDataTypeFactory.Builder builder = typeFactory.builder();
                for (Schema.Field field : avroSchema.getFields()) {
                    RelDataType fieldType = convertAvroType(field.schema(), typeFactory);
                    builder.add(field.name(), fieldType);
                }
                return builder.build();
            } catch (IOException e) {
                throw new RuntimeException("Error reading Avro schema", e);
            }
        }

        private RelDataType convertAvroType(Schema avroType, RelDataTypeFactory typeFactory) {
            switch (avroType.getType()) {
                case STRING:
                    return typeFactory.createJavaType(String.class);
                case INT:
                    return typeFactory.createJavaType(Integer.class);
                case LONG:
                    return typeFactory.createJavaType(Long.class);
                case FLOAT:
                    return typeFactory.createJavaType(Float.class);
                case DOUBLE:
                    return typeFactory.createJavaType(Double.class);
                case BOOLEAN:
                    return typeFactory.createJavaType(Boolean.class);
                // Add more type conversions as needed
                default:
                    return typeFactory.createJavaType(Object.class);
            }
        }

        @Override
        public Enumerable<Object[]> scan(DataContext root) {
            return new AbstractEnumerable<Object[]>() {
                @Override
                public Enumerator<Object[]> enumerator() {
                    try {
                        DataFileReader<GenericRecord> reader = new DataFileReader<>(new File(avroFilePath), new GenericDatumReader<>());
                        return new AvroEnumerator(reader);
                    } catch (IOException e) {
                        throw new RuntimeException("Error reading Avro file", e);
                    }
                }
            };
        }
    }

    // Custom Enumerator for Avro records
    public static class AvroEnumerator implements Enumerator<Object[]> {
        private final DataFileReader<GenericRecord> reader;
        private GenericRecord current;

        public AvroEnumerator(DataFileReader<GenericRecord> reader) {
            this.reader = reader;
        }

        @Override
        public Object[] current() {
            if (current == null) {
                return null;
            }
            List<Object> values = new ArrayList<>();
            for (Schema.Field field : current.getSchema().getFields()) {
                values.add(current.get(field.name()));
            }
            return values.toArray();
        }

        @Override
        public boolean moveNext() {
            if (reader.hasNext()) {
                try {
                    current = reader.next(current);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return true;
            }
            return false;
        }

        @Override
        public void reset() {
            try {
                reader.sync(0);
            } catch (IOException e) {
                throw new RuntimeException("Error resetting Avro reader", e);
            }
        }

        @Override
        public void close() {
            try {
                reader.close();
            } catch (IOException e) {
                throw new RuntimeException("Error closing Avro reader", e);
            }
        }
    }
}
