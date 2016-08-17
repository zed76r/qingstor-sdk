# qingstor-sdk
自制版青云对象存储SDK

### Quick Start

```gradle
compile 'com.zedcn:qingstor-sdk:0.4.alpha'
```

！如果使用Jar包，需要依赖其他的Jar包

```gradle
    compile 'com.squareup.okhttp3:okhttp:3.4.1'
    compile 'com.google.code.gson:gson:2.6.2'
```

----------------------------------

### Example

```java
ObjectApi api = ObjectApi.newApi(bucket);
api.delete("anyObj");
```
