document.addEventListener('DOMContentLoaded', () => {
    const joinForm = document.getElementById('joinForm');
    const password = document.getElementById('memberPwd');
    const passwordConfirm = document.getElementById('passwordConfirm');

    // 1. 비밀번호 복잡도 검사 (정규식)
    const validatePassword = (pw) => {
        const regex = /^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+|~={}\[\]:;<>?,./]).{8,16}$/;
        return regex.test(pw);
    };

    // 2. 비밀번호 패턴 체크 (비밀번호 칸 밑에 표시)
    const checkPasswordPattern = () => {
        const pwMsg = document.getElementById('pwMsg');
        if (!validatePassword(password.value)) {
            pwMsg.innerText = "8~16자 영문, 숫자, 특수문자를 조합해주세요.";
            pwMsg.className = "msg error";
            return false;
        } else {
            pwMsg.innerText = "사용 가능한 비밀번호입니다.";
            pwMsg.className = "msg success";
            return true;
        }
    };

    // 3. 비밀번호 일치 체크 (비밀번호 확인 칸 밑에 표시)
    const checkPasswordMatch = () => {
        const pwConfirmMsg = document.getElementById('pwConfirmMsg');
        if (passwordConfirm.value === "") {
            pwConfirmMsg.innerText = "";
            return false;
        }
        if (password.value === passwordConfirm.value) {
            pwConfirmMsg.innerText = "비밀번호가 일치합니다.";
            pwConfirmMsg.className = "msg success";
            return true;
        } else {
            pwConfirmMsg.innerText = "비밀번호가 일치하지 않습니다.";
            pwConfirmMsg.className = "msg error";
            return false;
        }
    };

    // 실시간 감지 이벤트
    password.addEventListener('focus', checkPasswordPattern);
    password.addEventListener('keyup', () => {
        checkPasswordPattern();
        checkPasswordMatch();
    });
    passwordConfirm.addEventListener('focus', checkPasswordMatch);
    passwordConfirm.addEventListener('keyup', checkPasswordMatch);

    // 4. 가입하기 버튼 클릭 시 (최종 유효성 검사)
    joinForm.addEventListener('submit', (e) => {
        let isValid = true;

        // 필수 입력 필드(required)들만 모두 가져오기
        const requiredFields = joinForm.querySelectorAll('input[required]');

        requiredFields.forEach(field => {
            // 핵심: input의 가장 가까운 부모인 .form-group을 찾고, 그 안의 .msg를 찾음
            const group = field.closest('.form-group');
            const errorSpan = group.querySelector('.msg');
            const labelName = group.querySelector('label').innerText;

            if (!field.value.trim()) {
                errorSpan.innerText = `${labelName}: 필수 정보입니다.`;
                errorSpan.className = "msg error";
                isValid = false;
            } else {
                // 값이 입력되었다면 에러 메시지 삭제 (비밀번호 관련은 별도 로직이 있으므로 제외 가능)
                if (field.id !== 'password' && field.id !== 'passwordConfirm') {
                    errorSpan.innerText = "";
                    errorSpan.className = "msg";
                }
            }
        });

        // 비밀번호 최종 체크 결과 반영
        const isPatternOk = checkPasswordPattern();
        const isMatchOk = checkPasswordMatch();

        if (!isValid || !isPatternOk || !isMatchOk) {
            e.preventDefault(); // 서버 전송 막기
            // 첫 번째 에러가 난 곳으로 스크롤 이동하면 더 친절한 폼이 됩니다.
            const firstError = document.querySelector('.msg.error');
            if (firstError) firstError.previousElementSibling.focus();
        }
    });
});

// 아이디 중복 확인 로직
const btnIdCheck = document.getElementById('btnIdCheck');
const idInput = document.getElementById('memberId');
const idCheckMsg = document.getElementById('idCheckMsg');

// 중복 확인 여부 상태 저장 (나중에 가입하기 누를 때 체크용)
let isIdChecked = false;

