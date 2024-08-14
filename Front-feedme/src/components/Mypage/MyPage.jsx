import React, { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { logout, setToken } from '../../store/slice';
import { fetchUserData } from '../../store/userSlice';
import Sidebar from '../Main/Sidebar';
import Search from '../Main/Search';
import Creature from './Creature';
import CreatureDel from './CreatureDel';
import './MyPage.css';


const LogoutModal = ({ onClose, onConfirm }) => {
  return (
    <div className="MyPageLogoutModal">
      <div className="MyPageLogoutModalContent">
        <p>로그아웃 하시겠습니까?</p>
        <div className="MyPageLogoutModalButtons">
          <button className="MyPageLogoutModalCancel" onClick={onClose}>취 소</button>
          <button className="MyPageLogoutModalLogout" onClick={onConfirm}>로그아웃</button>
        </div>
      </div>
    </div>
  );
};

const EditModal = ({  onClose, onConfirm, nickname, setNickname, birthday, setBirthday })=> {
  return (
    <div className="MyPageLogoutModal">
      <div className="MyPageInfoModalContent">
        <p>회원 정보를 수정하시겠습니까?</p>
        <label>
          닉네임:
          <input 
            type="text" 
            value={nickname} 
            onChange={(e) => setNickname(e.target.value)} 
          />
        </label>
        <label>
          생ㅤ일:
          <input 
            type="date" 
            value={birthday} 
            onChange={(e) => setBirthday(e.target.value)} 
          />
        </label>
        <div className="MyPageLogoutModalButtons">
          <button className="MyPageLogoutModalCancel" onClick={onClose}>취 소</button>
          <button className="MyPageLogoutModalcorrection" onClick={onConfirm}>수 정</button>
        </div>
      </div>
    </div>
  );
};

const ReleaseModal = ({ onClose, onConfirm }) => {
  return (
    <div className="MyPageLogoutModal">
      <div className="MyPageCreModalContent">
        <p>이렇게 귀여운 크리처를 정말로 방생하시겠습니까?</p>
        <div className="MyPageLogoutModalButtons">
          <button className="MyPageLogoutModalCancel" onClick={onClose}>취 소</button>
          <button className="MyPageLogoutModalLogout" onClick={onConfirm}>방 생</button>
        </div>
      </div>
    </div>
  );
};

const MyPage = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();

  useEffect(() => {
    const sessionToken = sessionStorage.getItem('accessToken');
    if (sessionToken) {
      dispatch(setToken(sessionToken));
    } else {
      navigate('/login'); 
    }
  }, [dispatch, navigate]);

  const token = useSelector((state) => state.auth.token);
  const user = useSelector((state) => state.user);
  const { nickname, email, brithday, creatureId, creatureName, exp, level, image, togetherDay, status, error } = user;

  useEffect(() => {
    if (token) {
      dispatch(fetchUserData(token));
    }
  }, [dispatch, token]);

  const [isLogoutModalOpen, setLogoutModalOpen] = useState(false);
  const [isEditModalOpen, setEditModalOpen] = useState(false);
  const [isReleaseModalOpen, setReleaseModalOpen] = useState(false); 

  const [newNickname, setNickname] = useState(nickname); 
  const [newBirthday, setBirthday] = useState(brithday);

  const handleLogoutClick = () => {
    setLogoutModalOpen(true);
  };

  const handleEditClick = () => {
    setEditModalOpen(true);
  };

  const handleReleaseClick = () => {
    setReleaseModalOpen(true);
  };

  const handleCloseModal = () => {
    setLogoutModalOpen(false);
    setEditModalOpen(false);
    setReleaseModalOpen(false); 
  };

  const handleConfirmLogout = () => {
    dispatch(logout()); 
    sessionStorage.removeItem('accessToken'); 
    navigate('/login'); 
  };

  const handleConfirmEdit = () => {
    fetch(`https://i11b104.p.ssafy.io/api/users`, { 
      method: 'PATCH',
      headers: {
        'Authorization': `${token}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        nickname: newNickname,
        birthday: newBirthday,
      }),
    })
      .then(response => {
        if (response.ok) {
          console.log("업데이트 성공");
          dispatch(fetchUserData(token));
        } else {
          console.error('실패');
        }
      })
      .catch(error => {
        console.error('Error:', error);
      })
      .finally(() => {
        setEditModalOpen(false);
      });
  };

  const handleConfirmRelease = () => {
    fetch(`https://i11b104.p.ssafy.io/api/creature`, {
      method: 'DELETE',
      headers: {
        'Authorization': `${token}`, 
      },
    })
      .then(response => {
        if (response.ok) {
          console.log(`크리처 삭제 성공`);
          navigate('/CreatureCreate');
        } else {
          console.error('삭제 실패');
        }
      })
      .catch(error => {
        console.error('Error:', error);
      })
      .finally(() => {
        setReleaseModalOpen(false); 
      });
  };
  
  return (
    <div className="MyPageBack">
      <div className="MyPageMain">
        <Sidebar />
        <div className="MyPageRight">
          <div className="MyPageRightContents">
            <span className="MyPageContentSide"></span>
            <div className="MyPageTitle">
              <h2>마이페이지</h2>
            </div>
            <div className="MyPageInformation">
              <label>
                닉네임:
                <div className="MyPageInfoBox">{nickname}</div> 
              </label>
              <label>
                이메일:
                <div className="MyPageInfoBox">{email}</div> 
              </label>
              <label>
                생ㅤ일:
                <div className="MyPageInfoBox">{brithday}</div> 
              </label>
              <div className='MyPageButtons'>
                <button className="MyPageButtonLogout" onClick={handleLogoutClick}>로그아웃</button>
                <button className="MyPageButton" onClick={handleEditClick}>정보 수정</button>
              </div>
            </div>
            <div className="MypageCreture">
              <Creature 
                creature={{ id: creatureId, name: creatureName, daysTogether: togetherDay, level: level, exp: exp, image: image }} 
              />
              <CreatureDel 
                onRelease={handleReleaseClick} 
                creature={{ id: creatureId }} 
              /> 
            </div>
          </div>
          <Search />
        </div>
      </div>
      {isLogoutModalOpen && <LogoutModal onClose={handleCloseModal} onConfirm={handleConfirmLogout} />}
      {isEditModalOpen && 
        <EditModal 
          onClose={handleCloseModal} 
          onConfirm={handleConfirmEdit} 
          nickname={newNickname} 
          setNickname={setNickname} 
          birthday={newBirthday} 
          setBirthday={setBirthday} 
        />
      }
      {isReleaseModalOpen && <ReleaseModal onClose={handleCloseModal} onConfirm={handleConfirmRelease} />} 
    </div>
  );
};
 
export default MyPage;
