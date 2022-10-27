package com.roc.minio.autoconfigure;

import io.minio.ObjectWriteResponse;
import io.minio.errors.*;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

/**
 * @author susp
 */
public interface MinioService {
     /**
      * 上传文件
      * @param object 文件对象（路径）
      * @param bucketName bucket
      * @param stream InputStream
      * @param objectSize file size
      * @param contentType file contentType
      * @return 上传结果
      */
     ObjectWriteResponse putObject(String object, String bucketName, InputStream stream, long objectSize, String contentType) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

     /**
      * 删除文件
      * @param object 文件对象（路径）
      * @param bucketName bucket
      */
     void removeObject(String object, String bucketName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

     /**
      * 获取文件加密地址（包含auth、expiresTime）
      *   默认7天
      * @param object 文件对象（路径）
      * @param bucketName bucket
      * @return 带有过期时间、鉴权信息的文件地址
      */
     String getPresignedObjectUrl(String object, String bucketName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

     /**
      * 获取文件加密地址（包含auth、expiresTime）
      *
      * @param object 文件对象（路径）
      * @param bucketName bucket
      * @param duration 过期时间（数值）
      * @param unit 过期时间（单位）
      * @return 带有过期时间、鉴权信息的文件地址
      */
     String getPresignedObjectUrl(String object, String bucketName, int duration, TimeUnit unit) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

     /**
      * bucket 是否存在
      * @param bucketName 桶名
      * @return true-存在 false-不存在
      */
     boolean bucketExists(String bucketName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

     /**
      * 创建桶
      * @param bucketName 桶名
      */
     void makeBucket(String bucketName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

     /**
      * 获取文件流
      * @param bucket bucket
      * @param objectName 文件对象（路径）
      * @return InputStream
      */
     InputStream getObjectInputStream(String bucket, String objectName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

    String generateObjectName(String fileName);
}
