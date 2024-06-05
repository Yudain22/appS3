package org.zerock.apps3.util;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Log4j2
public class S3Uploader {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;

    //S3 파일 업로드
    public String upload(String filePath) throws RuntimeException {
        //UploadLocal에서 저장한 파일의 전체 경로를 이용하여 파일 불러오기
        File targetFile = new File(filePath);
        //putS3메서드를 이용하여 S3스토리지에 파일 저장
        String uploadImageUrl = putS3(targetFile, targetFile.getName());

        removeOriginalFile(targetFile);
        return uploadImageUrl;

    }
    //s3 업로드
    private String putS3(File uploadfile, String fileName) throws RuntimeException {
        //putObject메서드를 이용하여 S3스토리지에 파일 저장
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName,uploadfile)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        //S3에 저장된 파일을 불러올 수 있는 주소를 반환
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    private void removeOriginalFile(File targetFile) {
        if (targetFile.exists() && targetFile.delete()) {
            log.info("File delete success ");
            return;
        }
        log.info("fail to remove");
    }

    public void removeS3File(String fileName){
        final DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, fileName);
        amazonS3Client.deleteObject(deleteObjectRequest);
    }
}
