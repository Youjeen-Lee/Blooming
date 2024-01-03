package kopo.poly.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreDTO {

    private String user_id;

    private String store_seq;
    private String store_owner_name;
    private String store_name;
    private String store_introduce;

    private String store_addr;
    private String store_addr2;
    private String store_call;

    private String store_main_photo;
    private String store_sub_photo_one;
    private String store_sub_photo_two;
    private String store_sub_photo_three;

    private String latitude;
    private String longitude;
}
