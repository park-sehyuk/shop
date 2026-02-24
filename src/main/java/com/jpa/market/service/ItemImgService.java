package com.jpa.market.service;



import com.jpa.market.dto.ItemImgDto;
import com.jpa.market.entity.Item;
import com.jpa.market.entity.ItemImg;
import com.jpa.market.mapper.ItemMapper;
import com.jpa.market.repository.ItemImgRepository;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;


@Service
@RequiredArgsConstructor      // 주요 필드에 대한 생성자를 자동으로 만들어주는 어노테이션
public class ItemImgService {

    private final ItemImgRepository itemImgRepository;

    private final FileService fileService;

    // 엔티티는 객체이므로 객체값만 바꾸면 DB값도 변경되므로 엔티티로 바로 사용함
    public void saveItemImg(ItemImg itemImg, MultipartFile itemImgFile) throws Exception{
        String oriImgName = itemImgFile.getOriginalFilename();
        String imgName ="";
        String imgUrl ="";

        // 사용자가 파일을 등록했으면
        if(!StringUtils.isEmpty(oriImgName)){
            imgUrl = fileService.uploadFile("items", oriImgName, itemImgFile.getBytes());

            // 저장된 전체 경로에서 s3 key(items/uuid.jpg)만 추출
            if(imgUrl.contains(".com/")){
                imgName = imgUrl.substring(imgUrl.lastIndexOf(".com/") + 5);
            }
        }

        itemImg.updateItemImg(imgName, oriImgName, imgUrl, itemImg.getRepImgYn());

        itemImgRepository.save(itemImg);
    }

    // 이미지 삭제
    public void deleteItemImg(Item item) throws Exception{
        // 해당 상품에 연결된 모든 이미지를 조회
        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(item.getId());

        for(ItemImg itemImg : itemImgList){

            String s3Key = itemImg.getImgName();

            // 이미지를 실제 삭제 처리
            if(!StringUtils.isEmpty(s3Key))
                fileService.deleteFile(s3Key);

            // DB에서 삭제
            itemImgRepository.delete(itemImg);
        }
        itemImgRepository.flush();
    }


}
