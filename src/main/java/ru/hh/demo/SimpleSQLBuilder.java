package ru.hh.demo;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.jooq.types.DayToSecond;
import org.junit.Test;
import java.sql.Timestamp;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.*;
import static ru.hh.demo.DBUtils.console_log;
import static ru.hh.demo.DBUtils.dsl;

public class SimpleSQLBuilder {

  private static DayToSecond day(int n) {
    return new DayToSecond(n);
  }

  @Test
  public void простой_селект() {
    // select * from bill limit 10;
    SelectQuery query = dsl()
        .select()
        .from(table("bill"))
        .limit(10)
        .getQuery();

    console_log(query);
  }

  @Test
  public void typed_fields_and_compile_checks() {
//     see CompileChecks for more examples!
    Table<Record> bill = table("bill");
    Field<Integer> bill_id = field("bill_id", Integer.class);
    Field<String> uid = field("uid", String.class);
    Field<String> bill_uid = field("bill_uid", String.class);
    Field<Timestamp> payment_time = field("payment_time", Timestamp.class);

    SelectQuery<Record> query = dsl()
        .select(bill_id)
        .select(DSL.concat(val("Счет №"), uid).as(bill_uid))
        .from(bill)
        // мы можем юзать предикат between, потому что объявили поле как Timestamp, иначе была бы ошибка компиляции
//        .where(payment_time.between(
//            currentTimestamp().minus(day(20)), currentTimestamp().minus(day(10))))
//        .and(uid.like("%123%"))
        .getQuery();

    // select bill_id, ('Счет №' || uid) as "bill_uid"
    // from bill
    // where (payment_time
    // between
    // (current_timestamp - cast('+20 00:00:00.000000000' as interval day to second))
    // and
    // (current_timestamp - cast('+10 00:00:00.000000000' as interval day to second))
    // and uid like '%123%');

    System.out.println(
        query.fetch().stream()
            .map(record -> record.get(bill_uid))
            .collect(Collectors.joining(", "))
    );

//    console_log(query);
    // see CompileChecks for more examples!
  }

  @Test
  public void простой_join() {
    Table<Record> es = table("employer_service").as("es");
    Table<Record> s = table("service").as("s");
    Field<Integer> employer_service_id = field("es.employer_service_id", Integer.class);
    Field<String> service_type = field("s.service_type", String.class);

    SelectQuery query = dsl()
        .select(employer_service_id, service_type)
        .from(es)
        .join(s).on(field("es.service_id").eq(field("s.service_id")))
        .limit(10)
        .getQuery();

    // select es.employer_service_id, s.service_type
    // from employer_service as "es"
    // join service as "s" on es.service_id = s.service_id
    // limit 10

    console_log(query);
  }

  @Test
  public void group_by_having() {
    SelectQuery query = dsl()
        .select(field("created_by_manager_id"), count().as("bill_count"))
        .from(table("bill"))
        .groupBy(field("created_by_manager_id"))
        .having(count().gt(2))
        .getQuery();

    // select created_by_manager_id, count(*) as "bill_count"
    // from bill
    // group by created_by_manager_id
    // having count(*) > 2

    console_log(query);
  }

  @Test
  public void with() {
    SelectQuery query = dsl()
        .with("a").as(select(
            val(100).as("x"),
            val("Ivan").as("y")))
        .with("b").as(select(
            val(-90).as("x"),
            val("Ivanov").as("y")))
        .select(
            field("a.x").add(field("b.x")).as("add"),
            field("a.y").concat(" ").concat(field("b.y")).as("concat"))
        .from(table(name("a")), table(name("b")))
        .getQuery();

    // with "a" as (select 100 as "x", 'Ivan' as "y"),
    // "b" as (select -90 as "x", 'Ivanov' as "y")
    // select (a.x + b.x) as "add",
    // ((cast(a.y as varchar) || ' ') || cast(b.y as varchar)) as "concat"
    // from "a", "b"

    console_log(query);
  }
}

// это самый простой способ использовать jOOQ,
// просто как sql builder без конкатинации строк,
// но каждый раз определять поля и таблицы это утомительно
// поэтому есть способ лучше
