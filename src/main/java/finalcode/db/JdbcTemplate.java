package finalcode.db;

import finalcode.App;
import finalcode.operatedata.ConcurrentData;
import finalcode.processHtml.bean.DataTable;
import finalcode.utils.FinalCodeUtil;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by peng_chao on 15-8-29.
 */
public final class JdbcTemplate {

    public static void initDataBases() {
        StringBuilder sb = new StringBuilder();

        sb.append("drop database if exists finalcode;");
        sb.append("create database finalcode;");
        sb.append("use finalcode;");

        String tableBeanClassPath = App.table;
        String tableName = tableBeanClassPath.substring(tableBeanClassPath.lastIndexOf(".") + 1,
                tableBeanClassPath.length());

        sb.append("CREATE TABLE `" + tableName + "` (`id` int(11) unsigned NOT NULL AUTO_INCREMENT,");

        Class<?> tables = null;
        try {
            tables = Class.forName(App.table);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Field[] fields = tables.getFields();
        for (Field field : fields) {
            String tableFieldTypeStr =  FinalCodeUtil.getTableFiledType(field);
            String tableFiledName = field.getName();
            sb.append("`" + tableFiledName + "` " + tableFieldTypeStr + ",");
        }

        sb.append("PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8;");
    }


    public static void batchAdd() {
        LinkedBlockingQueue<DataTable> data = ConcurrentData.DATA;

        if (!data.isEmpty()) {
            String sql = "";
            Connection conn = null;
            PreparedStatement pstm = null;
            try {
                conn = DBManager.getConnection();
                conn.setAutoCommit(false);
                pstm = conn.prepareStatement(sql);
            while (data.peek() != null) {
                data.poll();
                //pstm = setStatement(writeLog, pstm);
                pstm.addBatch();
            }
            pstm.executeBatch();
            conn.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

}
