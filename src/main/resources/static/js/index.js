const articles = [
    {
        title: "비 일단 소강 상태지만...비구름 계속 들어와 내일까지 시간당 30~80㎜ 강한 비",
        content:
            "18일 오전 일부 지역에서 비가 소강 상태를 보이고 있는 가운데, 토요일인 19일까지 전국 대부분 지역에 매우 강하고 많은 비가 내릴 전망이다.\n\n" +
            "18일 기상청에 따르면, 이날 오전 일부 지역에서는 비가 소강 상태를 보였다. 오전 11시 기준 수도권과 강원 영서, 충남 서해안을 중심으로 10㎜ 안팎의 약한 비가 내렸다. 오전 10시부터 오전 11시까지 1시간 동안 주요 지역의 강수량은 경기 남양주 5㎜, 강원 춘천 6㎜, 대전 0.5㎜ 등이다.\n\n" +
            "기상청은 “18일 낮까지 내륙을 중심으로 소강 상태를 보이는 곳이 많겠다”고 밝혔다.\n\n" +
            "그러나 서해안에서 비구름대가 지속적으로 들어오고 있어, 19일까지 전국에 시간당 30~80㎜의 비가 내릴 것으로 보인다. 18~19일 예상 강수량은 수도권·강원도(20일 아침까지) 30~150㎜, 충청·전라·경상권 50~200㎜, 제주도 10~150㎜ 등으로 예보됐다. 시간당 강수량은 대부분 지역에서 30㎜ 안팎이겠으나, 전라권과 경상권의 경우 최고 80㎜까지 내리겠다.\n\n" +
            "출처: https://www.chosun.com/national/transport-environment/2025/07/18/G3DQZX4X4VD3TC63CS4GNMQEKY/",
        img: "https://www.chosun.com/resizer/v2/4SHSUJMWMOVHYYIGFIDATCSGEU.jpg?auth=9fc7cfda94d2099c9515715cd46adce01955ddfcc372d4b03888978d9503dcb7&width=616"
    },
    {
        title: "2025 최신 스마트폰 비교",
        content: "삼성, 애플의 신제품들을 전격 비교해보았습니다. 카메라 성능, 배터리 수명, 무게까지 알아봅니다.",
        img: "https://picsum.photos/id/1050/800/400"
    },
    {
        title: "비건 식당 어디까지 가봤니?",
        content: "요즘 뜨는 비건 맛집 리스트! 채식주의자뿐 아니라 일반 고객도 매료된 맛집 소개.",
        img: "https://picsum.photos/id/292/800/400"
    },
    {
        title: "인터넷 밈이 바꾼 정치 풍토",
        content: "정치와 유머의 경계가 무너지고 있습니다. 밈이 여론 형성에 미치는 영향은 점점 커지고 있습니다.",
        img: "https://picsum.photos/id/1025/800/400"
    },
    {
        title: "AI 작곡 시대의 저작권 논쟁",
        content: "AI가 만든 음악은 누구의 것인가? 작곡가 협회와 기술 기업의 입장은 엇갈리고 있습니다.",
        img: "https://picsum.photos/id/1037/800/400"
    },
    {
        title: "한국인의 커피 사랑",
        content: "한국인의 1일 평균 커피 소비량은 세계 상위권입니다. 스타트업들도 커피 구독 서비스를 출시하고 있죠.",
        img: "https://picsum.photos/id/8/800/400"
    },
    {
        title: "우주 탐사 기술, 어디까지 왔나",
        content: "NASA와 SpaceX의 협력, 그리고 유럽 우주국의 차세대 발사체 계획까지 총정리합니다.",
        img: "https://picsum.photos/id/1003/800/400"
    },
    {
        title: "2030세대의 주거 트렌드",
        content: "오피스텔 대신 도시형 생활주택을 선택하는 청년층이 늘고 있습니다.",
        img: "https://picsum.photos/id/1060/800/400"
    }
];

function renderArticles(filter = "") {
    const container = document.getElementById("newsList");
    container.innerHTML = "";
    const filtered = articles.filter(a =>
        a.title.toLowerCase().includes(filter.toLowerCase())
    );
    filtered.forEach((article, index) => {
        const id = "collapse" + index;
        let paragraphs = article.content.split(/\n\s*\n/).map((p) => {
            if (p.trim().startsWith("출처:")) {
                const url = p.trim().replace(/^출처:\s*/, "");
                return `<p>출처: <a href="${url}" target="_blank" rel="noopener noreferrer">기사 링크</a></p>`;
            } else {
                return `<p>${p.trim()}</p>`;
            }
        }).join("");
        container.innerHTML += `
      <div class="card mb-4 news-item">
        <img src="${article.img}" class="card-img-top" alt="news image">
        <div class="card-body">
          <h2 class="card-title">${article.title}</h2>
          <p><a class="btn btn-outline-primary btn-sm" data-bs-toggle="collapse" href="#${id}" role="button" aria-expanded="false" aria-controls="${id}">더 읽기</a></p>
          <div class="collapse" id="${id}">
            <div class="card card-body">
              ${paragraphs}
            </div>
          </div>
        </div>
        <div class="card-footer text-muted">Posted on July 18, 2025</div>
      </div>
    `;
    });
}

document.getElementById("searchInput").addEventListener("input", (e) => {
    renderArticles(e.target.value);
});
renderArticles();

function updateNav() {
    const isLogin = !!localStorage.getItem("token");
    document.getElementById("navLogin").style.display = isLogin ? "none" : "";
    document.getElementById("navRegister").style.display = isLogin ? "none" : "";
    document.getElementById("navLogout").style.display = isLogin ? "" : "none";
}

function logout() {
    localStorage.removeItem("token");
    updateNav();
    window.location.href = "/";
}

window.addEventListener("DOMContentLoaded", updateNav);
