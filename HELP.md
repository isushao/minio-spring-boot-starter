# CityOs MinIo Spring Boot Starter

### MinIo spring boot starter

由于MinIo只提供了Java sdk，对于spring boot项目来说，需要引入sdk依赖，再进行配置才可以使用。此项目自定义spring boot starter，自动
加载Minio配置，实现自动装配，达到引入即已集成MinIo的效果。

使用到的依赖如下：
<dependency>
    <groupId>io.minio</groupId>
    <artifactId>minio</artifactId>
    <version>8.4.1</version>
</dependency>


## 开始吧
- 添加依赖
   ```xml
      <dependency>
          <groupId>com.cityos</groupId>
          <artifactId>minio-spring-boot-starter</artifactId>
          <version>Latest Version</version>
      </dependency>

- 添加配置
    ```yaml
     minio:
       endpoint:
       access-key:
       secret-key:
  
- 引入接口：MinioService，使用其中方法

###注意事项：
有可能会遇到okhttp、kotlin-stdlib版本冲突等问题，已经有okhttp依赖的可以忽略，没有kotlin-stdlib的需要添加依赖
    
    <dependency>
        <groupId>org.jetbrains.kotlin</groupId>
        <artifactId>kotlin-stdlib</artifactId>
        <version>1.3.72</version>
        <scope>compile</scope>
    </dependency>


####示例代码
```Java
import com.cityos.minio.autoconfigure.MinioService;
import com.thecityos.city.indicator.common.constants.Constants;
import com.thecityos.city.indicator.common.utils.MinIoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author susp
 */

@RestController
public class MinioController {
    @Autowired
    MinioService minioService;

    @RequestMapping("/minio/test")
    public void test(MultipartFile file){


        String fileName = MinIoUtil.cleanInvalid(file.getOriginalFilename());
        String path = Constants.OPERATE_PATH + "test" + "/" + fileName;
        try {
            minioService.putObject(path,"fyzxl",file.getInputStream(),file.getSize(),file.getContentType()  );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}