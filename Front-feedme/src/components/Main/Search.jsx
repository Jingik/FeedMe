import React, { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { setToken } from '../../store/slice';
import { useNavigate } from 'react-router-dom';
import NotificationModal from '../Notice/NotificationModal';
import '../Main/Search.css';
import noti from '../../assets/icons/icon-noti-gray.png';
import search from '../../assets/icons/icon-search-gray-24.png';
import mypage from '../../assets/icons/icon-account-gray-24.png';
import '../../assets/font/Font.css';

const Search = () => {
  const dispatch = useDispatch();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const [suggestions, setSuggestions] = useState([]);

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

  const fetchSearchResults = async (term) => {
    try {
      const response = await fetch(`https://i11b104.p.ssafy.io/api/users/${term}`, {
        method: 'GET',
        headers: {
          'Authorization': `${token}`,
        },
      });

      if (response.ok) {
        const data = await response.json();
        setSuggestions(data);
        // console.log(data);
      } else {
        setSuggestions([]);
        console.error('실패');
      }
    } catch (error) {
      setSuggestions([]);
      console.error('에러:', error);
    }
  };

  useEffect(() => {
    if (searchTerm) {
      fetchSearchResults(searchTerm);
    } else {
      setSuggestions([]);
    }
  }, [searchTerm]);

  const handleNotificationClick = () => {
    setIsModalOpen(!isModalOpen);
  };

  const handleProfileClick = () => {
    navigate('/MyPage');
  };

  const handleInputChange = (e) => {
    setSearchTerm(e.target.value);
  };

  const handleSuggestionClick = (suggestion) => {
    setSearchTerm(suggestion.nickname);
    setSuggestions([]);
  };

  const handleFriendRequest = async (nickname) => {
    try {
      const response = await fetch('https://i11b104.p.ssafy.io/api/friends', {
        method: 'POST',
        headers: {
          Authorization: token,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ counterpartNickname: nickname }),
      });

      if (response.status === 204) {
        setSuggestions((prevSuggestions) =>
          prevSuggestions.map((suggestion) =>
            suggestion.nickname === nickname
              ? { ...suggestion, requested: true }
              : suggestion
          )
        );
      } else {
        console.error('친구 요청 실패');
      }
    } catch (error) {
      console.error('친구 요청 중 에러 발생:', error);
    }
  };

  return (
    <div className="search-bar-container">
      <input
        type="text"
        className="search-input"
        placeholder="전체 사용자 검색"
        value={searchTerm}
        onChange={handleInputChange}
      />
      <div className="search-icon">
        <img src={search} alt="Search Icon" />
      </div>
      <div className="noti-mypage-icons">
        <div onClick={handleNotificationClick}>
          <img src={noti} alt="Noti Icon" />
        </div>
        <div onClick={handleProfileClick}>
          <img src={mypage} alt="Mypage Icon" />
        </div>
      </div>
      {suggestions.length > 0 && (
        <div className="suggestions-modal">
          <ul className="suggestions-list">
            {suggestions.map((suggestion, index) => (
              <li key={index} onClick={() => handleSuggestionClick(suggestion)}>
                {suggestion.nickname}
                {!suggestion.friend && (
                  <button
                    className="suggestionsListButton"
                    onClick={(e) => {
                      e.stopPropagation(); 
                      if (!suggestion.requested) {
                        handleFriendRequest(suggestion.nickname);
                      }
                    }}
                    disabled={suggestion.requested} 
                  >
                    {suggestion.requested ? '친구 신청 중' : '친구 신청'}
                  </button>
                )}
              </li>
            ))}
          </ul>
        </div>
      )}
      {isModalOpen && <NotificationModal onClose={handleNotificationClick} />}
    </div>
  );
};

export default Search;
