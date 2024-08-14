import React, { useEffect } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faAngleLeft } from '@fortawesome/free-solid-svg-icons';
import './LoginInfo.css';
import '../../assets/font/Font.css'
import { useDispatch, useSelector } from 'react-redux';
import { setNickname, setEmail, setBirthday } from '../../store/slice';
import axios from 'axios';

const Signup = () => {
  const dispatch = useDispatch();
  const { nickname, email, birthday } = useSelector((state) => state.auth);
  const location = useLocation();
  const navigate = useNavigate();

  const params = new URLSearchParams(location.search);
  const urlEmail = params.get('email');

  useEffect(() => {
    dispatch(setEmail(urlEmail));
  }, [dispatch, urlEmail]);

  const handleSubmit = async (e) => {
    e.preventDefault();

    console.log(nickname);
    console.log(email);
    console.log(birthday);

    try {
      const response = await axios.post('http://localhost:8080/users', {
        nickname,
        email,
        birthday,
      });

      if (response.status === 200) {
        navigate('/CreatureCreate');
      } else {
        console.log('회원 등록 실패', response.data);
      }
    } catch (error) {
      console.error('서버 요청 중 오류 발생', error);
    }
  };

  return (
    <div className="LoginMain">
      <div className="LoginMainFormContainer">
        <Link to="/Login" className="backButton">
          <FontAwesomeIcon icon={faAngleLeft} size="2x" />
        </Link>
        <div className="LoginMainHeadre">회원 정보 입력</div>
        <form className='LoginInfoForm' onSubmit={handleSubmit}>
          <div className="LoginInfoNicknameContainer">
            <label htmlFor="nickname" className="LoginInfoFormNickname">닉네임</label>
            <input
              className="LoginInfoFormNicknameInput"
              type="text"
              value={nickname}
              onChange={(e) => dispatch(setNickname(e.target.value))}
            />
          </div>

          <div className="LoginInfoEmailContainer">
            <label htmlFor="email" className="LoginInfoEmail">이메일</label>
            <input
              className="LoginInfoEmailInput"
              type="email"
              value={email}
              readonly
            />
          </div>

          <div className="LoginInfoBirthdateContaine">
            <label htmlFor="birthdate" className="LoginInfoBirthdate">생 일</label>
            <input
              className="LoginInfoBirthdateInput"
              type="date"
              value={birthday}
              onChange={(e) => {
                dispatch(setBirthday(e.target.value));
              }}
            />
          </div>
          <button type="submit" className="LoginInfoButton">다음</button>
        </form>
      </div>
    </div>
  );
};

export default Signup;