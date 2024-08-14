// ReactCalendar.js
import React, { useEffect } from 'react';
import '../Main/Calendar.css';
import Calendar from 'react-calendar';
import 'react-calendar/dist/Calendar.css';
import { useState } from 'react';
import axios from 'axios';
import { useDispatch, useSelector } from 'react-redux';
import { setCalendarTodos, addCalendarTodos } from '../../store/todoSlice';

function ReactCalendar() {
  const dispatch = useDispatch();
  const [value, onChange] = useState(new Date()); // 초기값은 현재 날짜
  const { token } = useSelector((state) => state.auth);
  const { calendarTodos } = useSelector((state) => state.todo);

  
  useEffect(() => {
    const date = value.toISOString().split('T')[0];
    console.log('value : ', date);

    axios.get(`http://localhost:8080/todos/calendar?date=${date}`,
      {
        headers: { Authorization: sessionStorage.getItem('accessToken'), },
      }
    )
    .then(response => {
      if (Array.isArray(response.data)) {
        dispatch(setCalendarTodos(response.data));
      } else {
        console.error('Unexpected response format:', response.data);
      }
  })
    .catch((error) => console.error('Error:', error));
  }, [value]);

  console.log('Calendar Todos:', calendarTodos);

  // 각 타일에 숫자를 표시하는 함수
  const tileContent = ({ date, view }) => {
    if (view === 'month') {
      const todoForDate = calendarTodos.find(calendarTodo => {
        const todoDate = new Date(calendarTodo.date);
  
        return (
          todoDate.getDate() === date.getDate() &&
          todoDate.getMonth() === date.getMonth() &&
          todoDate.getFullYear() === date.getFullYear()
        );
      });
  
      if (todoForDate) {
        return (
          <div className='date-number'>
            {todoForDate.completed}/{todoForDate.total}
          </div>
        );
      }
    }
  
    return null;
  };
  

  return (
    <div className='Calendar'>
      <Calendar
        onChange={onChange}
        value={value}
        tileContent={tileContent}
      />
    </div>
  );
}

export default ReactCalendar;