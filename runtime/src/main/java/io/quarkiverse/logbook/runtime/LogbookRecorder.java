package io.quarkiverse.logbook.runtime;

import io.quarkiverse.logbook.runtime.configuration.LogbookConfiguration;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class LogbookRecorder {

    public void setFormRequestMode(LogbookConfiguration configuration) {
        System.setProperty("logbook.servlet.form-request-mode",
                configuration.filter().formRequestMode().name().toLowerCase());
    }
}