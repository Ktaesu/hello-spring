$(document).ready(function() {
    // 선택 상태 관리 객체
    let currentFilters = {
        area: "",
        rank: "daily"
    };

    // --- 모달 열기 ---
    $('#areaModalBtn').click(() => $('#areaModal').css('display', 'flex'));
    $('#rankModalBtn').click(() => $('#rankModal').css('display', 'flex'));

    // --- 모달 닫기 ---
    $('.close-btn, .modal').click(function(e) {
        if ($(e.target).hasClass('modal') || $(e.target).hasClass('close-btn')) {
            $('.modal').hide();
        }
    });

    // --- 지역 선택 처리 ---
    $('.area-item').click(function() {
        $('.area-item').removeClass('active');
        $(this).addClass('active');
        
        currentFilters.area = $(this).data('code');
        $('#selectedAreaName').text($(this).text());
        $('#areaModal').hide();
        
        sendFilterRequest();
    });

    // --- 랭킹 선택 처리 ---
    $('.rank-item').click(function() {
        $('.rank-item').removeClass('active');
        $(this).addClass('active');
        
        currentFilters.rank = $(this).data('value');
        $('#selectedRankName').text($(this).text());
        $('#rankModal').hide();
        
        sendFilterRequest();
    });

    // --- 통합 AJAX 요청 ---
    function sendFilterRequest() {
        console.log("요청 데이터:", currentFilters); // 선택된 값이 콘솔에 찍히는지 확인용

        $.ajax({
            url: '/product/api/filter-art',
            type: 'GET',
            data: currentFilters,
            beforeSend: function() {
                // 로딩 표시가 필요하면 여기에 추가
                $('#performanceList').css('opacity', '0.5');
            },
            success: function(fragment) {
                $('#performanceList').html(fragment).css('opacity', '1');
            },
            error: function(err) {
                alert("데이터를 불러오는 중 오류가 발생했습니다.");
                $('#performanceList').css('opacity', '1');
            }
        });
    }
});