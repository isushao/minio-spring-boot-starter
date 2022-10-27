package com.roc.minio.autoconfigure;

import cn.hutool.core.io.file.FileNameUtil;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static cn.hutool.core.io.file.FileNameUtil.UNIX_SEPARATOR;

/**
 * @author susp
 */
public class MinioServiceImpl implements MinioService {
    private static final List<String> extNames = Arrays.asList("zip", "pdf", "txt", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "wps", "jpg", "JPG", "png", "PNG", "gif", "jfif");


    @Autowired
    MinioClient minioClient;

    @Override
    public ObjectWriteResponse putObject(String object, String bucketName, InputStream stream, long objectSize, String contentType) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        ServerSideEncryption sseS3 = new ServerSideEncryptionS3();
        return minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(object)
                .stream(stream, objectSize, -1)
                .sse(sseS3)
                .contentType(contentType)
                .build());
    }

    @Override
    public void removeObject(String object, String bucketName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(bucketName)
                .object(object)
                .build());
    }

    @Override
    public String getPresignedObjectUrl(String object, String bucketName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return getPresignedObjectUrl(object, bucketName, 7, TimeUnit.DAYS);
    }

    @Override
    public String getPresignedObjectUrl(String object, String bucketName, int duration, TimeUnit unit) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .object(object)
                        .expiry(duration, unit)
                        .build());
    }


    /**
     * 判断 bucket是否存在
     *
     * @param bucketName 桶名称
     */
    @Override
    public boolean bucketExists(String bucketName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(bucketName)
                .build());
    }

    /**
     * 创建 bucket
     */
    @Override
    public void makeBucket(String bucketName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        boolean isExist = bucketExists(bucketName);
        if (!isExist) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
        }
    }

    @Override
    public InputStream getObjectInputStream(String bucket, String objectName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        boolean exists = bucketExists(bucket);
        if (exists) {
            StatObjectResponse objectStat = minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .build());
            if (objectStat.size() > 0) {
                // 获取objectName的输入流。
                return minioClient.getObject(GetObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectName)
                        .build());
            }
        }
        return null;
    }


    public static String cleanInvalid(String fileName) {
        String mainName = FileNameUtil.mainName(fileName);
        String extName = FileNameUtil.extName(fileName);
        String newMainName = mainName.replace(".", "-");
        if (extNames.contains(extName)) {
            fileName = fileName.replace(mainName, newMainName);
            return fileName;
        }
        throw new RuntimeException("文件格式非法");
    }

    @Override
    public String generateObjectName(String fileName) {
        LocalDate now = LocalDate.now();
        String url = "files/" + now.format(DateTimeFormatter.BASIC_ISO_DATE) + UNIX_SEPARATOR;
        return url + cleanInvalid(fileName);
    }
}
