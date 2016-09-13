package ru.hh.demo;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record2;
import org.jooq.SelectQuery;
import org.junit.Test;
import ru.hh.jooq.tables.BillingTemplate;
import ru.hh.jooq.tables.EmployerService;
import ru.hh.jooq.tables.Service;
import ru.hh.jooq.tables.Site;
import ru.hh.jooq.tables.pojos.Bill;

import java.util.List;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.*;
import static ru.hh.demo.DBUtils.console_log;
import static ru.hh.demo.DBUtils.dsl;
import static ru.hh.jooq.tables.Bill.BILL;
import static ru.hh.jooq.tables.BillingTemplate.BILLING_TEMPLATE;
import static ru.hh.jooq.tables.EmployerService.EMPLOYER_SERVICE;
import static ru.hh.jooq.tables.Service.SERVICE;
import static ru.hh.jooq.tables.Site.SITE;

public class SQLBuilder {

  @Test
  public void простой_селект() {
    SelectQuery<Record> query = dsl()
        .select()
        .from(BILL)
        .limit(10)
        .getQuery();

    // select "public"."bill"."bill_id", "public"."bill"."creation_time", "public"."bill"."payment_time", "public"."bill"."status", "public"."bill"."price", "public"."bill"."payment_type", "public"."bill"."delivery_address", "public"."bill"."original", "public"."bill"."payment_order_num", "public"."bill"."payment_order_date", "public"."bill"."is_deleted", "public"."bill"."approved_by_manager_id", "public"."bill"."uid", "public"."bill"."visibility", "public"."bill"."offshore", "public"."bill"."moneta", "public"."bill"."type", "public"."bill"."paid_deed", "public"."bill"."bill_subject", "public"."bill"."bank_detail_id", "public"."bill"."need_original", "public"."bill"."blocked_price", "public"."bill"."payment_order_date_tmp", "public"."bill"."extract_order_date", "public"."bill"."invoice_create", "public"."bill"."wait_for_pay", "public"."bill"."account_id", "public"."bill"."recipient_id", "public"."bill"."partial", "public"."bill"."credit_line", "public"."bill"."currency", "public"."bill"."initial", "public"."bill"."emoney_type", "public"."bill"."vat", "public"."bill"."bank", "public"."bill"."assist_type", "public"."bill"."cart_id", "public"."bill"."created_by_manager_id", "public"."bill"."credit", "public"."bill"."merchant_id", "public"."bill"."version_", "public"."bill"."vat_value", "public"."bill"."responsible_manager_id", "public"."bill"."price_rollback"
    // from "public"."bill"
    // limit 10;

    console_log(query);

    query.fetch().forEach(record -> {
      System.out.println("bill_id: " + record.get(BILL.BILL_ID));
      System.out.println("payment_time: " + record.get(BILL.PAYMENT_TIME));
    });
  }

  @Test
  public void join() {
    EmployerService es = EMPLOYER_SERVICE.as("es");
    Service s = SERVICE.as("s");
    // typed result set
    SelectQuery<Record2<Integer, String>> query = dsl()
        .select(es.EMPLOYER_SERVICE_ID, s.SERVICE_TYPE)
        .from(es)
        .join(s).on(es.SERVICE_ID.eq(s.SERVICE_ID))
        .orderBy(es.CREATION_TIME.desc())
        .limit(10)
        .getQuery();

    // select "es"."employer_service_id", "s"."service_type"
    // from "public"."employer_service" as "es"
    // join "public"."service" as "s" on "es"."service_id" = "s"."service_id"
    // order by "es"."creation_time" desc
    // limit 10

    console_log(query);

    query.fetch().forEach(r -> System.out.println(r.get(es.EMPLOYER_SERVICE_ID)));
  }

  @Test
  public void window_functions() {
    BillingTemplate bt = BILLING_TEMPLATE.as("bt");
    Site site = SITE.as("s");
    Field<Integer> version = field("version", Integer.class);

    SelectQuery<Record> query = dsl()
        .select(bt.fields())
        .select(site.CODE)
        .select(rank().over()
            .partitionBy(bt.TYPE, bt.LANG, bt.SITE_ID)
            .orderBy(bt.CREATION_TIME).as(version))
        .from(bt)
        .join(site).on(site.SITE_ID.eq(bt.SITE_ID))
        .getQuery();

    console_log(query);

    System.out.println(
        query.fetch().stream().collect(
            Collectors.groupingBy(r -> ImmutableTriple.of(r.get(bt.TYPE), r.get(bt.SITE_ID), r.get(bt.LANG)),
                Collectors.counting())));
  }

  @Test
  public void simple_update() {
    dsl().update(BILL)
        .set(BILL.VISIBILITY, true)
        .execute();
  }

  @Test
  public void usingPojos() {
    List<Bill> bills = dsl().select()
        .from(BILL)
        .limit(10)
        .fetchInto(Bill.class);
    System.out.println(bills);
  }
}
