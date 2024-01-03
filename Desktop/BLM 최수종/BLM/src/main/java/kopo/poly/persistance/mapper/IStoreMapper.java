package kopo.poly.persistance.mapper;

import kopo.poly.dto.StoreDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IStoreMapper {

    List<StoreDTO> getStoreList() throws Exception;

    StoreDTO getStoreInfo(StoreDTO pDTO) throws Exception;

    StoreDTO getMainStoreInfo() throws Exception;

    int insertStoreInfo(StoreDTO pDTO) throws Exception;

}
