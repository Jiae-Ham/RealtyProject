package com.Realty.RealtyWeb.controller;

import com.Realty.RealtyWeb.dto.*;
import com.Realty.RealtyWeb.enums.Purpose;
import com.Realty.RealtyWeb.enums.TransactionType;
import com.Realty.RealtyWeb.services.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/house-board")
public class HouseBoardController {

    private final HouseBoardService houseBoardService;
    private final ImageService imagesService;
    private final CodefRegisterService codefRegisterService;
    private final HouseInfoService houseInfoService;
    private final RegisterAnalysisService registerAnalysisService;

    @PostMapping("/{pid}/analyze")
    public ResponseEntity<?> analyzeRegister(
            @PathVariable Long pid,
            @RequestBody CodefRequestDTO dto,
            @AuthenticationPrincipal UserDetails user
    ) {
        try {
            // ① 주소 조회
            HouseInfoDTO infoDTO = houseInfoService.findAddressByPid(pid);

            // ② Codef 1차 요청
            JsonNode response = codefRegisterService.requestRegisterFirst(dto, infoDTO.getAddress() + " " + infoDTO.getAddressDetail());

            // ③ 2차 인증이 필요한 경우
            if (codefRegisterService.isTwoWayRequired(response)) {
                JsonNode addrList = response.path("data").path("extraInfo").path("resAddrList");

                // ✅ 무조건 첫 번째 주소 사용
                if (!addrList.isArray() || addrList.size() == 0) {
                    throw new IllegalArgumentException("유효하지 않은 주소입니다.");
                }

                String commUniqueNo = addrList.get(0).path("commUniqueNo").asText();

                // ④ Unique 요청 DTO 구성
                UniqueNoRequestDTO uniqueDto = UniqueNoRequestDTO.builder()
                        .phoneNo(dto.getPhoneNo())
                        .password(dto.getPassword())
                        .uniqueNo(commUniqueNo)
                        .inquiryType("0")     // 보통 "0"
                        .issueType("1")         // 보통 "1"
                        .ePrepayNo(dto.getEPrepayNo())
                        .ePrepayPass(dto.getEPrepayPass())
                        .recordStatus("0")
                        .jointMortgageJeonseYN("0")
                        .tradingYN("0")
                        .electronicClosedYN("0")
                        .selectAddress("0")                     // 자동 선택 핵심
                        .isIdentityViewYN("0")
                        .originDataYN("1")
                        .warningSkipYN("0")
                        .build();

                // ⑤ 2차 요청 → 최종 분석
                JsonNode uniqueResponse = codefRegisterService.requestByUnique(uniqueDto);
                Long id = codefRegisterService.parseFinalResult(uniqueResponse, pid, user.getUsername(), infoDTO);
                RegisterAnalysisDTO analysisDTO = registerAnalysisService.getByIdAndUser(id, user.getUsername());

                return ResponseEntity.ok(analysisDTO);
            }

            // ⑥ 2차 인증 불필요 → 바로 분석
            Long id = codefRegisterService.parseFinalResult(response, pid, user.getUsername(), infoDTO);
            RegisterAnalysisDTO analysisDTO = registerAnalysisService.getByIdAndUser(id, user.getUsername());

            return ResponseEntity.ok(analysisDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 매물 게시글 등록 
    /*
      @AuthenticationPrincipal UserDetails userDetails을 이용해서 하면
      유저 디테일에서 현재 로그인한 사용자의 정보를 가져올 수 있음. 
     */
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<HouseResisterRequestDTO> createHouseBoard(
            @RequestPart("data") HouseResisterRequestDTO requestDTO,
            @RequestParam(value = "pimg", required = false) MultipartFile pimg,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (requestDTO.getHouseBoardDTO() == null || requestDTO.getHouseInfoDTO() == null) {
            return ResponseEntity.badRequest().body(null);
        }

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        // 이미지 저장 및 세팅
        String imageUrl = (pimg != null && !pimg.isEmpty()) ? imagesService.save(pimg) : null;
        requestDTO.getHouseBoardDTO().setPimg(imageUrl);

        HouseResisterRequestDTO responseDTO = houseBoardService.createHouseBoard(
                userDetails.getUsername(),
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
