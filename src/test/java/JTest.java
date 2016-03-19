import com.zedcn.qingstor.conn.QingStorConnection;
import com.zedcn.qingstor.elements.QingStorBucket;
import org.junit.Test;

public class JTest {

    @Test
    public void testSign() {
        QingStorBucket bucket = new QingStorBucket();
        bucket.setName("chailv").setAccessKey("TWUEZOBZHCCFCFBILWWL").setAccessSecret("5IqDfSC28vOQq1mlimX3WDBzylBFIeLUghE3arX7");
        QingStorConnection connection = QingStorConnection.create(bucket);
//        System.out.println(connection.isBucketExist());
        connection.statistics();
    }
}
