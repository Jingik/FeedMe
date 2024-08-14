import React from "react";
import { createButton } from "react-social-login-buttons";
import kakaoLogo from "../../assets/images/kakao-logo.png";

const handleKakaoLogin = () => {
    // window.location.href = 'https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=e76f1a4a0c79727d85cd6b3b1969c892&redirect_uri=http://localhost:3000/login/oauth2/code/kakao';
    window.location.href = 'https://i11b104.p.ssafy.io/api/oauth2/authorization/kakao';
};

const config = {
    text: "Continue with Kakao",
    icon: (props) => <img src={kakaoLogo} alt="Kakao" style={{ width: "25px", height: "25px" }} {...props} />,
    style: { background: "#F7E600", color: '#000000', display: 'flex', alignItems: 'center', justifyContent: 'center' },
    activeStyle: { background: "#E1D100" },
    onClick: handleKakaoLogin,
};

/** My Naver login button. */
const MyKakaoLoginButton = createButton(config);

export default MyKakaoLoginButton;