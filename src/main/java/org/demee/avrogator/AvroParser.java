package org.demee.avrogator;

import org.apache.avro.file.DataFileStream;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@Service
public class AvroParser {
    public AvroParser() {
    }

    public void parse(File file) {
        // parse avro file
        DatumReader<GenericRecord> datumReader = new GenericDatumReader<>();
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try (DataFileStream<GenericRecord> dataFileReader = new DataFileStream<>(fileInputStream, datumReader)) {
            while (dataFileReader.hasNext()) {
                GenericRecord record = dataFileReader.next();
                System.out.println(record);
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}
