package com.jpa.market.controller;

import com.jpa.market.dto.ItemSearchDto;
import com.jpa.market.dto.MainItemDto;
import com.jpa.market.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/main")
@RequiredArgsConstructor
public class MainController {

    private final ItemService itemService;

    @GetMapping(value = {"","/{page}"})
    public ResponseEntity<Map<String, Object>> getMainPage(ItemSearchDto itemSearchDto,
                                                         @PathVariable("page") Optional<Integer> page){
        Pageable pageable = PageRequest.of(page.orElse(0), 6);
        Page<MainItemDto> items = itemService.getMainItemPage(itemSearchDto, pageable);

        Map<String, Object> response = new HashMap<>();

        response.put("items", items);
        response.put("maxPage", 10);
        response.put("isEventActive", true);


        return ResponseEntity.ok(response);

    }

}
