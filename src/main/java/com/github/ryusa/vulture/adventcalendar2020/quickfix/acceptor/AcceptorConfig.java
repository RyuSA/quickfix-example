package com.github.ryusa.vulture.adventcalendar2020.quickfix.acceptor;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import quickfix.ConfigError;
import quickfix.DefaultMessageFactory;
import quickfix.FileLogFactory;
import quickfix.FileStoreFactory;
import quickfix.MessageFactory;
import quickfix.RuntimeError;
import quickfix.SessionSettings;
import quickfix.ThreadedSocketAcceptor;

@Configuration
public class AcceptorConfig {

    @Bean
    public AcceptorApplication acceptorApplication() {
        return new AcceptorApplication();
    }

    @Bean
    public ThreadedSocketAcceptor acceptor(AcceptorApplication acceptorApplication) throws ConfigError {
        SessionSettings setting = new SessionSettings("quickfix/acceptor.cfg");
        FileStoreFactory fileStoreFactory = new FileStoreFactory(setting);
        MessageFactory messageFactory = new DefaultMessageFactory();
        FileLogFactory fileLogFactory = new FileLogFactory(setting);
        ThreadedSocketAcceptor acceptor = new ThreadedSocketAcceptor(acceptorApplication, fileStoreFactory, setting,
                fileLogFactory, messageFactory);
        return acceptor;
    }

    public static class AcceptorLifecycle {
        private final ThreadedSocketAcceptor acceptor;

        public AcceptorLifecycle(ThreadedSocketAcceptor acceptor) {
            this.acceptor = acceptor;
        }

        @PostConstruct
        public void init() throws RuntimeError, ConfigError {
            this.acceptor.start();
        }

        @PreDestroy
        public void destroy() {
            // this part is a kind of serious part...be careful
            this.acceptor.stop();
        }
    }

    @Bean
    public AcceptorLifecycle acceptorLifecycle(ThreadedSocketAcceptor acceptor) {
        return new AcceptorLifecycle(acceptor);
    }
}
