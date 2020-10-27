package com.github.ryusa.vulture.adventcalendar2020.quickfix.acceptor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import quickfix.ConfigError;
import quickfix.DefaultMessageFactory;
import quickfix.FileLogFactory;
import quickfix.FileStoreFactory;
import quickfix.MessageFactory;
import quickfix.SessionSettings;
import quickfix.ThreadedSocketAcceptor;

@Configuration
public class AcceptorConfig {

    @Bean
    public ThreadedSocketAcceptor acceptor(AcceptorApplication acceptorApplication) throws ConfigError {
        SessionSettings setting = new SessionSettings("quickfix/acceptor.cfg");
        FileStoreFactory fileStoreFactory = new FileStoreFactory(setting);
        MessageFactory messageFactory = new DefaultMessageFactory();
        FileLogFactory fileLogFactory = new FileLogFactory(setting);
        ThreadedSocketAcceptor acceptor = new ThreadedSocketAcceptor(acceptorApplication, fileStoreFactory, setting, fileLogFactory,
                messageFactory);
        acceptor.start();
        return acceptor;
    }
}
