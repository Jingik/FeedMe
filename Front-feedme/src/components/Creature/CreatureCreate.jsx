import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faAngleLeft } from '@fortawesome/free-solid-svg-icons';
import { useDispatch, useSelector } from 'react-redux';
import { setCreatureName, setKeyword, addKeyword, removeKeyword, resetKeywords, } from '../../store/slice';
import './CreatureCreate.css';
import '../../assets/font/Font.css';
import axios from 'axios';

const CreatureCreate = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const { creatureName, keyword, token } = useSelector((state) => state.auth);
  const [photo, setPhoto] = useState(null); // local state로 photo 관리

  const handleImageUpload = (e) => {
    const fileUrl = e.target.files[0];
    if (fileUrl) {
      setPhoto(fileUrl); // 로컬 상태에 파일 저장
    }
  };

  // const handleKeywordChange = (type, value) => {
  //   const keyword = { type, value };
  //   const isKeywordExist = keywords.some((kw) => kw.type === type);

  //   if (isKeywordExist) {
  //     // 이미 존재하는 키워드 타입을 제거하고 새로운 값으로 추가
  //     dispatch(removeKeyword({ type }));
  //   }

  //   dispatch(addKeyword(keyword));
  // };

  const handleSubmit = async (e) => {
    e.preventDefault();

    // const formData = new FormData();
    // formData.append('creatureName', creatureName);
    // formData.append('keywords', JSON.stringify(keywords)); // 키워드 배열 직렬화
    // if (photo) {
    //   formData.append('photo', photo.name); // 파일 추가
    // }

    // const formData = new FormData();
    // formData.append('creatureName', creatureName);
    // formData.append('photo', photo.name);
    // formData.append('keyword', keyword);

    // // FormData를 JSON으로 변환하는 함수
    // function formDataToJson(formData) {
    //   const obj = {};
    //   formData.forEach((value, key) => {
    //     if (value instanceof File) {
    //       // 파일 메타데이터를 JSON으로 포함
    //       obj[key] = {
    //         name: value.name,
    //         size: value.size,
    //         type: value.type,
    //       };
    //     } else {
    //       obj[key] = value;
    //     }
    //   });
    //   return JSON.stringify(obj);
    // }

    // const json = formDataToJson(formData);

    console.log(photo.name);
    console.log(creatureName);
    console.log(keyword);
    console.log(token);

    // 서버에 JSON 형식으로 데이터 보내기
    try {
      const response = await axios.post('https://i11b104.p.ssafy.io/api/creature', {
        creatureName,
        keyword,
        photo: photo.name  
      }, {
        headers: {
          'Content-Type': 'application/json',
          'Authorization': sessionStorage.getItem('accessToken'),
        }
      });

      if (response.status === 200) {
        navigate('/CreatureResult');
      } else {
        console.log('크리쳐 생성 실패', response.data);
      }
    } catch (error) {
      console.error('서버 요청 중 오류 발생', error);
    }
  };

  return (
    <div className="CreatureCreateMain">
      <div className="CreatureCreateFormContainer">
        <Link to="/Signup" className="backButton">
          <FontAwesomeIcon icon={faAngleLeft} size="2x" />
        </Link>
        <div className="CreatureCreateMainHeader">아바타 생성하기</div>
        <form className="CreatureCreateForm" onSubmit={handleSubmit}>
          <div className="CreatureCreateFormNicknameContainer">
            <label htmlFor="nickname" className="CreatureCreateFormNickname">이ㅤ름</label>
            <input
              className="CreatureCreateFormNicknameInput"
              type="text"
              id="nickname"
              value={creatureName}
              onChange={(e) => dispatch(setCreatureName(e.target.value))}
            />
          </div>

          <div className="CreatureCreateFormImageUploadContainer">
            <label htmlFor="imageUpload" className="CreatureCreateFormImageUploadLabel">사ㅤ진</label>
            <input
              className="CreatureCreateFormImageUpload"
              type="file"
              id="imageUpload"
              accept="image/*"
              onChange={handleImageUpload}
            />
          </div>

          {photo && (
            <div className="CreatureCreateFormImagePreviewContainer">
              <img src={URL.createObjectURL(photo)} alt="Preview" className="CreatureCreateFormImagePreview" />
            </div>
          )}

          {/* <div className="CreatureCreateFormKeywordsContainer">
            <label className="CreatureCreateFormKeywordsHeader">키워드</label>
            <div className="CreatureCreateFormKeywords">
              <div>
                <div>개체 선택</div>
                <div className="CreatureCreateFormKeywordOption">
                  <input
                    type="radio"
                    name="type"
                    value="강아지"
                    onChange={(e) => handleKeywordChange('type', e.target.value)}
                  />
                  <label htmlFor="dog">강아지</label>
                </div>
                <div className="CreatureCreateFormKeywordOption">
                  <input
                    type="radio"
                    name="type"
                    value="고양이"
                    onChange={(e) => handleKeywordChange('type', e.target.value)}
                  />
                  <label htmlFor="cat">고양이</label>
                </div>
                <div className="CreatureCreateFormKeywordOption">
                  <input
                    type="radio"
                    name="type"
                    value="판다"
                    onChange={(e) => handleKeywordChange('type', e.target.value)}
                  />
                  <label htmlFor="panda">판다</label>
                </div>
                <div className="CreatureCreateFormKeywordOption">
                  <input
                    type="radio"
                    name="type"
                    value="래서 판다"
                    onChange={(e) => handleKeywordChange('type', e.target.value)}
                  />
                  <label htmlFor="red panda">래서 판다</label>
                </div>
                <div className="CreatureCreateFormKeywordOption">
                  <input
                    type="radio"
                    name="type"
                    value="여자 아이"
                    onChange={(e) => handleKeywordChange('type', e.target.value)}
                  />
                  <label htmlFor="girl">여자 아이</label>
                </div>
                <div className="CreatureCreateFormKeywordOption">
                  <input
                    type="radio"
                    name="type"
                    value="남자 아이"
                    onChange={(e) => handleKeywordChange('type', e.target.value)}
                  />
                  <label htmlFor="a boy">남자 아이</label>
                </div>
              </div>
              <div>
                <div>색상 선택</div>
                <div className="CreatureCreateFormKeywordOption">
                  <input
                    type="radio"
                    name="color"
                    value="빨간색"
                    onChange={(e) => handleKeywordChange('color', e.target.value)}
                  />
                  <label htmlFor="red">빨간색</label>
                </div>
                <div className="CreatureCreateFormKeywordOption">
                  <input
                    type="radio"
                    name="color"
                    value="노란색"
                    onChange={(e) => handleKeywordChange('color', e.target.value)}
                  />
                  <label htmlFor="yellow">노란색</label>
                </div>
                <div className="CreatureCreateFormKeywordOption">
                  <input
                    type="radio"
                    name="color"
                    value="흰색"
                    onChange={(e) => handleKeywordChange('color', e.target.value)}
                  />
                  <label htmlFor="white">흰색</label>
                </div>
                <div className="CreatureCreateFormKeywordOption">
                  <input
                    type="radio"
                    name="color"
                    value="보라색"
                    onChange={(e) => handleKeywordChange('color', e.target.value)}
                  />
                  <label htmlFor="purple">보라색</label>
                </div>
                <div className="CreatureCreateFormKeywordOption">
                  <input
                    type="radio"
                    name="color"
                    value="금색"
                    onChange={(e) => handleKeywordChange('color', e.target.value)}
                  />
                  <label htmlFor="gold">금색</label>
                </div>
                <div className="CreatureCreateFormKeywordOption">
                  <input
                    type="radio"
                    name="color"
                    value="분홍색"
                    onChange={(e) => handleKeywordChange('color', e.target.value)}
                  />
                  <label htmlFor="pink">분홍색</label>
                </div>
                <div className="CreatureCreateFormKeywordOption">
                  <input
                    type="radio"
                    name="color"
                    value="은색"
                    onChange={(e) => handleKeywordChange('color', e.target.value)}
                  />
                  <label htmlFor="silver">은색</label>
                </div>
                <div className="CreatureCreateFormKeywordOption">
                  <input
                    type="radio"
                    name="color"
                    value="회색"
                    onChange={(e) => handleKeywordChange('color', e.target.value)}
                  />
                  <label htmlFor="grey">회색</label>
                </div>
              </div>
            </div>
          </div> */}
          <button type="submit" className="CreatureCreateButton">생성</button>
        </form>
      </div>
    </div>
  );
};

export default CreatureCreate;
