package kopo.poly.util;

import java.io.File;

public class FileUtil {
    // 현재 날짜를 기준으로 년/월/일 폴더 생성하기 (파일 관리는 무조건 년월일!!)

    public static String mkdirForDate(String uploadDir) {
        // 파일을 저장하기 위한 폴더는 년월일로 폴더를 생성함
        // 해당되는 날짜 폴더만 삭제하면, 한번에 삭제되기에 관리가 쉬움

        String path = uploadDir + DateUtil.getDateTime("/yyyy/MM/dd");

        File Folder = new File(path);

        // 저장하기 위한 폴더가 존재하지 않으면 폴더 생성
        if(!Folder.exists()) {
            Folder.mkdirs();
        }
        return path;
    }
}
