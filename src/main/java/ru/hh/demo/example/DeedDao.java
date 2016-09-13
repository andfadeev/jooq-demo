package ru.hh.demo.example;

import org.jooq.Field;
import org.jooq.Result;
import org.junit.Test;
import ru.hh.jooq.tables.Deed;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.sum;
import static ru.hh.demo.DBUtils.dsl;
import static ru.hh.jooq.tables.Deed.DEED;

public class DeedDao {
  //  public Collection<Integer> getDeedIdsBetweenDates(LocalDate from, LocalDate to) {
//    SQLQuery query = sessionFactory.getCurrentSession()
//        .createSQLQuery(DeedQuery.getDeedIdsBetweenDatesQuery(from, to.plusDays(1)));
//    query.addScalar("deed_id", StandardBasicTypes.INTEGER);
//    query.setCacheMode(CacheMode.IGNORE);
//    return query.list();
//  }
  private List<Integer> getDeedIdsBetweenDates(LocalDate from, LocalDate to) {
    Deed d = DEED.as("d");
    return dsl().select(d.DEED_ID)
        .from(d)
        .where(d.CREATION_TIME.ge(from.atStartOfDay()),
            d.CREATION_TIME.lt(to.atStartOfDay()),
            d.VISIBILITY.isTrue())
        .orderBy(d.CREATION_TIME, d.DEED_ID)
        .fetch(d.DEED_ID);
  }

  @Test
  public void getDeedIdsBetweenDatesTest() {
    System.out.println(getDeedIdsBetweenDates(LocalDate.now().minusMonths(2), LocalDate.now()));
  }

  //  public List<DeedStatisticPair> getDeedsStatisticForPeriod(Date startDate, Date endDate, boolean isVisible) {
//    String hql =
//        " SELECT new ru.hh.billing.dao.DeedStatisticPair(d.price.currency, sum(d.price.amount), count(d)) " +
//            " FROM Deed d " +
//            " WHERE " +
//            "     d.creationTime >= :startDate " +
//            " AND d.creationTime <= :endDate " +
//            " AND d.visible = :isVisible " +
//            " GROUP BY d.price.currency ";
//    Query query = sessionFactory.getCurrentSession().createQuery(hql);
//    query.setParameter("startDate", startDate);
//    query.setParameter("endDate", endDate);
//    query.setParameter("isVisible", isVisible);
//    return query.list();
//  }
  private Result getDeedsStatisticForPeriod(LocalDateTime start, LocalDateTime end, boolean isVisible) {
    Deed d = DEED.as("d");
    Field<Integer> deed_count = field("deed_count", Integer.class);
    Field<BigDecimal> price_sum = field("price_sum", BigDecimal.class);
    return dsl().select(d.CURRENCY, sum(d.PRICE).as(price_sum), count().as(deed_count))
        .from(d)
        .where(d.CREATION_TIME.ge(start))
        .and(d.CREATION_TIME.le(end))
        .and(d.VISIBILITY.eq(isVisible))
        .groupBy(d.CURRENCY)
        .fetch();
  }

  @Test
  public void getDeedsStatisticForPeriodTest() {
    System.out.println(getDeedsStatisticForPeriod(LocalDateTime.now().minusMonths(2), LocalDateTime.now(), true));
  }
}

