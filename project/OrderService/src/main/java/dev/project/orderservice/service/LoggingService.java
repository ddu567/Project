package dev.project.orderservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LoggingService {
    private static final Logger logger = LoggerFactory.getLogger(LoggingService.class);

    public void logError(String message, Exception e) {
        logger.error(message, e);
    }

    public void logInfo(String message) {
        logger.info(message);
    }
}
