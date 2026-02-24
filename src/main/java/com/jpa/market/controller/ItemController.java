package com.jpa.market.controller;

import com.jpa.market.dto.ItemAdminListDto;
import com.jpa.market.dto.ItemFormDto;
import com.jpa.market.dto.ItemSearchDto;
import com.jpa.market.repository.ItemRepository;
import com.jpa.market.service.ItemService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ItemController {

    private final ItemService itemService;

    // 상품 등록
    // @RequestPart : 파일과 json을 같이 보내는 요청을 처리할 때 사용
    @PostMapping("/admin/items")
    public ResponseEntity itemNew(@Valid @RequestPart("itemCreateDto") ItemFormDto itemFormDto,
                                  @RequestPart("itemImgFile") List<MultipartFile> itemImgFileList) throws Exception{


            Long itemId = itemService.saveItem(itemFormDto, itemImgFileList);
            return ResponseEntity.ok(itemId);

    }

    @GetMapping("/items/{itemId}")
    public ResponseEntity<?> getItemDetail(@PathVariable("itemId") Long itemId) {

        ItemFormDto itemFormDto = itemService.getItemDetail(itemId);
        return ResponseEntity.ok(itemFormDto);

    }

    @PostMapping("/admin/items/{itemId}")
    public ResponseEntity<?> itemUpdate(@PathVariable("itemId") Long itemId,
                                        @Valid @RequestPart("itemUpdateDto") ItemFormDto itemFormDto,
                                        @RequestPart("itemImgFile") List<MultipartFile> itemImgFileList) throws Exception{

        // 필수 아님.
        // 프론트에서 넘어오는 dto에 id가 포함되어 있지 않을수도 잇을때
        // url에 등록된 itemId를 저장하도록 함
        itemFormDto.setId(itemId);

        itemService.updateItem(itemFormDto, itemImgFileList);

        return ResponseEntity.ok(itemFormDto.getId());
    }

    // 상품 관리 페이지
    @GetMapping(value = {"/admin/items", "/admin/items/{page}"})
    public ResponseEntity<Page<ItemAdminListDto>> itemManage(ItemSearchDto itemSearchDto,
                                                             @PathVariable("page")Optional<Integer> page){

        Pageable pageable = PageRequest.of(page.orElse(0), 5);

        Page<ItemAdminListDto> items = itemService.getAdminItemPage(itemSearchDto, pageable);

        return ResponseEntity.ok(items);

    }

}
