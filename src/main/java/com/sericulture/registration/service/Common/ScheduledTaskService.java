package com.sericulture.registration.service.Common;

import com.sericulture.registration.service.ReelerService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTaskService {

    @Autowired
    private ReelerService reelerService;

    @Scheduled(cron = "0 4 * * 0") // Every sunday Indian Timings 10AM
    public void execute() {
        reelerService.getIsReelerLicenseExpiryDateIsNear();
    }
}