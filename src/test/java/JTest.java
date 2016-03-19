import com.zedcn.qingstor.conn.QingStorConnection;
import com.zedcn.qingstor.elements.QingStorBucket;
import com.zedcn.qingstor.elements.QingStorObject;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class JTest {

    @Test
    public void testSign() {
        QingStorBucket bucket = new QingStorBucket();
        bucket.setName("develop").setAccessKey("TWUEZOBZHCCFCFBILWWL").setAccessSecret("5IqDfSC28vOQq1mlimX3WDBzylBFIeLUghE3arX7");
        QingStorConnection connection = QingStorConnection.create(bucket);
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
        connection.deleteObject("lyl.dog");

    }
}
