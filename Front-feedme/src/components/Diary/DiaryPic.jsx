import React, { useState, useEffect } from "react";
import styled from "styled-components";
import './DiaryPic.css';
import UploadOutlinedIcon from '@mui/icons-material/UploadOutlined';
import Modal from 'react-modal';
import axios from 'axios';

function DiaryPic() {
  const [slideIndex, setSlideIndex] = useState(0);
  const [currentDate, setCurrentDate] = useState("");
  const [currentContent, setCurrentContent] = useState("");
  const [drawingModalIsOpen, setDrawingModalIsOpen] = useState(false);
  const [selectedImage, setSelectedImage] = useState(""); // 선택된 이미지를 저장하는 상태
  const [diaries, setDiaries] = useState([]);  // 다이어리 데이터를 저장할 상태
  const [page, setPage] = useState(0);         // 현재 페이지 상태
  const [limit, setLimit] = useState(10);      // 페이지 당 항목 수
  const [totalPages, setTotalPages] = useState(1); // 전체 페이지 수
  const [feedContent, setFeedContent] = useState(""); // 피드 텍스트 내용을 저장하는 상태

  useEffect(() => {
    fetchDiaries(page, limit);
  }, [page, limit]);

  const fetchDiaries = async (page, limit) => {
    try {
      const response = await axios.post('https://i11b104.p.ssafy.io/api/diary/list', {
        skip: page * limit,
        limit: limit,
      },
        {
          headers: {
            'Content-Type': 'application/json',
            'Authorization': sessionStorage.getItem('accessToken'),
          }
        });

      console.log('pages:', response.data.content);

      const sortedDiaries = response.data.content;
      const date = sortedDiaries[0]?.createdAt.split('T')[0];

      setDiaries(sortedDiaries);
      setTotalPages(response.data.totalPages);
      setCurrentDate(date || "");
      setCurrentContent(sortedDiaries[0]?.content || "");
      setSlideIndex(0);
    } catch (error) {
      console.error('Error fetching diaries:', error);
    }
  };

  const moveToPrevSlide = async () => {
    if (diaries.length === 0) {
      return;
    }

    setSlideIndex((prev) => {
      // 현재 페이지의 첫 슬라이드에서 왼쪽으로 이동할 때, 이전 페이지를 요청
      if (prev === 0 && page > 0) {
        const prevPage = page - 1;
        fetchDiaries(prevPage, limit); // 이전 페이지의 데이터를 가져옴
        setPage(prevPage);
        return limit - 1; // 이전 페이지의 마지막 슬라이드로 이동
      }

      const newIndex = prev === 0 ? diaries.length - 1 : prev - 1;
      setCurrentDate(diaries[newIndex].createdAt.split('T')[0]);
      setCurrentContent(diaries[newIndex].content);
      return newIndex;
    });
  };

  const moveToNextSlide = async () => {
    if (diaries.length === 0) {
      return;
    }

    setSlideIndex((prev) => {
      const newIndex = prev === diaries.length - 1 ? 0 : prev + 1;

      // 현재 페이지의 마지막 슬라이드에서 오른쪽으로 이동할 때, 다음 페이지를 요청
      if (newIndex === 0 && page < totalPages - 1) {
        setPage((prevPage) => prevPage + 1);
        fetchDiaries(page + 1, limit); // 다음 페이지의 데이터를 가져옴
        return 0; // 다음 페이지의 첫 번째 슬라이드로 이동
      }

      setCurrentDate(diaries[newIndex].createdAt.split('T')[0]);
      setCurrentContent(diaries[newIndex].content);
      return newIndex;
    });
  };

  const moveDot = (index) => {
    setSlideIndex(index);
    setCurrentDate(diaries[index].createdAt.split('T')[0]);
    setCurrentContent(diaries[index].content);
  };

  const openModalWithImage = () => {
    setSelectedImage(diaries[slideIndex].diaryImg); // 현재 슬라이드의 이미지를 선택
    setDrawingModalIsOpen(true);
  };

  const handleUpload = async () => {
    if (!selectedImage || !feedContent) {
      alert("이미지와 내용을 모두 입력해주세요.");
      return;
    }

    try {
      const response = await axios.post('https://i11b104.p.ssafy.io/api/feed', {
        diaryDate: currentDate,
        content: feedContent,
      }, {
        headers: {
          'Content-Type': 'application/json',
          'Authorization': sessionStorage.getItem('accessToken'),
        }
      });

      if (response.status === 200) {
        alert("피드가 성공적으로 업로드되었습니다.");
        setDrawingModalIsOpen(false);
        setFeedContent("");
      } else {
        alert("피드 업로드에 실패했습니다.");
      }
    } catch (error) {
      console.error('Error uploading feed:', error);
      alert("피드 업로드 중 오류가 발생했습니다.");
    }
  };

  return (
    <div className='DiaryPic'>
      <p className='DiaryPicDay'>{currentDate}</p>
      <ThumbnailContainer>
        {diaries.map((diary, index) => (
          <ThumbnailWrapper
            key={diary.id}
            className={index === slideIndex ? "active" : ""}
            onClick={() => moveDot(index)}
          >
            <Thumbnail
              src={diary.diaryImg}
            />
          </ThumbnailWrapper>
        ))}
      </ThumbnailContainer>
      <Arrow className="DiaryArrows" direction="prev" onClick={moveToPrevSlide}>
        ◀
      </Arrow>
      <Container slideIndex={slideIndex}>
        <Wrapper>
          {diaries.map((diary) => (
            <Slide key={diary.id}>
              <PhotoWrapper>
                <Photo
                  src={diary.diaryImg}
                />
              </PhotoWrapper>
            </Slide>
          ))}
        </Wrapper>
        <span className="DiaryPicContents">{currentContent}</span>
      </Container>
      <Arrow className="DiaryArrows" direction="next" onClick={moveToNextSlide}>
        ▶
      </Arrow>
      {diaries.length === 0 ? (<></>) : (
        <div className="CreateFeedContainer">
          <button className="CreateFeedButton" onClick={openModalWithImage}>
            <UploadOutlinedIcon className="CreateFeedIcon" /> 피드 올리기
          </button>
        </div>
      )}


      <Modal
        isOpen={drawingModalIsOpen}
        onRequestClose={() => setDrawingModalIsOpen(false)}
        contentLabel="피드 올리기"
        className="TodoMainModalD"
        overlayClassName="TodoMainOverlay"
      >
        <h2 className="TodoMainModalTitle">피드 올리기</h2>
        {selectedImage && (
          <img src={selectedImage} alt="선택된 그림일기 이미지" className="DrawingModalDImage" />
        )}
        <textarea
          className="DrawingContent"
          value={feedContent}
          onChange={(e) => setFeedContent(e.target.value)}
          placeholder="내용을 입력하세요"
        ></textarea>
        <div className="TodoMainModalButtons">
          <button className="TodoMainModalButton" onClick={() => setDrawingModalIsOpen(false)}>취소</button>
          <button className="TodoMainModalButton" onClick={handleUpload}>업로드</button>
        </div>
      </Modal>
    </div>
  );
}

