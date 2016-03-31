import com.zedcn.qingstor.conn.QingStorConnection;
import com.zedcn.qingstor.elements.QingCloudAccess;
import com.zedcn.qingstor.elements.QingStorBucket;
import org.junit.Test;

import java.util.List;

public class JTest {

    @Test
    public void testSign() {
        QingCloudAccess bucket = new QingCloudAccess();
        bucket.setAccessKey("UGMOXFTZIYFNVRDJSGWJ").setAccessSecret("V2iFT1kZH93exzZmZ869AuvX1gYRWIyyHMsTxFsj");
//        QingStorConnection connection = QingStorConnection.create(bucket);
//        System.out.println(connection.isBucketExist());
//        bucket = connection.statistics();
//        bucket.getName();
//        QingStorObject object = connection.getObject("hosts");
//        object.getKey();
//        try {
//            QingStorObject object = new QingStorObject();
//            object.setKey("lyl.dog")
//                    .setContentType(QingStorObject.ContentType.IMAGE_ALL)
//                    .setContent(new FileInputStream("D:\\images\\hzt\\1.jpg"))
//                    .autoContentLength();
//            connection.putObject(object);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        connection.deleteObject("lyl.dog");
        List<QingStorBucket> buckets = QingStorConnection.getAllBuckets(bucket);
        System.out.println(buckets);

    }
}
