# qingstor-sdk
自制版青云对象存储SDK

Click to [JavaDoc](http://cn-zed.github.io/qingstor-sdk/javadoc/)

### Quick Start

```gradle
compile 'com.zedcn:qingstor-sdk:1.0.0.alpha'
```

！如果使用Jar包，需要依赖其他的Jar包

```groovy
compile 'com.squareup.okhttp3:okhttp:latest.release'
compile 'com.google.code.gson:gson:latest.release'
```

----------------------------------

### How to use

```java
ObjectApi api = ObjectApi.Builder.newApi(bucket);
api.delete("interesting");
api.get("excited").getContent();
```
