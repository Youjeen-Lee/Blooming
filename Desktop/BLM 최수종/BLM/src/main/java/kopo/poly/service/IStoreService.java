package kopo.poly.service;

import kopo.poly.dto.StoreDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IStoreService {

    List<StoreDTO> getStoreList() throws Exception;

    StoreDTO getStoreInfo(StoreDTO pDTO) throws Exception;

    StoreDTO getMainStoreInfo() throws Exception;

    int insertStoreInfo(StoreDTO pDTO, List<MultipartFile> files) throws Exception;

}