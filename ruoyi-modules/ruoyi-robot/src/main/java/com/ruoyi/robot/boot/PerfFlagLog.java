package com.ruoyi.robot.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/** 启动后打印干跑开关，避免“以为生效了” */
@Component
public class PerfFlagLog implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(PerfFlagLog.class);

    @Value("${robot.dry-run:false}")
    private boolean dryRun;

    @Override
    public void run(ApplicationArguments args) {
        log.warn("==== [robot.dry-run] effective value: {} ====", dryRun);
    }
}