btnIdCheck.addEventListener('click', () => {
    const memberId = idInput.value.trim();

    if (!memberId) {
        idCheckMsg.innerText = "아이디를 먼저 입력해주세요.";
        idCheckMsg.className = "msg error";
        idInput.focus();
        return;
    }

    // 서버로 데이터 전송 (Ajax)
    fetch(`/member/idCheck?memberId=${memberId}`, {
        method: 'GET'
    })
    .then(response => response.json()) // 서버 응답을 JSON으로 받음
    .then(isDuplicate => {
        if (isDuplicate) {
            idCheckMsg.innerText = "이미 사용 중인 아이디입니다.";
            idCheckMsg.className = "msg error";
            isIdChecked = false;
        } else {
            idCheckMsg.innerText = "사용 가능한 아이디입니다.";
            idCheckMsg.className = "msg success";
            isIdChecked = true; // 중복 확인 완료!
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('중복 확인 중 오류가 발생했습니다.');
    });
});

// 아이디를 수정하면 다시 중복 확인을 해야 함
idInput.addEventListener('change', () => {
    isIdChecked = false;
    idCheckMsg.innerText = "";
});


// 닉네임 중복 확인 로직
const btnNicknameCheck = document.getElementById('btnNicknameCheck');
const nicknameInput = document.getElementById('nickname');
const nicknameCheckMsg = document.getElementById('nicknameCheckMsg');

let isNicknameChecked = false; // 닉네임 체크 여부

btnNicknameCheck.addEventListener('click', () => {
    const nickname = nicknameInput.value.trim();

    if (!nickname) {
        nicknameCheckMsg.innerText = "닉네임을 입력해주세요.";
        nicknameCheckMsg.className = "msg error";
        return;
    }

    fetch(`/member/nicknameCheck?nickname=${nickname}`)
    .then(response => response.json())
    .then(isDuplicate => {
        if (isDuplicate) {
            nicknameCheckMsg.innerText = "이미 사용 중인 닉네임입니다.";
            nicknameCheckMsg.className = "msg error";
            isNicknameChecked = false;
        } else {
            nicknameCheckMsg.innerText = "사용 가능한 닉네임입니다.";
            nicknameCheckMsg.className = "msg success";
            isNicknameChecked = true;
        }
    })
    .catch(error => console.error('Error:', error));
});

// 닉네임 수정 시 초기화
nicknameInput.addEventListener('change', () => {
    isNicknameChecked = false;
    nicknameCheckMsg.innerText = "";
});


/**
 * 회원가입 인증 로직 전담 스크립트
 */

// 모든 인증이 완료되었는지 확인하기 위한 상태 변수
let isPhoneVerified = false;
let isEmailVerified = false;

/* ==========================================
   1. 휴대폰 인증 (SMS)
   ========================================== */
// 인증번호 전송
document.getElementById('btnPhoneAuth').addEventListener('click', () => {
    const phone = document.getElementById('phone').value;
    if(!phone) {
        alert("전화번호를 입력해주세요.");
        return;
    }

    fetch(`/api/auth/sms/send?phone=${phone}`, { method: 'POST' })
        .then(res => res.json())
            .then(data => {
                console.log("서버 응답:", data); // 브라우저 콘솔에서 확인용
                if (data.success) {
                    alert(data.data); // "문자가 발송되었습니다."
                    const wrapper = document.getElementById('authCodeWrapper');
                    wrapper.style.display = 'flex';
                } else {
                    alert(data.message); // "일일 인증 횟수를 초과했습니다. (최대 5회)"
                }
        });
});

// 인증번호 확인
document.getElementById('btnVerifyCode').addEventListener('click', () => {
    const phone = document.getElementById('phone').value;
    const code = document.getElementById('authCode').value;
    const msg = document.getElementById('phoneMsg');

    fetch(`/api/auth/sms/verify?phone=${phone}&code=${code}`, { method: 'POST' })
        .then(res => res.json())
        .then(result => {
            if(result.success) {
                alert("휴대폰 인증 성공!");
                isPhoneVerified = true;
                document.getElementById('phone').readOnly = true; // 수정 불가
                document.getElementById('authCodeWrapper').style.display = 'none'; // 입력창 숨김
                msg.innerText = "휴대폰 인증 완료";
                msg.className = "msg success";
            } else {
                alert("인증번호가 일치하지 않습니다.");
                msg.innerText = "인증 실패";
                msg.className = "msg error";
            }
        });
});

/* ==========================================
   2. 이메일 인증
   ========================================== */
// 인증번호 전송
document.getElementById('btnEmailAuth').addEventListener('click', () => {
    const email = document.getElementById('email').value;
    if(!email) {
        alert("이메일을 입력해주세요.");
        return;
    }

    fetch(`/api/auth/email/send?email=${email}`, { method: 'POST' })
        .then(res => res.json())
        .then(data => {
            console.log("서버 응답:", data); // 브라우저 콘솔에서 확인용
            if(data.success) {
                alert(data.data);
                document.getElementById('emailAuthWrapper').style.display = 'flex';
            } else {
                    alert(data.message); // "일일 인증 횟수를 초과했습니다.
                }
        });
});

// 인증번호 확인
document.getElementById('btnVerifyEmail').addEventListener('click', () => {
    const code = document.getElementById('emailAuthCode').value;
    const email = document.getElementById('email').value;
    const msg = document.getElementById('emailMsg');

    fetch(`/api/auth/email/verify?email=${email}&code=${code}`, { method: 'POST' })
        .then(res => res.json())
        .then(isValid => {
            if(isValid) {
                alert("이메일 인증 성공!");
                isEmailVerified = true;
                document.getElementById('email').readOnly = true; // 수정 불가
                document.getElementById('emailAuthWrapper').style.display = 'none'; // 입력창 숨김
                msg.innerText = "이메일 인증 완료";
                msg.className = "msg success";
            } else {
                alert("인증번호가 일치하지 않습니다.");
                msg.innerText = "인증 실패";
                msg.className = "msg error";
            }
        });
});



/* ==========================================
   4. 주소 검색 (카카오 API)
   ========================================== */
document.getElementById('btnAddress').addEventListener('click', () => {
    new daum.Postcode({
        oncomplete: function(data) {
            // 팝업에서 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분입니다.

            // 각 주소의 노출 규칙에 따라 주소를 조합한다.
            // 내려오는 변수가 값이 없는 경우엔 공백('')값을 가짐
            let addr = ''; // 주소 변수
            let extraAddr = ''; // 참고항목 변수

            //사용자가 선택한 주소 타입에 따라 해당 주소 값을 가져온다.
            if (data.userSelectedType === 'R') { // 사용자가 도로명 주소를 선택했을 경우
                addr = data.roadAddress;
            } else { // 사용자가 지번 주소를 선택했을 경우(J)
                addr = data.jibunAddress;
            }

            // 1. 우편번호(zonecode)를 해당 필드에 넣는다.
            document.getElementById("zipcode").value = data.zonecode;

            // 주소 정보를 해당 필드에 넣는다.
            document.getElementById("address").value = addr;
            
            // 커서를 상세주소 필드로 이동하여 바로 입력할 수 있게 한다.
            document.getElementById("addressDetail").focus();
        }
    }).open();
});

// 최종 가입하기 버튼 클릭 시 이벤트
document.getElementById('joinForm').addEventListener('submit', function(e) {
    // 1. 필수 입력값 및 인증 상태 체크
    if(!isIdChecked) {
        alert("아이디 중복 확인을 완료해 주세요.");
        e.preventDefault(); return;
    }
    if(!isNicknameChecked) {
        alert("닉네임 중복 확인을 완료해 주세요.");
        e.preventDefault(); return;
    }
    if(!isEmailVerified) {
        alert("이메일 인증을 완료해 주세요.");
        e.preventDefault(); return;
    }
    if(!isPhoneVerified) {
        alert("휴대폰 인증을 완료해 주세요.");
        e.preventDefault(); return;
    }

    // 2. 주소 입력 확인
    if(document.getElementById('address').value === "") {
        alert("주소를 검색해 주세요.");
        e.preventDefault(); return;
    }

    // 모든 검증을 통과하면 폼이 제출됩니다.
    // 제출된 후에는 아래 3번(Ajax 처리 시) 혹은 서버에서 리다이렉트 처리를 합니다.
});