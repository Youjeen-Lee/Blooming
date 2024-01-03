package kopo.poly.service.impl;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kopo.poly.dto.StoreDTO;
import kopo.poly.persistance.mapper.IStoreMapper;
import kopo.poly.service.IStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreService implements IStoreService {

    @Value("${accessKey}")
    private String accessKey;
    @Value("${secretKey}")
    private String secretKey;

    private final IStoreMapper storeMapper;

    // 청년점포 게시판 진입
    @Override
    public List<StoreDTO> getStoreList() throws Exception {
        log.info(this.getClass().getName() + ".청년점포 리스트 서비스 호출 받았습니다.");

        return storeMapper.getStoreList();
    }

    // 청년점포 등록
    @Override
    public int insertStoreInfo(StoreDTO pDTO, List<MultipartFile> files) throws Exception {
        log.info(this.getClass().getName() + "점포 정보 등록, 서비스 실행합니다.");


        final String endPoint = "https://kr.object.ncloudstorage.com";
        final String regionName = "kr-standard";

        // S3 client
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endPoint, regionName))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .build();

        String bucketName = "bluefingers97";

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            try {
                // 여기부터
                String originalFilename = file.getOriginalFilename();

                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(file.getSize());
                metadata.setContentType(file.getContentType());

                // MultipartFile의 InputStream을 읽어서 byte 배열로 변환
                byte[] fileContent = IOUtils.toByteArray(file.getInputStream());

                // byte 배열을 사용하여 S3에 업로드
                s3.putObject(bucketName, originalFilename, new ByteArrayInputStream(fileContent), metadata);

                // 기존 ACL 가져오기
                AccessControlList acl = s3.getObjectAcl(bucketName, originalFilename);

                // 읽기 권한 추가
                s3.setObjectAcl(bucketName, originalFilename, CannedAccessControlList.PublicRead);

                String photoUrl = s3.getUrl(bucketName, originalFilename).toString();
                log.info("photoUrl : " + photoUrl);

                if(i == 0) {
                    pDTO.setStore_main_photo(photoUrl);
                } else if(i == 1) {
                    pDTO.setStore_sub_photo_one(photoUrl);
                } else if(i == 2) {
                    pDTO.setStore_sub_photo_two(photoUrl);
                } else if(i == 3) {
                    pDTO.setStore_sub_photo_three(photoUrl);
                }

            } catch (AmazonS3Exception e) {
                e.printStackTrace();
            } catch (SdkClientException e) {
                e.printStackTrace();
            }
        }

        log.info(this.getClass().getName() + "점포 정보 등록, 서비스 종료합니다.");

        return storeMapper.insertStoreInfo(pDTO);
    }

    @Override
    public StoreDTO getStoreInfo(StoreDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".청년점포 상세보기 서비스 호출 받았습니다.");

        StoreDTO rDTO = storeMapper.getStoreInfo(pDTO);

        return rDTO;
    }

    @Override
    public StoreDTO getMainStoreInfo() throws Exception {
        log.info(this.getClass().getName() + ".청년점포 메인에 띄울 가게 정보 호출합니다.");
        log.info(this.getClass().getName() + ".청년점포 메인에 띄울 가게 정보 호출 종료합니다.");

        return storeMapper.getMainStoreInfo();
    }




}
