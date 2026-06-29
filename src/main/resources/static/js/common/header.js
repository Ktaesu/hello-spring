let debounceTimer;

        function onSearchInput(keyword) {
            clearTimeout(debounceTimer);
            const resultBox = document.getElementById('searchResult');

            if (keyword.trim().length < 2) {
                resultBox.style.display = 'none';
                return;
            }

            debounceTimer = setTimeout(() => {
                console.log('fetch 요청 시작:', keyword);  // ✅ 추가
                fetch('/product/api/search?keyword=' + encodeURIComponent(keyword))
                    .then(res => res.json())
                    .then(data => {
                        console.log('응답 데이터:', data);  // ✅ 추가
                        if (data.success) {
                            renderResults(data.data);   // ✅ data.data로 꺼내기
                        } else {
                            document.getElementById('searchResult').style.display = 'none';
                        }
                    })
                    .catch((err) => {
                        console.log('fetch 에러:', err);  // ✅ 추가
                        document.getElementById('searchResult').style.display = 'none';
                    });
            }, 300); // 300ms 디바운스
        }

        function renderResults(items) {
            const resultBox = document.getElementById('searchResult');

            if (!items || items.length === 0) {
                resultBox.innerHTML = '<div style="padding:12px 16px;color:#999;font-size:14px;">검색 결과가 없습니다.</div>';
                resultBox.style.display = 'block';
                return;
            }

            resultBox.innerHTML = items.map(item => `
                <div onclick="location.href='/product/detail/${escapeHtml(item.mt20id)}'"
                <div style="display:flex; gap:12px; padding:10px 16px; cursor:pointer;
                            border-bottom:1px solid #f0f0f0; align-items:center;"
                    onmouseover="this.style.background='#f5f5f5'"
                    onmouseout="this.style.background='white'">

                    <!-- 포스터 (왼쪽) -->
                   <img src="${escapeHtml(item.imgPath || '')}"
                        onerror="this.style.visibility='hidden'"
                        style="width:40px; height:56px; object-fit:cover;
                                border-radius:3px; flex-shrink:0;">

                    <!-- 텍스트 정보 -->
                    <div style="display:flex; flex-direction:column; gap:4px;">
                        <div style="font-size:14px; font-weight:500; color:#111;">
                            ${escapeHtml(item.title || '')}
                        </div>
                        <div style="font-size:12px; color:#888;">
                            ${escapeHtml(item.area || '')} · ${escapeHtml(item.genre || '')}
                        </div>
                        <div style="font-size:12px; color:#aaa;">
                            ${escapeHtml(item.hallName || '')}
                        </div>
                    </div>
                </div>
            `).join('');

            resultBox.style.display = 'block';
        }

        function escapeHtml(str) {
            return str.replace(/[&<>"']/g, m => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[m]));
        }

        // 검색창 바깥 클릭 시 닫기
        document.addEventListener('click', function(e) {
            if (!document.getElementById('searchForm').contains(e.target)) {
                document.getElementById('searchResult').style.display = 'none';
            }
        });