package com.codelovers.quanonghau.app;

import com.codelovers.quanonghau.utils.BrowserUtils;
import org.springframework.boot.SpringApplication;

public class MainThread extends Thread{
    private String[] args;

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    @Override
    public void run() {
        super.run();
        SpringApplication application = new SpringApplication(Application.class);
        application.run(args);
        BrowserUtils.browse("http://localhost:8080"+"/signin");
    }
}
