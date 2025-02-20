package com.Realty.RealtyWeb.controller;

import com.Realty.RealtyWeb.dto.*;
import com.Realty.RealtyWeb.enums.Purpose;
import com.Realty.RealtyWeb.enums.TransactionType;
import com.Realty.RealtyWeb.services.HouseBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/house-board")
public class HouseBoardController {

    private final HouseBoardService houseBoardService;

    // 매물 게시글 등록 
    /*
      @AuthenticationPrincipal UserDetails userDetails을 이용해서 하면
      유저 디테일에서 현재 로그인한 사용자의 정보를 가져올 수 있음. 
     */
    @PostMapping("/create")
    public ResponseEntity<HouseResisterRequestDTO> createHouseBoard(
            @RequestBody HouseResisterRequestDTO requestDTO,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (requestDTO.getHouseBoardDTO() == null || requestDTO.getHouseInfoDTO() == null) {
            return ResponseEntity.badRequest().body(null);
        }

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        HouseResisterRequestDTO responseDTO = houseBoardService.createHouseBoard(
                userDetails.getUsername(), // userId를 반환
                requestDTO.getHouseBoardDTO(),
                requestDTO.getHouseInfoDTO()
        );

        return ResponseEntity.ok(responseDTO);
    }


    // 특정 매물 게시글 조회
    @GetMapping("/{pid}")
    public ResponseEntity<HouseResisterRequestDTO> getHouseBoardById(@PathVariable Long pid) {
        Optional<HouseResisterRequestDTO> houseBoard = houseBoardService.getHouseBoardById(pid);
        return houseBoard.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 특정 회원이 작성한 매물 게시글 조회 -> 아이디말고 닉네임으로 수정할 것
    @GetMapping("/user/{username}")
    public ResponseEntity<List<HouseResisterRequestDTO>> getHouseBoardsByUser(@PathVariable String username) {
        List<HouseResisterRequestDTO> houseBoards = houseBoardService.getHouseBoardsByUsername(username);
        return ResponseEntity.ok(houseBoards);
    }

    //전체 매물 게시글 조회
    @GetMapping("/list")
    public Page<HouseBoardSummaryDTO> getHouseBoards(
            @RequestParam(required = false) String purpose,       // 매물 종류 (Optional)
            @RequestParam(required = false) String transactionType, // 거래 방식 (Optional)
            @RequestParam(required = false) Integer minPrice,    // 최소 가격
            @RequestParam(required = false) Integer maxPrice,    // 최대 가격
            @RequestParam(required = false) Integer minExclusiveArea, // 최소 전용 면적
            @RequestParam(required = false) Integer maxExclusiveArea, // 최대 전용 면적
            @RequestParam(required = false) Integer minFloor,    // 최소 층수
            @RequestParam(required = false) Integer maxFloor,    // 최대 층수
            @RequestParam(required = false) Integer builtYear,   // 사용 승인일
            @RequestParam(required = false) Boolean petAllowed,  // 반려동물 가능 여부
            @RequestParam(required = false) Boolean parkingAvailable, // 주차 가능 여부
            @RequestParam(defaultValue = "0") int page,         // 페이지 번호 (기본값: 0)
            @RequestParam(defaultValue = "10") int size         // 페이지 크기 (기본값: 10)
    ) {
        HouseBoardFilterDTO filter = HouseBoardFilterDTO.builder()
                .purpose(purpose != null ? Purpose.valueOf(purpose) : null)
                .transactionType(transactionType != null ? TransactionType.valueOf(transactionType) : null)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .minExclusiveArea(minExclusiveArea)
                .maxExclusiveArea(maxExclusiveArea)
                .minFloor(minFloor)
                .maxFloor(maxFloor)
                .builtYear(builtYear)
                .petAllowed(petAllowed)
                .parkingAvailable(parkingAvailable)
                .build();

        Pageable pageable = PageRequest.of(page, size);
        return houseBoardService.getAllHouseBoards(filter, pageable);
    }


    // 매물 게시글 수정
    /*
    아래 방법은 좀 더 수동적인 방법. SecurityContextHolder를 이용해서 
    수동으로 현재 로그인한 사용자의 정보를 가져옴. 컨트롤러보다는 서비스에서 사용하면 될 방법
     */
    @PutMapping("/update/{pid}")
    public ResponseEntity<HouseResisterRequestDTO> updateHouseBoard(
            @PathVariable Long pid,
            @RequestBody HouseResisterRequestDTO requestDTO) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName(); // 🔹 JWT에서 userId 가져오기

        HouseResisterRequestDTO updatedBoard = houseBoardService.updateHouseBoard(
                pid,
                userId,
                requestDTO.getHouseBoardDTO(),
                requestDTO.getHouseInfoDTO()
        );

        return ResponseEntity.ok(updatedBoard);
    }


    // 매물 게시글 삭제
    @DeleteMapping("/delete/{pid}")
    public ResponseEntity<String> deleteHouseBoard(@PathVariable Long pid) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName(); // 🔹 JWT에서 userId 가져오기

        boolean deleted = houseBoardService.deleteHouseBoard(pid, userId);
        return deleted ? ResponseEntity.ok("매물 게시글이 삭제되었습니다.") : ResponseEntity.notFound().build();
    }

}
