/*<![CDATA[*/

        // ── 탭 스크롤 ──
        function navScroll(id) {
            const el = document.getElementById(id);
            if (!el) return;
            const offset = document.querySelector('.sticky-nav').offsetHeight + 16;
            window.scrollTo({ top: el.getBoundingClientRect().top + window.scrollY - offset, behavior: 'smooth' });
        }

        // 탭 active 연동
        const sectionIds = ['section-hero','section-cast','section-synopsis','section-price','section-map','section-review'];
        const navTabs    = document.querySelectorAll('.nav-tab');

        window.addEventListener('scroll', () => {
            let cur = sectionIds[0];
            sectionIds.forEach(id => {
                const el = document.getElementById(id);
                if (el && el.getBoundingClientRect().top < 80) cur = id;
            });
            navTabs.forEach((t, i) => t.classList.toggle('active', sectionIds[i] === cur));
        });

        // ── 카카오맵 ──
        console.log("주소: ", mapAddr);

        kakao.maps.load(() => {
            const geocoder = new kakao.maps.services.Geocoder();

            geocoder.addressSearch(mapAddr, (result, status) => {
                if (status === kakao.maps.services.Status.OK) {
                    const coords = new kakao.maps.LatLng(result[0].y, result[0].x);
                    const map = new kakao.maps.Map(document.getElementById('map'), {
                        center: coords,
                        level: 4
                    });
                    new kakao.maps.Marker({ map, position: coords });
                } else {
                    document.getElementById('map').innerHTML =
                        '<div style="display:flex;align-items:center;justify-content:center;height:100%;color:#888;">지도를 불러올 수 없습니다.</div>';
                }
            });
        });

        // ── 달력 ──
        const prfFrom   = /*[[${performance.prfpdfrom}]]*/ '2026.04.01';
        const prfTo     = /*[[${performance.prfpdto}]]*/ '2026.12.31';
        const parseDS   = s => { const [y,m,d] = s.split('.').map(Number); return new Date(y,m-1,d); };
        const startDate = parseDS(prfFrom);
        const endDate   = parseDS(prfTo);

        let curYear  = new Date().getFullYear();
        let curMonth = new Date().getMonth();
        let selDate  = null;

        // 샘플 좌석 데이터 (실제: 날짜 선택 시 API 호출로 교체)
        const sampleSeats = [
            { type:'VIP석',  remain: 12, price:'170,000원', cls:'remain-good' },
            { type:'R석',    remain:  3, price:'130,000원', cls:'remain-few'  },
            { type:'S석',    remain:  0, price:'100,000원', cls:'remain-sold' },
        ];

        function renderCal() {
            const grid  = document.getElementById('calGrid');
            const today = new Date();
            document.getElementById('calMonth').textContent = `${curYear}년 ${curMonth+1}월`;
            grid.innerHTML = '';

            const firstDay = new Date(curYear, curMonth, 1).getDay();
            const lastDay  = new Date(curYear, curMonth+1, 0).getDate();

            for (let i = 0; i < firstDay; i++) {
                const el = document.createElement('div');
                el.className = 'cal-cell';
                grid.appendChild(el);
            }

            for (let d = 1; d <= lastDay; d++) {
                const date = new Date(curYear, curMonth, d);
                const el   = document.createElement('div');
                el.textContent = d;
                el.className   = 'cal-cell';

                const inRange = date >= startDate && date <= endDate;
                const isToday = date.toDateString() === today.toDateString();
                const isSel   = selDate && date.toDateString() === selDate.toDateString();

                if (inRange) el.classList.add('available');
                if (isToday) el.classList.add('today');
                if (isSel)   el.classList.add('selected');

                if (inRange) el.onclick = () => onDateSelect(date, d);
                grid.appendChild(el);
            }
        }

        function onDateSelect(date, d) {
            selDate = date;
            renderCal();

            const label = `📅 ${date.getFullYear()}.${String(date.getMonth()+1).padStart(2,'0')}.${String(d).padStart(2,'0')}`;
            document.getElementById('selectedDateLabel').textContent = label;
            document.getElementById('seatInfo').classList.add('show');

            // 시간 칩 (실제: API 호출)
            const times = ['14:00', '19:00'];
            document.getElementById('timeChips').innerHTML =
                times.map(t => `<div class="time-chip" onclick="onTimeSelect(this,'${t}')">${t}</div>`).join('');

            // 좌석 현황 (실제: API 호출)
            document.getElementById('seatRows').innerHTML = sampleSeats.map(s => `
                <div class="seat-row">
                    <span class="seat-type">${s.type}</span>
                    <div class="seat-right">
                        <span class="seat-remain ${s.cls}">
                            ${s.remain === 0 ? '매진' : s.remain + '석 남음'}
                        </span>
                        <span class="seat-price">${s.price}</span>
                    </div>
                </div>
            `).join('');

            const btn = document.getElementById('bookBtn');
            btn.disabled = true;
            btn.textContent = '시간을 선택하세요';
        }

        function onTimeSelect(el, time) {
            document.querySelectorAll('.time-chip').forEach(c => c.classList.remove('active'));
            el.classList.add('active');
            const btn = document.getElementById('bookBtn');
            btn.disabled = false;
            btn.textContent = `${time} 예매하기 →`;
        }

        function changeMonth(dir) {
            curMonth += dir;
            if (curMonth > 11) { curMonth = 0; curYear++; }
            if (curMonth < 0)  { curMonth = 11; curYear--; }
            renderCal();
        }

        renderCal();
        /*]]>*/