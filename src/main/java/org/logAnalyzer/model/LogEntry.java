package org.logAnalyzer.model;

import java.time.ZonedDateTime;

public record LogEntry(
    String remoteAddr,
    String remoteUser,
    ZonedDateTime timeLocal,
    String method,
    String resource,
    String protocol,
    int status,
    long bodyBytesSent,
    String httpReferer,
    String httpUserAgent
) {}
