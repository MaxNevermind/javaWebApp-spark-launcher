package org.maxkons.service;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.launcher.SparkLauncher;
import org.maxkons.model.JobResult;
import org.maxkons.model.JobStatus;
import org.maxkons.sparkwordscounterjob.SparkJob;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SparkService {

    private static final String NAME_NODE = "hdfs://makea1.cgs.sbrf.ru:8020";
    private static final Path HDFS_OUT_FILE_PATH = new Path("/user/mkonstantinov/tmp/wordsCount");

    private static java.nio.file.Path sparkJobJarPath = null;
    private static final String SPARK_JOB_MAIN_CLASS = SparkJob.class.getName();

    private static final String JAVA_HOME = "/usr/java/jdk1.8.0_60/jre";
    private static final String SPARK_HOME = "/opt/cloudera/parcels/CDH-5.5.0-1.cdh5.5.0.p0.8";

    private static final List<JobResult> jobResults = new CopyOnWriteArrayList<>();

    public void countWords(String hdfsInFilePath)  {
        JobResult jobResult = new JobResult();
        jobResults.add(jobResult);
        try {
            extractSparkJobJarIfNeeded();
            new SparkLauncher()
                    .setSparkHome(SPARK_HOME)
                    .setJavaHome(JAVA_HOME)
                    .setAppResource(sparkJobJarPath.toString())
                    .setMainClass(SPARK_JOB_MAIN_CLASS)
                    .addAppArgs(hdfsInFilePath, HDFS_OUT_FILE_PATH.toString())
                    .setMaster("yarn-cluster")
                    .launch()
                    .waitFor();
            FileSystem fs = FileSystem.get(new URI(NAME_NODE), new Configuration());
            jobResult.setJobStatus(JobStatus.FINISHED);
            jobResult.setResult(IOUtils.toString(fs.open(HDFS_OUT_FILE_PATH), "UTF-8"));
        } catch (Exception e) {
            jobResult.setJobStatus(JobStatus.FAILED);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private synchronized void extractSparkJobJarIfNeeded() throws IOException, URISyntaxException {
        if (sparkJobJarPath != null && sparkJobJarPath.toFile().exists()) {
            return;
        }
        sparkJobJarPath = Files.createTempFile("spark-words-counter-spark-job-", ".jar");
        URL url = new URL(StringUtils.removeEnd(
                        SparkJob.class.getProtectionDomain().getCodeSource().getLocation().toURI().toURL().toString()
                        , "!/"));
        FileUtils.copyURLToFile(url, sparkJobJarPath.toFile());
    }

    public List<JobResult> getJobResults() {
        return jobResults;
    }
}
