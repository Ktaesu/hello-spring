document.addEventListener("DOMContentLoaded", () => {

    const logoutBtn =
        document.getElementById("logoutBtn");

    if (logoutBtn) {

        logoutBtn.addEventListener("click", async () => {

            const confirmed =
                confirm("로그아웃 하시겠습니까?");

            if (!confirmed) {
                return;
            }

            try {

                const response =
                    await fetch("/api/member/logout", {
                        method: "POST"
                    });

                if (response.ok) {

                    location.href = "/";

                } else {

                    alert("로그아웃에 실패했습니다.");

                }

            } catch (error) {

                console.error(error);

                alert("로그아웃 처리 중 오류가 발생했습니다.");

            }

        });

    }

});