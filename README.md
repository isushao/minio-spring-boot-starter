# 背景

minio用作云原生应用程序的主要存储，与传统对象存储相比，MinIO能够提供更高的吞吐量和更低的延迟。

# 最佳实践

# 部署

### 访问策略

minio访问策略有两种设置：

【必须】private （私有，只能通过权限校验获取有效时间内的文件访问地址）

【禁止】public （公开，路径对外开放，没有任何权限校验和过期处理）

### 加密

MinIO使用密钥管理系统（KMS）支持SSE-S3。如果客户端请求SSE-S3，或 启用了自动加密，则MinIO服务器会使用唯一的对象密钥对每个对象进行加密，该对象密钥受 KMS管理的主密钥保护。

┌─────────┐         ┌────────────┐         ┌─────────┐

│  MinIO  ├─────────┤ KES Server ├─────────┤   KMS   │

└─────────┘         └────────────┘         └─────────┘

设置成功后，存储文件到minio上，在进行落盘时会自动加密，获取文件时会自动解密。以达到相当高的安全要求。

# 客户端SDK

### minIo spring boot starter

由于MinIo只提供了Java sdk，对于spring boot项目来说，需要引入sdk依赖，再进行配置才可以使用。故封装了spring boot starter，自动加载Minio配置，创建minioClient。达到依赖即已集成MinIo的效果。

对文件存储路径统一规范，路径：*files/时间戳（yyyyMMdd)/xxx.txt*

也加入了文件名校验，处理（重命名等）、文件后缀校验（过滤非法后缀，例如js、py..)等功能。避免项目漏掉或重复处理，带来安全隐患。

使用到的依赖如下： io.minio minio 8.4.1

### 开始吧

添加依赖

```xml
<dependency>
   <groupId>com.xxx</groupId>
   <artifactId>minio-spring-boot-starter</artifactId>
   <version>Latest Version</version>
</dependency>
```

注：目前测试版本：0.0.1-SNAPSHOT添加配置

```yaml
minio:
	endpoint:
	access-key:
	secret-key:
```

引入接口：MinioService，使用其中方法注意事项：有可能会遇到okhttp、kotlin-stdlib版本冲突等问题，已经有okhttp依赖的可以忽略，没有kotlin-stdlib的需要添加依赖

```xml
   <dependency>
        <groupId>org.jetbrains.kotlin</groupId>
        <artifactId>kotlin-stdlib</artifactId>
        <version>1.3.72</version>
        <scope>compile</scope>
    </dependency>
```

```java
/**
*
*@author  susp
*/

		@RestController
    public class MinioController {

        @Autowired
        MinioService minioService;
    	@Value("${minio.bucketName}")
        private String bucketName;

        @RequestMapping("/minio/test")
        public void test(MultipartFile file){

            String object = minioService.generateObjectName(file.getOriginalFilename());

            try {
                minioService.putObject(object,bucketName ,file.getInputStream(),file.getSize(),file.getContentType()  );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
```
