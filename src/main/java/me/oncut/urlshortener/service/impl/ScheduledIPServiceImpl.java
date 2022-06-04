package me.oncut.urlshortener.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import me.oncut.urlshortener.service.IPAddressService;
import me.oncut.urlshortener.service.ScheduledIPService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@CommonsLog
@RequiredArgsConstructor
public class ScheduledIPServiceImpl implements ScheduledIPService {

    private final IPAddressService ipAddressService;

    @Override
    @Scheduled(cron = "0 * * * * *")
    public void deactivateDeprecatedIps() {
        ipAddressService.deactivateDeprecatedIps();
    }

    @Override
    @Scheduled(cron = "0 */24 * * * *")
    public void deleteDeactivatedIps() {
        ipAddressService.deleteDeactivatedIps();
    }
}