package com.ts.demo.hello_spring.member.controller;

import com.ts.demo.hello_spring.member.dto.LoginRequestDTO;
import com.ts.demo.hello_spring.member.dto.LoginResponseDTO;
import com.ts.demo.hello_spring.member.dto.MemberDTO;
import com.ts.demo.hello_spring.member.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member") // 추가: 공통 경로 설정
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/login")
    public String loginForm(HttpServletRequest request, HttpSession session) {

        // 1. 직전 페이지 주소 가져오기
        String referer = request.getHeader("Referer");

        // 2. 주소가 있고, 로그인 페이지 자체가 아니라면 세션에 'prevPage'로 저장
        if (referer != null && !referer.contains("/loginForm")) {
            session.setAttribute("prevPage", referer);
        }
        return "member/login";
    }

    //로그인
    @PostMapping("/login")
    public String login(@ModelAttribute LoginRequestDTO dto, HttpServletResponse response, HttpSession session,
                        Model model, RedirectAttributes ra){

        // 서비스에서 이제 DTO가 아닌 토큰(String)을 받아옵니다.
        String token = memberService.login(dto);

        if(token == null){
            ra.addFlashAttribute("msg", "아이디 또는 비밀번호가 일치하지 않습니다.");
            return "redirect:/loginForm";
        }

        // --- JWT 쿠키 생성 ---
        Cookie cookie = new Cookie("jwt_token", token);
        cookie.setHttpOnly(true);   // 자바스크립트 접근 방지 (보안)
        cookie.setPath("/");        // 모든 경로에서 쿠키 전송
        cookie.setMaxAge(60 * 30);  // 30분 유지
        response.addCookie(cookie); // 브라우저로 전송
        // -----------------------

        // 3. 세션에 저장된 이전 페이지 주소 꺼내기
        String prevPage = (String) session.getAttribute("prevPage");

        if (prevPage != null) {
            session.removeAttribute("prevPage"); // 4. 사용 후 세션에서 삭제 (중요!)
            return "redirect:" + prevPage;      // 보던 페이지로 이동
        }

        return "redirect:/";
    }

    //로그아웃
    @GetMapping("logout")
    public String logout(HttpServletResponse response){
        // 세션을 무효화하는 대신, 같은 이름의 쿠키를 수명을 0으로 해서 보냅니다.
        Cookie cookie = new Cookie("jwt_token", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return "redirect:/";
    }

    //회원가입폼
    @GetMapping("/createMember")
    public String createMember(){
        return "member/createMember";
    }

    //아이디 중복체크
    @GetMapping("/idCheck")
    @ResponseBody
    public boolean idCheck(@RequestParam("memberId") String memberId) {
        // 서비스에게 물어보고 true/false를 브라우저로 바로 리턴
        return memberService.checkIdDuplicate(memberId);
    }

    //닉네임 중복체크
    @GetMapping("/nicknameCheck")
    @ResponseBody
    public boolean nicknameCheck(@RequestParam("nickname") String nickname) {
        return memberService.checkNicknameDuplicate(nickname);
    }

    //회원가입
    @PostMapping("/createMember")
    public String join(@ModelAttribute MemberDTO.JoinRequest joinDto, RedirectAttributes rttr) {
        // 1. 서비스에 데이터 전달 및 가입 처리 요청
        boolean isSuccess = memberService.joinMember(joinDto);

        if(isSuccess) {
            // 2. 가입 성공 시 로그인 페이지로 리다이렉트 (메시지 포함)
            rttr.addFlashAttribute("msg", "회원가입이 완료되었습니다!");
            return "redirect:/member/login";
        } else {
            // 3. 실패 시 다시 가입 페이지로
            rttr.addFlashAttribute("msg", "가입에 실패했습니다.");
            return "redirect:/member/createMember";
        }
    }


}
