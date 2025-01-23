package com.example.memo.controller;

import com.example.memo.dto.MemoRequestDto;
import com.example.memo.dto.MemoResponseDto;
import com.example.memo.entity.Memo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/memos")
public class MemoController {


    private final Map<Long, Memo> memoList =new HashMap<>();

    public MemoResponseDto createMemo(@RequestBody MemoRequestDto dto) {

        //식별자가 1씩 증가하도록 만듦
        Long memoId = memoList.isEmpty() ? 1 : Collections.max(memoList.keySet()) + 1;


        //요청받은 데이터로 Memo 객체 생성
        Memo memo = new Memo(memoId, dto.getTitle(),dto.getContents());


        //Inmemory DB에 Memo 메모
        memoList.put(memoId, memo);

        return new MemoResponseDto(memo);
    }


    //메모 단 건 조회기능
    //- 메모 하나를 조회할 수 있다. (READ)
    //    - 조회할 memo에 대한 식별자 id값이 필요하다.
    //    - 조회된 데이터가 응답된다.
    //        - 응답 상태코드는 200 OK로 설정한다.
    //    - **조회될 데이터가 없는 경우 Exception이 발생**한다.
    //        - 응답 상태코드는 404 NOT FOUND로 설정한다.

    @GetMapping("/{id}")
    public ResponseEntity<MemoResponseDto> findMemoById(@PathVariable Long id) {

        Memo memo = memoList.get(id);

        if (memo == null) {

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        }

        return new ResponseEntity<MemoResponseDto>(new MemoResponseDto(memo),HttpStatus.OK);

    }

//    - 메모 하나를 전체 수정(덮어쓰기)할 수 있다. (UPDATE)
//            - 수정할 memo에 대한 식별자 id값이 필요하다.
//            - 수정할 요청 데이터(제목, 내용)가 꼭 필요하다.
//            - 수정된 데이터가 응답된다.
//        - 응답 상태코드는 200 OK로 설정한다.
//            - 수정될 데이터가 없는 경우 Exception이 발생한다.
//            - 응답 상태코드는 404 NOT FOUND 로 설정한다.

    @PutMapping("/{id}")
    public ResponseEntity<MemoResponseDto> updateMemoById(
        @PathVariable Long id,
        @RequestBody MemoRequestDto dto
    ) {
        Memo memo = memoList.get(id);


        //NPE 방지
        if (memo == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        //필수값 검증
        if (dto.getTitle() == null || dto.getContents() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        memo.update(dto);

        return new ResponseEntity<>(new MemoResponseDto(memo), HttpStatus.OK);
        //HTTP STATus
    }


    //메모 목졸 조회기능
    //    - 메모 전체 목록을 조회할 수 있다. (READ)
    //    - 여러개의 데이터를 배열 형태로 한번에 응답한다.
    //    - 데이터가 없는 경우 비어있는 배열 형태로 응답한다.
    //    - 응답 상태코드는 200 OK로 설정한다.
    @GetMapping
    public List<MemoResponseDto> findAllMemos() {
        //
        //init List
        List<MemoResponseDto> responseList = new ArrayList<>();

        // HashMap<Memo> -> List<MemoResponseDto>
        for (Memo memo : memoList.values()) {
            MemoResponseDto responseDto = new MemoResponseDto(memo);
            responseList.add(responseDto);
        }
        //Map to List
//        responseList = memoList.values().stream().map(MemoResponseDto::new).toList();
        return responseList;
    }

//    - 메모 하나의 제목을 수정(일부 수정)할 수 있다. (UPDATE)
//            - 수정할 memo에 대한 식별자 id값이 필요하다.
//            - 수정할 요청 데이터(제목)이 **꼭 필요하다.**
//            - 응답 상태코드는 400 BAD REQUEST로 설정한다.
//    - 수정된 데이터가 응답된다.
//        - 응답 상태코드는 200 OK로 설정한다.
//            - 수정될 데이터가 없는 경우 Exception이 발생한다.
//            - 응답 상태코드는 404 NOT FOUND 로 설정한다.

    @PatchMapping("/{id}")
    public ResponseEntity<MemoResponseDto> updateTitle(
            @PathVariable Long id,
            @RequestBody MemoRequestDto dto
    ){
        Memo memo = memoList.get(id);

        // NPE 방지
        if (memo == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (dto.getTitle() == null || dto.getContents() != null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        memo.updateTitle(dto);

        return new ResponseEntity<>(new MemoResponseDto(memo),HttpStatus.OK);
    }

    //- 메모를 삭제할 수 있다. (DELETE)
    //    - 삭제할 memo에 대한 식별자 id값이 필요하다.
    //    - 삭제될 데이터가 없는 경우 Exception이 발생한다.
    //        - 응답 상태코드는 404 NOT FOUND로 설정한다.
    //    - 응답 데이터는 없어도 무방하다.
    //        - 응답 상태코드는 200 OK로 설정한다.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMemo(@PathVariable Long id) {

        //memoList의 Key값에 id를 포함하고 있다면
        if (memoList.containsKey(id)) {
            memoList.remove(id);

            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