export default DiaryPic;


const Container = styled.div`
  width: 500px;
  height: 330px;
  margin: 0 auto;
  overflow: hidden;
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: space-evenly;
  font-family: PretendardR;
  border-radius: 20px;
  background-color: rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(12px);
`;

const Wrapper = styled.div`
  width: 100%;
  height: 70%;
  display: flex;
  transition: all 0.3s ease-in-out;
  transform: translateX(${({ slideIndex }) => slideIndex * -100 + "%"});
`;

const Slide = styled.div`
  width: 100%;
  height: 100%;
  flex-shrink: 0;
  display: flex;
  justify-content: center;
  align-items: center;
`;

const PhotoWrapper = styled.div`
  width: 90%;
  height: 100%;
  overflow: hidden;
  border-radius: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
`;

const Photo = styled.img`
  width: 100%;
  height: 100%;
  object-fit: cover;
`;

const Arrow = styled.div`
  position: absolute;
  top: 0;
  bottom: 0;
  margin: auto 80px;
  left: ${({ direction }) => direction === "prev" && "0px"};
  right: ${({ direction }) => direction === "next" && "0px"};
  width: 45px;
  height: 45px;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  z-index: 1;
  transition: transform 0.3s ease-in-out;
`;

const ThumbnailContainer = styled.div`
  margin: 10px 0;
  display: flex;
  justify-content: center;
`;

const ThumbnailWrapper = styled.div`
  width: 50px;
  height: 50px;
  border-radius: 20px;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid transparent;
  transition: all 0.3s ease-in-out;
  display: flex;
  justify-content: center;
  align-items: center;
  &.active {
    width: 55px;
    height: 55px;
    border-radius: 25px;
    background-color: #9E69FA;
  }
`;

const Thumbnail = styled.img`
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 20px;
`;
