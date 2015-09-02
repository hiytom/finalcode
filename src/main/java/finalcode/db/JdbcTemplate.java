package finalcode.db;

import finalcode.processhtml.bean.DataTable;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by peng_chao on 15-8-29.
 */
public final class JdbcTemplate {

    private static final int BATCH_SIZE = 2000;

    public static void insertBatch(LinkedBlockingQueue<DataTable> dataTables) {

        if (dataTables.size() < BATCH_SIZE) {
            return;
        }

        org.springframework.jdbc.core.JdbcTemplate jdbcTemplate =
                new org.springframework.jdbc.core.JdbcTemplate(DBManager.getDatasource());

        StringBuilder sb = new StringBuilder();
        sb.append("insert into metadata (")
                .append("link,companyName,workPlace,lowestExperience,tiptopExperience,educational,")
                .append("wayOfWork,profession,releaseDate,financing,lowestWage,tiptopWage,welfare,language,skill,html")
                .append(")values(")
                .append("?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?")
                .append(")");

        jdbcTemplate.batchUpdate(sb.toString(), new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                DataTable d = dataTables.poll();
                ps.setString(1, d.link);
                ps.setString(2, d.companyName);
                ps.setString(3, d.workPlace);
                ps.setInt(4, d.lowestExperience);
                ps.setInt(5, d.tiptopExperience);
                ps.setString(6, d.educational);
                ps.setString(7, d.wayOfWork);
                ps.setString(8, d.profession);
                ps.setDate(9, (Date) d.releaseDate);
                ps.setString(10, d.financing);
                ps.setInt(11, d.lowestWage);
                ps.setInt(12, d.tiptopWage);
                ps.setString(13, d.welfare);
                ps.setString(14, d.language);
                ps.setString(15, d.skill);
                ps.setString(16, d.html);
            }

            @Override
            public int getBatchSize() {
                return BATCH_SIZE;
            }
        });

    }

}
