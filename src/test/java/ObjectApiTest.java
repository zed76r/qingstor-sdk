import com.zedcn.qingstor.api.ObjectApi;
import com.zedcn.qingstor.elements.QingStorBucket;
import com.zedcn.qingstor.elements.QingStorObject;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * ObejctAPI测试类
 * Created by Zed on 2017/5/17.
 */
public class ObjectApiTest {

    private static final QingStorBucket bucket = (QingStorBucket) new QingStorBucket()
            .setName("***")
            .setAccessKey("***")
            .setAccessSecret("***");

    @Test
    public void 测试文件对象上传() throws IOException {
        ObjectApi objectApi = ObjectApi.Builder.newApi(bucket);
        File file = new File("/Data/temp.xlsx");
        assert file.exists();
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] content = new byte[fileInputStream.available()];
        //noinspection ResultOfMethodCallIgnored
        fileInputStream.read(content, 0, content.length);
        QingStorObject qingStorObject = new QingStorObject()
                .setContentBinary(content)
                .setContentLength(content.length)
                .setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .setKey("temp.xlsx");
        objectApi.create(qingStorObject);
    }

    @Test
    public void 测试文件对象上传2() throws IOException {
        ObjectApi objectApi = ObjectApi.Builder.newApi(bucket);
        File file = new File("/Data/1.jpg");
        assert file.exists();
        FileInputStream fileInputStream = new FileInputStream(file);
        QingStorObject qingStorObject = new QingStorObject()
                .setContent(fileInputStream)
                .autoContentLength()
                .setContentType(QingStorObject.ContentType.IMAGE_JPG)
                .setKey("1.jpg");
        objectApi.create(qingStorObject);
    }

    @Test
    public void 测试删除对象() {
        ObjectApi objectApi = ObjectApi.Builder.newApi(bucket);
        objectApi.delete("temp.xlsx");
    }
}
