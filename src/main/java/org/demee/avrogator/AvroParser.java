package org.demee.avrogator;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileStream;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class AvroParser {
    public Schema getSchema(File file) {
        // get schema from avro file
        try (FileInputStream fileInputStream = new FileInputStream(file)){
            DatumReader<GenericRecord> datumReader = new GenericDatumReader<>();
            try (DataFileStream<GenericRecord> dataFileReader = new DataFileStream<>(fileInputStream, datumReader)) {
                return dataFileReader.getSchema();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public ArrayList<GenericRecord> parse(File file) {
        // parse avro file
        DatumReader<GenericRecord> datumReader = new GenericDatumReader<>();
        FileInputStream fileInputStream = null;
        ArrayList<GenericRecord> records = new ArrayList<>();
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try (DataFileStream<GenericRecord> dataFileReader = new DataFileStream<>(fileInputStream, datumReader)) {
            while (dataFileReader.hasNext()) {
                GenericRecord record = dataFileReader.next();
                records.add(record);
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return records;
    }
}
