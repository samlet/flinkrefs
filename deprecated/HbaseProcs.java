package com.bluecc.fixtures;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HbaseProcs {

    public static void main(String[] args) throws IOException {
        /*
        HbaseFac client=Modules.build().getInstance(HbaseFac.class);
        List<Entry> ps = client.getRow("student", "row1");
        System.out.println("total "+ps.size());
        ps.forEach(System.out::println);
         */

        new HbaseChecker().check();
    }

    private static class HbaseChecker{
        private TableName table1 = TableName.valueOf("Table3");
        private String family1 = "Family1";
        private String family2 = "Family2";

        void check() throws IOException {
            Configuration config = HBaseConfiguration.create();

            String path = HbaseProcs.class
                    .getClassLoader()
                    .getResource("hbase-site.xml")
                    .getPath();
            config.addResource(new Path(path));

            HBaseAdmin.available(config);

            Connection connection = ConnectionFactory.createConnection(config);
            Admin admin = connection.getAdmin();

            HTableDescriptor desc = new HTableDescriptor(table1);
            desc.addFamily(new HColumnDescriptor(family1));
            desc.addFamily(new HColumnDescriptor(family2));
            admin.createTable(desc);

            // insert
            byte[] row1 = Bytes.toBytes("row1");
            Put p = new Put(row1);
            p.addImmutable(family1.getBytes(), qualifier1, Bytes.toBytes("cell_data"));
            table1.put(p);
        }
    }
}
