package com.github.ryusa.vulture.adventcalendar2020.quickfix.initiator;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import quickfix.ConfigError;
import quickfix.RuntimeError;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.ThreadedSocketInitiator;
import quickfix.field.ClOrdID;
import quickfix.field.OrdType;
import quickfix.field.OrderQty;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TransactTime;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DebugController {

    private final ThreadedSocketInitiator initiator;

    /**
     * カウンターパーティに接続するAPIエンドポイントです
     * 
     * @return
     */
    @PutMapping("/connect")
    public ResponseEntity<?> startConnection() {

        if (initiator.isLoggedOn()) {
            return ResponseEntity.ok().build();
        }

        try {
            log.info("connecting...");
            initiator.start();
            log.info("connected");
            return ResponseEntity.ok().build();
        } catch (RuntimeError e) {
            log.error("RuntimeError", e);
            return ResponseEntity.badRequest().build();
        } catch (ConfigError e) {
            log.error("ConfigError", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Data
    public static class NewOrderSingleRequest {
        @NotBlank
        @Pattern(regexp = "^[A-Z]+$")
        private String symbol;
        @Positive
        private double qty;
        @NotBlank
        @Pattern(regexp = "^[1|2|3|4|5|6|7|8|9|A|B|C|D|E|F|G]$")
        private char side;
    }

    /**
     * カウンターパーティへ指定された注文を発注します. 
     * 
     * @param request {@link NewOrderSingleRequest}
     * @return
     */
    @PostMapping("/order")
    public ResponseEntity<?> newOrderSingle(@RequestBody NewOrderSingleRequest request) {

        if (!initiator.isLoggedOn()) {
            return ResponseEntity.badRequest()
                    .body("You need to request `PUT /api/connect` before requesting newOrderSingle");
        }

    quickfix.fix44.NewOrderSingle newOrderSingle = new quickfix.fix44.NewOrderSingle(
      new ClOrdID(UUID.randomUUID().toString()),
      new Side(request.side),
      new TransactTime(LocalDateTime.now()),
      new OrdType(OrdType.MARKET)
    );
    newOrderSingle.set(new OrderQty(request.qty));
    newOrderSingle.set(new Symbol(request.getSymbol()));

    SessionID session = this.initiator.getSessions().get(0);
    try {
      Session.sendToTarget(newOrderSingle, session);
      return ResponseEntity.ok().build();
    } catch (SessionNotFound e) {
      log.error("failed to send a newOrderSingle", e);
      return ResponseEntity.badRequest()
              .body("You need to request `PUT /api/connect` before requesting newOrderSingle");
    }

    }
}
