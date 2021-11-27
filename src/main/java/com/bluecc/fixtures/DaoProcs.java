package com.bluecc.fixtures;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.customizer.BindMap;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.statement.UseRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static java.util.Collections.singletonMap;

public class DaoProcs {
    public static void main(String[] args) {
        Jdbi jdbi = Jdbi.create("jdbc:h2:mem:test"); // (H2 in-memory database)
        jdbi.installPlugin(new SqlObjectPlugin());
        procs(jdbi);
        procsBean(jdbi);
    }

    private static void procs(Jdbi jdbi) {
        jdbi.withHandle(handle -> {
            Dao dao = handle.attach(Dao.class);
            createTable(handle);
            handle.execute("insert into something (id, name) values (1, 'Alice')");
            // dao.update(1, singletonMap("name", "Alicia"));
            dao.updatePrefix(1, singletonMap("name", "Alicia"));
            System.out.println(dao.get(1).getName());
            return null;
        });
    }

    private static void procsBean(Jdbi jdbi) {
        jdbi.withHandle(handle -> {
            BeanDao dao = handle.attach(BeanDao.class);
            createTable(handle);
            handle.execute("insert into something (id, name) values (1, 'Alice')");
            System.out.println(dao.getName(1));
            dao.update(new Something(1, "Alicia"));
            System.out.println(dao.getName(1));
            return null;
        });
    }

    private static void createTable(Handle handle) {
        handle.execute("create table something (" +
                "id identity primary key, name varchar(50), " +
                "integerValue integer, " +
                "intValue integer)");
    }

    public interface BeanDao {
        @SqlUpdate("update something set name=:name where id=:id")
        void update(@BindBean Something thing);

        @SqlUpdate("update something set name=:thing.name where id=:thing.id")
        void updatePrefix(@BindBean("thing") Something thing);

        @SqlQuery("select name from something where id = :id")
        String getName(@Bind("id") long id);
    }

    public interface Dao {
        @SqlUpdate("update something set name=:name where id=:id")
        void update(@Bind int id, @BindMap Map<Object, Object> map);

        @SqlUpdate("update something set name=:map.name where id=:id")
        void updatePrefix(@Bind("id") int id, @BindMap("map") Map<String, Object> map);

        @SqlUpdate("update something set name=:name where id=:id")
        void updateNameKey(@Bind int id, @BindMap(keys = "name") Map<String, Object> map);

        @SqlUpdate("update something set name=:name where id=:id")
        void updateConvertKeys(@Bind int id, @BindMap(convertKeys = true) Map<Object, Object> map);

        @SqlQuery("select * from something where id=:id")
        @UseRowMapper(SomethingMapper.class)
        Something get(@Bind("id") long id);
    }

    public static class SomethingMapper implements RowMapper<Something> {
        @Override
        public Something map(ResultSet r, StatementContext ctx) throws SQLException {
            return new Something(r.getInt("id"), r.getString("name"));
        }
    }

}
