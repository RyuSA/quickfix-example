package com.github.ryusa.vulture.adventcalendar2020.quickfix.acceptor;

import java.util.UUID;

import lombok.extern.log4j.Log4j2;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.RejectLogon;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.UnsupportedMessageType;
import quickfix.field.ExecType;
import quickfix.field.OrdStatus;
import quickfix.fix44.component.Instrument;

@Log4j2
public class AcceptorApplication extends quickfix.MessageCracker implements quickfix.Application {

    @Override
    public void onCreate(SessionID sessionId) {
        log.info("---------- Server onCreate ----------");
    }

    @Override
    public void onLogon(SessionID sessionId) {
        log.info("---------- Server onLogon ----------");
    }

    @Override
    public void onLogout(SessionID sessionId) {
        log.info("---------- Server onLogout ----------");
    }

    @Override
    public void toAdmin(Message message, SessionID sessionId) {
        log.info("---------- Server toAdmin ----------");
        log.info("message {}", message.toString());
    }

    @Override
    public void fromAdmin(Message message, SessionID sessionId)
            throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
        log.info("---------- Server fromAdmin ----------");
        log.info("message {}", message.toString());
    }

    @Override
    public void toApp(Message message, SessionID sessionId) throws DoNotSend {
        log.info("---------- Server toApp ----------");
        log.info("message {}", message.toString());
    }

    @Override
    public void fromApp(Message message, SessionID sessionId)
            throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
        log.info("---------- Server fromApp ----------");
        log.info("message {}", message.toString());
        crack(message, sessionId);
    }

    /**
     * Clientからの注文を受信した場合の処理を記述します. この処理はMessageCracker#crackから呼び出されます
     * 
     * @param order     {@link quickfix.fix44.NewOrderSingle}
     * @param sessionID {@link quickfix.SessionID}
     */
    @quickfix.MessageCracker.Handler
    public void onNewOrderSingle(quickfix.fix44.NewOrderSingle order, SessionID sessionID) {
        log.info("Congratulations! I recieve a new order!!");
        log.info("Order Detail; {}", order.toRawString());
        replyNewOrderSingle(order, sessionID);
    }

    /**
     * 約定した情報をClientへ通知する
     * 
     * @param order
     * @param sessionID
     * @throws FieldNotFound
     */
    private void replyNewOrderSingle(quickfix.fix44.NewOrderSingle order, SessionID sessionID) {
        quickfix.field.OrderID orderID;
        try {
            orderID = new quickfix.field.OrderID(order.getClOrdID().getValue());
        } catch (FieldNotFound e) {
            log.error(
                    "ALERT: PLEASE CONTACT TO THE FOLLOWING SESSION OWNER ASAP; failed to parse ClOrdID@NewOrderSingle; {}",
                    sessionID.getSenderCompID(), e);
            throw new InternalError(e);
        }

        quickfix.field.CumQty cumQty;
        quickfix.field.Side side;
        quickfix.field.LeavesQty leavesQty;
        Instrument instrument;
        try {
            cumQty = new quickfix.field.CumQty(order.getOrderQty().getValue());
            side = order.getSide();
            leavesQty = new quickfix.field.LeavesQty(order.getOrderQty().getValue() - cumQty.getValue());
            instrument = new Instrument(order.getSymbol());
        } catch (FieldNotFound e) {
            log.error("Something happen; failed to parse NewOrderSingle; {}", orderID.getValue(), e);
            throw new InternalError(e);
        }

        quickfix.field.ExecID execID = new quickfix.field.ExecID(UUID.randomUUID().toString());
        quickfix.field.ExecType execType = new quickfix.field.ExecType(ExecType.NEW);
        quickfix.field.OrdStatus ordStatus = new quickfix.field.OrdStatus(OrdStatus.FILLED);
        quickfix.field.AvgPx avgPx = new quickfix.field.AvgPx(0);

        quickfix.fix44.ExecutionReport report = new quickfix.fix44.ExecutionReport(orderID, execID, execType, ordStatus,
                side, leavesQty, cumQty, avgPx);
        report.set(instrument);
        try {
            Session.sendToTarget(report, sessionID);
        } catch (SessionNotFound e) {
            log.error("Something happen; failed to send Execution Report; #{}", orderID.getValue(), e);
            throw new InternalError(e);
        }
    }

}
