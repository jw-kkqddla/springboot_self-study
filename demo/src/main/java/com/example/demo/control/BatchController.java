package com.example.demo.control;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/batch")
public class BatchController {

    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private Job importGoodsJob;
    @Autowired
    private Job updateGoodsJob;
    @Autowired
    private Job deleteGoodsJob;
    @Autowired
    private Job queryGoodsJob;

    @PostMapping("/import")
    public String runImportJob(@RequestParam(required = false) String inputFile) throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("inputFile", inputFile != null ? inputFile : "data/data.csv")
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        jobLauncher.run(importGoodsJob, jobParameters);
        return "导入作业已启动";
    }

    @PutMapping("/update")
    public String runUpdateJob(@RequestParam(required = false) String inputFile) throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("inputFile", inputFile != null ? inputFile : "data/update.csv")
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        jobLauncher.run(updateGoodsJob, jobParameters);
        return "更新作业已启动";
    }

    @DeleteMapping("/delete")
    public String runDeleteJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        jobLauncher.run(deleteGoodsJob, jobParameters);
        return "删除作业已启动";
    }

    @GetMapping("/query")
    public String runQueryJob(@RequestParam(required = false) String status,
                              @RequestParam(required = false) String outputFile) throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("status", status != null ? status : "ACTIVE")
                .addString("outputFile", outputFile != null ? outputFile : "output/query-result.csv")
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        jobLauncher.run(queryGoodsJob, jobParameters);
        return "查询作业已启动";
    }
}