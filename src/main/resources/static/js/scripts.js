function decodeJWT(token) {
    const payloadPart = token.split('.')[1];
    if (!payloadPart) return null;
    try {
        // base64url → base64로 변환
        const base64 = payloadPart.replace(/-/g, '+').replace(/_/g, '/');
        const json = decodeURIComponent(atob(base64).split('').map(function(c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));
        return JSON.parse(json);
    } catch (e) {
        console.error("JWT 디코딩 실패:", e);
        return null;
    }
}

function showAdminSectionIfAdmin() {
    const token = localStorage.getItem('token');
    const payload = decodeJWT(token);
    if (payload && payload.admin === true) {
        document.getElementById('adminSection').style.display = 'block';
    } else {
        document.getElementById('adminSection').style.display = 'none';
    }
}
