package ru.hh.demo;

import org.jooq.Field;
import org.junit.Test;
import ru.hh.jooq.tables.Bill;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.jooq.impl.DSL.coalesce;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.partitionBy;
import static org.jooq.impl.DSL.sum;
import static ru.hh.demo.DBUtils.dsl;
import static ru.hh.jooq.tables.Bill.BILL;

public class RunningTotals {

  private class Balance {
    LocalDateTime transactionTime;
    Integer accountId;
    BigDecimal balance;
    Long transactionAmount;

    Balance(LocalDateTime transactionTime, Integer accountId, BigDecimal balance, Long transactionAmount) {
      this.transactionTime = transactionTime;
      this.accountId = accountId;
      this.balance = balance;
      this.transactionAmount = transactionAmount;
    }

    @Override
    public String toString() {
      return "Balance{" +
          "transactionTime=" + transactionTime +
          ", accountId=" + accountId +
          ", balance=" + balance +
          ", transactionAmount=" + transactionAmount +
          '}';
    }
  }

  @Test
  public void calculateRunningTotals() {
    Bill b = BILL.as("b");
    Field<BigDecimal> balance = field("balance", BigDecimal.class);

    List<Balance> transactions = dsl()
        .select(b.BILL_ID, b.ACCOUNT_ID, b.CREATION_TIME, b.PRICE,
            coalesce(sum(b.PRICE)
                .over(partitionBy(b.ACCOUNT_ID)
                    .orderBy(b.CREATION_TIME.desc(), b.BILL_ID.desc())
                    .rowsBetweenFollowing(1)
                    .andUnboundedFollowing()), 0).as(balance))
        .from(b)
        .where(b.ACCOUNT_ID.eq(105591))
        .orderBy(b.CREATION_TIME.desc(), b.BILL_ID.desc())
        .fetch()
        .map(record -> new Balance(
            record.get(b.CREATION_TIME),
            record.get(b.ACCOUNT_ID),
            record.get(balance),
            record.get(b.PRICE)));

    transactions.forEach(System.out::println);
  }
}
