package ru.hh.demo.example;

import org.jooq.Condition;
import org.junit.Test;
import ru.hh.jooq.tables.pojos.BillingTemplate;

import java.util.List;
import java.util.Objects;

import static org.jooq.impl.DSL.trueCondition;
import static ru.hh.demo.DBUtils.dsl;
import static ru.hh.jooq.tables.BillingTemplate.BILLING_TEMPLATE;

public class BillingTemplateDao {

  private List<BillingTemplate> findTemplates(Integer siteId, Integer lang, Integer type) {
    ru.hh.jooq.tables.BillingTemplate bt = BILLING_TEMPLATE.as("bt");

    Condition where = trueCondition();
    if (Objects.nonNull(lang)) {
      where.and(bt.LANG.eq(lang));
    }
    if (Objects.nonNull(siteId)) {
      where.and(bt.SITE_ID.eq(siteId));
    }
    if (Objects.nonNull(type)){
      where.and(bt.TYPE.eq(type));
    }

    return dsl().select()
        .from(bt)
        .where(where)
        .fetchInto(BillingTemplate.class);
  }

  @Test
  public void findTemplateTest() {
    System.out.println(findTemplates(1, 1, 1));
    System.out.println(findTemplates(null, null, null));
  }
}
