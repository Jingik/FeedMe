import React, { useEffect, useState } from 'react';
import { FaAngleLeft, FaAngleRight, FaEllipsisH, FaPlus, FaPen } from 'react-icons/fa';
import Modal from 'react-modal';
import './TodoMainList.css';
import '../../assets/font/Font.css';
import diary from '../../assets/images/test2.png';
import axios from 'axios';

const TodoMainList = () => {
  const [categories, setCategories] = useState({
    '2024-08-07': [
      { title: '공부', items: ['CS 공부하기', '알고리즘 공부하기', '자격증 공부하기'] },
      { title: '일일 미션', items: ['산책 다녀오기', '우산 챙기기'] }
    ],
    '2024-08-08': [
      { title: '공부', items: ['새로운 과목 공부하기'] },
      { title: '일일 미션', items: ['운동하기'] }
    ],
  });

  const [currentDate, setCurrentDate] = useState(new Date());

  const [selectedTodo, setSelectedTodo] = useState({ categoryIndex: null, todoIndex: null });
  const [categoryModalIsOpen, setCategoryModalIsOpen] = useState(false);
  const [todoModalIsOpen, setTodoModalIsOpen] = useState(false);
  const [addTodoModalIsOpen, setAddTodoModalIsOpen] = useState(false);
  const [drawingModalIsOpen, setDrawingModalIsOpen] = useState(false); 
  const [newCategoryTitle, setNewCategoryTitle] = useState('');
  const [newTodo, setNewTodo] = useState('');
  const [editedTodo, setEditedTodo] = useState('');
  const [currentCategoryIndex, setCurrentCategoryIndex] = useState(null);

  useEffect(() => {
    const todoRequest = async () => {
      try {
        const response = await axios.get('https://i11b104.p.ssafy.io/api/todos/main/daily', {
          headers: {
            'Content-Type': 'application/json',
            'Authorization': sessionStorage.getItem('accessToken'),
          },
          params: {
            date: '2024-08-13',
            memberId: 1
          }
        });        
  
        if (response.status === 200) {
          console.log(response.data); // 실제 데이터에 접근
        } else {
          console.log('불러오기 실패', response);
        }
      } catch (error) {
        console.error('서버 요청 중 오류 발생', error);
      }
    };

    const dailyTodoRequest = async () => {
      try {
        const response = await axios.get('https://i11b104.p.ssafy.io/api/todos/main/daily', {
          headers: {
            'Content-Type': 'application/json',
            'Authorization': sessionStorage.getItem('accessToken'),
          },
          params: {
            date: '2024-08-13',
            memberId: 1
          }
        });        
  
        if (response.status === 200) {
          console.log(response.data); // 실제 데이터에 접근
        } else {
          console.log('불러오기 실패', response);
        }
      } catch (error) {
        console.error('서버 요청 중 오류 발생', error);
      }
    };

    const diaryButtonRequest = async () => {
      try {
        const response = await axios.get('https://i11b104.p.ssafy.io/api/todos/main/daily', {
          headers: {
            'Content-Type': 'application/json',
            'Authorization': sessionStorage.getItem('accessToken'),
          },
          params: {
            date: '2024-08-13',
            memberId: 1
          }
        });        
  
        if (response.status === 200) {
          console.log(response.data); // 실제 데이터에 접근
        } else {
          console.log('불러오기 실패', response);
        }
      } catch (error) {
        console.error('서버 요청 중 오류 발생', error);
      }
    };
  
    todoRequest();
    dailyTodoRequest();
    dailyTodoRequest();
  }, []);

  const formatDate = (date) => {
    return date.toISOString().split('T')[0];
  };

  const handleIncreaseDate = () => {
    setCurrentDate(new Date(currentDate.setDate(currentDate.getDate() + 1)));
  };

  const handleDecreaseDate = () => {
    setCurrentDate(new Date(currentDate.setDate(currentDate.getDate() - 1)));
  };

  const getCurrentDateCategories = () => {
    const formattedDate = formatDate(currentDate);
    return categories[formattedDate] || [];
  };

  const handleAddCategory = () => {
    setCategoryModalIsOpen(true);
  };

  const handleAddTodo = (categoryIndex) => {
    setCurrentCategoryIndex(categoryIndex);
    setAddTodoModalIsOpen(true);
  };

  const handleAddTodoSubmit = () => {
    if (newTodo) {
      const formattedDate = formatDate(currentDate);
      const newCategories = { ...categories };
      newCategories[formattedDate][currentCategoryIndex].items.push(newTodo);
      setCategories(newCategories);
      setNewTodo('');
      setAddTodoModalIsOpen(false);
    }
  };

  const handleEditTodo = (categoryIndex, todoIndex) => {
    const formattedDate = formatDate(currentDate);
    const newCategories = { ...categories };
    newCategories[formattedDate][categoryIndex].items[todoIndex] = editedTodo;
    setCategories(newCategories);
    setSelectedTodo({ categoryIndex: null, todoIndex: null });
    setTodoModalIsOpen(false);
  };

  const handleDeleteTodo = (categoryIndex, todoIndex) => {
    const formattedDate = formatDate(currentDate);
    const newCategories = { ...categories };
    newCategories[formattedDate][categoryIndex].items.splice(todoIndex, 1);
    setCategories(newCategories);
    setSelectedTodo({ categoryIndex: null, todoIndex: null });
    setTodoModalIsOpen(false);
  };

  const toggleOptions = (categoryIndex, todoIndex) => {
    setSelectedTodo({ categoryIndex, todoIndex });
    setEditedTodo(getCurrentDateCategories()[categoryIndex].items[todoIndex]);
    setTodoModalIsOpen(true);
  };

  const handleCategoryModalSubmit = () => {
    if (newCategoryTitle) {
      const formattedDate = formatDate(currentDate);
      const newCategories = { ...categories };
      if (!newCategories[formattedDate]) {
        newCategories[formattedDate] = [];
      }
      newCategories[formattedDate].push({ title: newCategoryTitle, items: [] });
      setCategories(newCategories);
      setNewCategoryTitle('');
      setCategoryModalIsOpen(false);
    }
  };

  const isSameDay = (date1, date2) => {
    return date1.getFullYear() === date2.getFullYear() &&
           date1.getMonth() === date2.getMonth() &&
           date1.getDate() === date2.getDate();
  };

  const today = new Date();
  const yesterday = new Date();
  yesterday.setDate(today.getDate() - 1);

  const formattedCurrentDate = currentDate.toLocaleDateString('ko-KR', {
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  });

  return (
    <div className="TodoMainListContainer">
      <div className="TodoHeader">
        <FaAngleLeft className="TodoArrow" onClick={handleDecreaseDate} /> 
        <h3>{formattedCurrentDate}</h3>
        <FaAngleRight className="TodoArrow" onClick={handleIncreaseDate} />
      </div>

      <div className="TodoSections">
        {getCurrentDateCategories().map((category, categoryIndex) => (
          <div className="TodoSection" key={categoryIndex}>
            <div className="TodoSectionHeader">
              <h4>{category.title}</h4>
              {category.title !== '일일 미션' && (
                <FaPlus className="AddTodoButton" onClick={() => handleAddTodo(categoryIndex)} />
              )}
            </div>
            <ul>
              {category.items.map((item, todoIndex) => (
                <li key={todoIndex} className="TodoItem">
                  <div className="TodoItemContent">
                    <input type="checkbox" /> {item}
                    <FaEllipsisH className="TodoOptionsButton" onClick={() => toggleOptions(categoryIndex, todoIndex)} />
                  </div>
                </li>
              ))}
            </ul>
          </div>
        ))}
      </div>

      <div className="TodoActions">
        {(isSameDay(currentDate, today) || isSameDay(currentDate, yesterday)) && (
          <button className="CreateDrawingButton" onClick={() => setDrawingModalIsOpen(true)}>
            <FaPen className="DrawingIcon" />
            그림일기 생성
          </button>
        )}
        <FaEllipsisH className="MoreOptionsButton" onClick={handleAddCategory} />
      </div>

      <Modal
        isOpen={categoryModalIsOpen}
        onRequestClose={() => setCategoryModalIsOpen(false)}
        contentLabel="새로운 카테고리 추가"
        className="TodoMainModal"
        overlayClassName="TodoMainOverlay"
      >
        <h2 className="TodoMainModalTitle">새로운 카테고리 추가</h2>
        <input
          type="text"
          value={newCategoryTitle}
          onChange={(e) => setNewCategoryTitle(e.target.value)}
          placeholder="카테고리를 입력하세요"
          className="TodoMainModalInput"
        />
        <div className="TodoMainModalButtons">
          <button className="TodoMainModalButton" onClick={() => setCategoryModalIsOpen(false)}>취소</button>
          <button className="TodoMainModalButton" onClick={handleCategoryModalSubmit}>추가</button>
        </div>
      </Modal>

      <Modal
        isOpen={addTodoModalIsOpen}
        onRequestClose={() => setAddTodoModalIsOpen(false)}
        contentLabel="새로운 할 일 추가"
        className="TodoMainModal"
        overlayClassName="TodoMainOverlay"
      >
        <h2 className="TodoMainModalTitle">Todo 추가</h2>
        <input
          type="text"
          value={newTodo}
          onChange={(e) => setNewTodo(e.target.value)}
          placeholder="할 일을 입력하세요"
          className="TodoMainModalInput"
        />
        <div className="TodoMainModalButtons">
          <button className="TodoMainModalButton" onClick={() => setAddTodoModalIsOpen(false)}>취소</button>
          <button className="TodoMainModalButton" onClick={handleAddTodoSubmit}>추가</button>
        </div>
      </Modal>

      <Modal
        isOpen={todoModalIsOpen}
        onRequestClose={() => setTodoModalIsOpen(false)}
        contentLabel="Todo 옵션"
        className="TodoMainModal"
        overlayClassName="TodoMainOverlay"
      >
        <h2 className="TodoMainModalTitle">Todo 옵션</h2>
        <input
          type="text"
          value={editedTodo}
          onChange={(e) => setEditedTodo(e.target.value)}
          placeholder="할 일을 수정하세요"
          className="TodoMainModalInput"
        />
        <div className="TodoMainModalButtons">
          <button className="TodoMainModalButton" onClick={() => handleDeleteTodo(selectedTodo.categoryIndex, selectedTodo.todoIndex)}>삭제</button>
          <button className="TodoMainModalButton" onClick={() => handleEditTodo(selectedTodo.categoryIndex, selectedTodo.todoIndex)}>수정</button>
        </div>
      </Modal>

      <Modal
        isOpen={drawingModalIsOpen}
        onRequestClose={() => setDrawingModalIsOpen(false)}
        contentLabel="그림일기 생성"
        className="TodoMainModalD"
        overlayClassName="TodoMainOverlay"
      >
        <h2 className="TodoMainModalTitle">그림일기 생성</h2>
        <img src={diary} alt="그림일기 이미지" className="TodoMainModalDImage" /> 
        <div className="TodoMainModalButtons">
          <button className="TodoMainModalButton" onClick={() => setDrawingModalIsOpen(false)}>취소</button>
          <button className="TodoMainModalButton">생성</button>
        </div>
      </Modal>

    </div>
  );
};

export default TodoMainList;
