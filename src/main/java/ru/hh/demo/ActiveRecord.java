package ru.hh.demo;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.junit.Assert;
import org.junit.Test;
import ru.hh.jooq.tables.Currency;
import ru.hh.jooq.tables.records.BillingSettingRecord;
import ru.hh.jooq.tables.records.SiteRecord;
import java.math.BigDecimal;
import static ru.hh.demo.DBUtils.dsl;
import static ru.hh.jooq.tables.BillingSetting.BILLING_SETTING;
import static ru.hh.jooq.tables.Currency.CURRENCY;
import static ru.hh.jooq.tables.Site.SITE;

public class ActiveRecord {

// для простого crud, можно обойтись и без hibernate,
// а воспользоваться active record pattern-ом, который реализован в jOOQ

  @Test
  public void простой_crud() {
    BillingSettingRecord setting = dsl()
        .selectFrom(BILLING_SETTING)
        .where(BILLING_SETTING.NAME.like("admin.billing.compensation.batchLimit"))
        .fetchOne();

    System.out.println(setting.getValue());
    setting.setValue("102");
    setting.store();
  }

  @Test
  public void внутри_транзакции() {
    dsl().transaction(ctx -> {
          DSLContext dsl = DSL.using(ctx);
          SiteRecord siteRecord = dsl.selectFrom(SITE)
              .where(SITE.SITE_ID.eq(1))
              .fetchOne();
          System.out.println(siteRecord);

          Assert.assertEquals(siteRecord.getAreaId(), new Integer(113));

          siteRecord.setAreaId(114);
          siteRecord.store();

          int areaId = dsl
              .select(SITE.AREA_ID)
              .from(SITE)
              .where(SITE.SITE_ID.eq(1))
              .fetchOne(SITE.AREA_ID);

          Assert.assertEquals(areaId, 114);
          System.out.println("New areaId " + areaId);

          throw new RuntimeException("Transaction rollback");
        });
  }

  @Test
  public void batch_insert() {
    dsl().transaction(ctx -> {
      DSLContext dsl = DSL.using(ctx);
      Currency cur = CURRENCY;

      int countBefore = dsl
          .selectCount()
          .from(cur)
          .fetchOne(0, Integer.class);
      System.out.println("Currencies count before: " + countBefore);

      dsl.batch(
          dsl.insertInto(cur)
              .set(cur.CODE, "INR")
              .set(cur.RATE, BigDecimal.ONE)
              .set(cur.MASCULINE, false),
          dsl.insertInto(cur)
              .set(cur.CODE, "XOF")
              .set(cur.RATE, BigDecimal.ONE)
              .set(cur.MASCULINE, false)
      ).execute();

      int countAfter = dsl.selectCount().from(cur)
          .fetchOne(0, Integer.class);

      Assert.assertEquals(countAfter, countBefore + 2);

      System.out.println("Currencies count after: " + countAfter);

      throw new RuntimeException("Transaction rollback");
    });
  }
}
