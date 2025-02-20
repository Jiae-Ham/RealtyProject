package com.Realty.RealtyWeb.controller;

import com.Realty.RealtyWeb.dto.HouseBoardDTO;
import com.Realty.RealtyWeb.dto.HouseInfoDTO;
import com.Realty.RealtyWeb.dto.HouseResisterRequestDTO;
import com.Realty.RealtyWeb.services.HouseBoardService;
import lombok.RequiredArgsConstructor;
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

    // 전체 매물 게시글 조회
    @GetMapping("/all")
    public ResponseEntity<List<HouseResisterRequestDTO>> getAllHouseBoards() {
        return ResponseEntity.ok(houseBoardService.getAllHouseBoards());
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
