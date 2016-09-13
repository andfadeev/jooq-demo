package ru.hh.demo;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.SelectQuery;
import org.jooq.conf.Settings;
import org.jooq.conf.StatementType;
import org.jooq.impl.DSL;
import org.postgresql.jdbc2.optional.SimpleDataSource;

public class DBUtils {
  private static final String USERNAME = "hh";
  private static final String PASSWORD = "123";
  private static final String DB_URL = "jdbc:postgresql://ts27.pyn.ru:5432/hh";

  public static DSLContext dsl() {
    SimpleDataSource ds = new SimpleDataSource();
    ds.setUrl(DB_URL);
    ds.setUser(USERNAME);
    ds.setPassword(PASSWORD);
    Settings settings = new Settings().withStatementType(StatementType.STATIC_STATEMENT);
    return DSL.using(ds, SQLDialect.POSTGRES, settings);
  }

  static void console_log(SelectQuery query) {
    System.out.println(query.getSQL());
    System.out.println(query.fetch());
  }
}
