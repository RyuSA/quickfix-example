package com.github.ryusa.vulture.adventcalendar2020.quickfix.initiator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import quickfix.ConfigError;
import quickfix.DefaultMessageFactory;
import quickfix.FileLogFactory;
import quickfix.FileStoreFactory;
import quickfix.LogFactory;
import quickfix.MessageFactory;
import quickfix.MessageStoreFactory;
import quickfix.SessionSettings;
import quickfix.ThreadedSocketInitiator;

@Configuration
public class InitiatorConfig {

    @Bean
    public ThreadedSocketInitiator initiator(InitiatorApplication initiatorApplication) throws ConfigError {
        SessionSettings settings = new SessionSettings("quickfix/initiator.cfg");
        MessageStoreFactory storeFactory = new FileStoreFactory(settings);
        LogFactory logFactory = new FileLogFactory(settings);
        MessageFactory messageFactory = new DefaultMessageFactory();
        ThreadedSocketInitiator socketInitiator = new ThreadedSocketInitiator(initiatorApplication, storeFactory,
                settings, logFactory, messageFactory);
        return socketInitiator;
    }
}
