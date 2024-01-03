package kopo.poly.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ApiResultDTO {
    public ApiResultDTO() {
        // 기본 생성자
    }

    private String seq;
    private String rnum;                    //row번호
    private String biz_id;                  //정책 ID
    private String poly_biz_secd;           //정책일련번호
    private String poly_biz_ty;             //기관 및 지자체 구분
    private String poly_biz_sjnm;           //정책명
    private String poly_itcn_cn;            //정책소개

    private String spor_cn;                 //지원내용
    private String spor_scvl;               //지원규모
    private String bizPrdCn;
    private String prd_rptt_secd;
    private String rqut_prd_cn;             //신청기간
    private String age_info;                //참여요건 - 연령
    private String majr_rqis_cn;            //참여요건 - 전공
    private String empm_stts_cn;            //참여요건 - 취업상태
    private String splz_rlm_rqis_cn;        //참여요건 - 특화분야
    private String accr_rqis_cn;            //참여요건 - 학력
    private String prcp_cn;                 //거주지 및 소득조건
    private String aditRscn;
    private String prcp_lmtt_trgt_cn;       //참여제한대상
    private String rqut_proc_cn;            //신청절차
    private String pstn_papr_cn;            //제출서류
    private String jdgn_pres_cn;            //심사발표
    private String rqut_urla;               //신청 사이트 링크 주소
    private String rfcSiteUrla1;
    private String rfcSiteUrla2;
    private String mngtMson;
    private String mngtMrofCherCn;
    private String cherCtpcCn;
    private String cnsg_nmor;               //신청기관명
    private String tintCherCn;
    private String tintCherCtpcCn;
    private String etct;
    private String poly_rlm_cd;             //정책분야코드
    private String srch_poly_biz_secd;      //지역코드


    private String polyCode;
    private String areaCode;
    private String reg_dt;
}
