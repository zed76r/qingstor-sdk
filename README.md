# qingstor-sdk
自制版青云对象存储SDK

### Quick Start

```gradle
compile 'com.zedcn:qingstor-sdk:1.0.0.alpha'
```

！如果使用Jar包，需要依赖其他的Jar包

```gradle
    compile 'com.squareup.okhttp3:okhttp:3.4.1'
    compile 'com.google.code.gson:gson:2.6.2'
```

----------------------------------

### How to use

```java
ObjectApi api = ObjectApi.Builder.newApi(bucket);
api.delete("anyObj");
api.get("excited").getContent();
```
