package org.maxkons.sparkwordscounterjob;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.IOException;
import java.util.Arrays;

public class SparkJob {

    private static final int EXPECTED_ARG_COUNT = 2;
//    private static final String HDFS_IN_FILE_PATH = "/user/mkonstantinov/tmp/src/AGG_EVENTS_OPCAT_DAY_VC/2016-03-10_15-08-51_455/part-m-00000";
//    private static final String HDFS_OUT_FILE_PATH = "/user/mkonstantinov/tmp/wordsCount";

    public static void main(String[] args) throws IOException {
        System.out.println("Spark job start, args: " + Arrays.toString(args));
        if (args.length < EXPECTED_ARG_COUNT) {
            throw new IllegalArgumentException("Usage: SparkJob <HDFS_IN_FILE_PATH> <HDFS_OUT_FILE_PATH>");
        }
        String hdfsInFilePath = args[0];
        String hdfsOutFilePath = args[1];

        JavaSparkContext sparkContext = new JavaSparkContext(new SparkConf());
        JavaRDD<String> newRecordsFile = sparkContext.textFile(hdfsInFilePath);
        long rowsCount = newRecordsFile.count();
        System.out.println("Result - " + rowsCount);

        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        try (FSDataOutputStream fsDataOutputStream = fs.create(new Path(hdfsOutFilePath))) {
            fsDataOutputStream.write((""+rowsCount).getBytes());
        }
        System.out.println("Spark job end");
    }

}
