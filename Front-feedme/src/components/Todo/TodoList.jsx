import React, { useEffect, useState } from 'react';
import './TodoList.css';
import axios from 'axios';

const ToDoList = ({onClick}) => {

  const [ incompletedTodos, setIncompletedTodos ] = useState([]);

  useEffect(() => { 
    const fetchData = async () => {
      try {
        const response = await axios.get('https://i11b104.p.ssafy.io/api/todos/main/daily', {
          headers: {
            'Content-Type': 'application/json',
            'Authorization': sessionStorage.getItem('accessToken'),
          }
        });
  
        if (response.status === 200) {
          console.log(response.data); // 실제 데이터에 접근
          setIncompletedTodos(response.data); // 불러온 데이터를 상태로 설정
        } else {
          console.log('불러오기 실패', response);
        }
      } catch (error) {
        console.error('서버 요청 중 오류 발생', error);
      }
    };
  
    fetchData();
  }, []);

  console.log('data : ', incompletedTodos.length);
  

  return (
  <div className='ToDoList' onClick={onClick}>
    <div>
      <span className='ToDoListTitle'>TO DO</span>
      <span className='ToDoListNotDo'>
        {incompletedTodos ? (incompletedTodos.length < 10 ? `(0${incompletedTodos.length})` : `(${incompletedTodos.length})`) : '(00)'}
      </span>
    </div>
    <div className='ToDoListContents'>
      <ul>
        {incompletedTodos && incompletedTodos.length > 0 ? (
          incompletedTodos.map((todo, index) => (
            <li key={index}>
              <input type="checkbox" />
              <label htmlFor={`todo-${index}`}> {todo.content}</label>
            </li>
          ))
        ) : (
          <li>할 일이 없습니다...</li> // 로딩 중일 때 표시할 내용
        )}
      </ul>
    </div>
  </div>
);

};

export default ToDoList;
