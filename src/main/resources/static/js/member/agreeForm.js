// agreeForm.js
document.addEventListener('DOMContentLoaded', function() {
    const allCheck = document.getElementById('all-check');
    const agrees = document.getElementsByName('agreeCheck');
    const submitBtn = document.getElementById('agree-enter');

    // 1. 전체 체크박스 로직
    allCheck.addEventListener('change', function() {
        agrees.forEach(cb => cb.checked = allCheck.checked);
        toggleSubmitBtn();
    });

    // 2. 개별 체크박스 로직
    agrees.forEach(cb => {
        cb.addEventListener('change', function() {
            const isAllChecked = Array.from(agrees).every(c => c.checked);
            allCheck.checked = isAllChecked;
            toggleSubmitBtn();
        });
    });

    // 3. 버튼 활성화 제어
    function toggleSubmitBtn() {
        submitBtn.disabled = !allCheck.checked;
    }
});