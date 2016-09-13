package ru.hh.demo;

import org.junit.Test;
import static org.jooq.impl.DSL.rowNumber;
import static org.jooq.impl.DSL.select;
import static ru.hh.demo.DBUtils.dsl;
import static ru.hh.jooq.tables.Account.ACCOUNT;
import static ru.hh.jooq.tables.BankMoneyPaymentType.BANK_MONEY_PAYMENT_TYPE;
import static ru.hh.jooq.tables.Bill.BILL;

public class CompileChecks {
  @Test
  public static void compile_checks_examples() {
    dsl().select()
//~~~~~~~ "join" is not possible here
//        .join(BILL).on(BILL.ACCOUNT_ID.equal(ACCOUNT.ACCOUNT_ID))
        .from(ACCOUNT)
        .fetch();

    dsl().select()
        .from(BILL)
//        .join(ACCOUNT)
//~~~~~~~ "on" is missing here
        .fetch();

//  dsl()
//      .select(rowNumber())
//~~~~~ "over()" is missing here
//      .from(BILL)
//      .fetch();

//    dsl().select()
//        .from(BILL)
//      .where(BILL.BILL_SUBJECT.in(
//         select(BANK_MONEY_PAYMENT_TYPE.PAYMENT_TYPE).from(BANK_MONEY_PAYMENT_TYPE)))
//~~~~~ type mismatch
//    .fetch();

    dsl().select()
        .from(BILL)
//      .where(BILL.EMONEY_TYPE.in(select(BANK_MONEY_PAYMENT_TYPE.PAYMENT_TYPE, BANK_MONEY_PAYMENT_TYPE.PAYMENT_TYPE).from(BANK_MONEY_PAYMENT_TYPE)))
//~~~~~ wrong arity
        .fetch();
  }
}
