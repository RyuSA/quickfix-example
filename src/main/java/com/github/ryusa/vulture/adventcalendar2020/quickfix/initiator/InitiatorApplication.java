package com.github.ryusa.vulture.adventcalendar2020.quickfix.initiator;

import lombok.extern.log4j.Log4j2;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.RejectLogon;
import quickfix.SessionID;
import quickfix.UnsupportedMessageType;

@Log4j2
public class InitiatorApplication extends quickfix.MessageCracker implements quickfix.Application {

  @Override
  public void onCreate(SessionID sessionId) {
    log.info("---------- Client onCreate ----------");
  }

  @Override
  public void onLogon(SessionID sessionId) {
    log.info("---------- Client onLogon ----------");
  }

  @Override
  public void onLogout(SessionID sessionId) {
    log.info("---------- Client onLogout ----------");
  }

  @Override
  public void toAdmin(Message message, SessionID sessionId) {
    log.info("---------- Client toAdmin ----------");
    log.info("message {}", message.toString());
  }

  @Override
  public void fromAdmin(Message message, SessionID sessionId)
      throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
    log.info("---------- Client fromAdmin ----------");
    log.info("message {}", message.toString());
  }

  @Override
  public void toApp(Message message, SessionID sessionId) throws DoNotSend {
    log.info("---------- Client toApp ----------");
    log.info("message {}", message.toString());
  }

  @Override
  public void fromApp(Message message, SessionID sessionId)
      throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
    log.info("---------- Client fromApp ----------");
    log.info("message {}", message.toString());
    crack(message, sessionId);
  }

  /**
   * カウンターパーティから約定情報を受信した場合の処理を記述します. この処理はMessageCracker#crackから呼び出されます
   * 
   * @param message
   * @param sessionID
   */
  public void onMessage(quickfix.fix44.ExecutionReport message, SessionID sessionID) {
    log.info("Awesome! Your order has been executed!");
    log.info("Execution report; {}", message.toRawString());
    // you may save the report into your database.
  }
}
