package ru.hh.demo.example;

import org.jooq.Field;
import org.junit.Test;
import ru.hh.jooq.tables.Bill;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.jooq.impl.DSL.coalesce;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.sum;
import static ru.hh.demo.DBUtils.dsl;
import static ru.hh.jooq.tables.Bill.BILL;

public class BillDao {
  //  String hql =
//      "SELECT " +
//          "sum ( b.priceValue )" +
//          "  FROM Bill b " +
//          "WHERE " +
//          "  b.account.id = :account_id " +
//          "AND b.status IN (:billStates) ";
  @Test
  public void getPaidBillsPrice() {
    int accountId = 1;
    List<Integer> paidStates = Arrays.asList(2, 4);
    Field<BigDecimal> sum_field = field("sum", BigDecimal.class);
    Bill b = BILL.as("b");
    BigDecimal result = dsl().select(coalesce(sum(b.PRICE), 0L).as(sum_field))
        .from(b)
        .where(b.ACCOUNT_ID.eq(accountId))
        .and(b.STATUS.in(paidStates))
        .fetchOne(sum_field);
    System.out.println(result);
  }
}
