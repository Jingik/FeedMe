import axios from 'axios';

const API = axios.create({
	BASE_URL: 'http://localhost:8080/',
    headers: {
      	'Content-Type': 'application/json',
    },
    withCredentials: true,
});

// 요청 인터셉터 설정 : 요청 전 실행
API.interceptors.request.use(
    config => {
        const token = sessionStorage.getItem('accessToken');
        if (token) {
            config.headers['Authorization'] = token;  // Bearer 없이 토큰 설정
        } else {
            console.log('토큰이 없습니다.');
        }
        return config;
    },
    error => {
        return Promise.reject(error);
    }
);

export default API;