import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Sidebar from '../components/Main/Sidebar';
import SearchBar from '../components/Main/Search';
import CreatureInfo from '../components/Main/CreatureInfo';
import Calendar from '../components/Main/Calendar';
import '../pages/Main.css';
import { useSelector, useDispatch } from 'react-redux';
import { setToken } from '../store/slice';
import axios from 'axios';

const Main = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const token = useSelector((state) => state.auth.token);
  const [weatherCategory, setWeatherCategory] = useState('notknow');

  useEffect(() => {
    const storedToken = sessionStorage.getItem('accessToken');

    if (!token && !storedToken) {
      navigate('/login');
    } else if (storedToken) {
      dispatch(setToken(storedToken));
    }

    const fetchWeather = async () => {
      try {
        navigator.geolocation.getCurrentPosition(
          async (position) => {
            const lat = position.coords.latitude;
            const lon = position.coords.longitude;
            const apiKey = process.env.REACT_APP_API_KEY;
            const response = await fetch(`https://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${lon}&appid=${apiKey}`);
            const data = await response.json();

            if (response.ok) {
              categorizeWeather(data.weather[0].id);
            } else {
              console.error('날씨 정보 가져오기 실패:', data);
              setWeatherCategory('notknow');
            }
          },
          (error) => {
            console.error("위치 정보 가져오기 실패:", error);
            setWeatherCategory('notknow');
          }
        );
      } catch (error) {
        console.error("날씨 정보 가져오는 중 오류 발생:", error);
        setWeatherCategory('notknow');
      }
    };

    const categorizeWeather = (id) => {
      let category;
      if (id >= 200 && id < 600) {
        category = 'rainy';
      } else if (id >= 600 && id < 700) {
        category = 'snowy';
      } else if (id === 800) {
        category = 'sunny';
      } else if (id > 800) {
        category = 'cloudy';
      } else {
        category = 'notknow';
      }
      console.log(category)
      setWeatherCategory(category);
      postWeatherCategory(category);
    };

    const postWeatherCategory = async (category) => {
      try {
        const response = await axios.get(`http://localhost:8080/creatureTodo/${category}`, {
          headers: {
            Authorization:  storedToken || token,
          },
        });
        // console.log('get 요청 성공:', response.data);
      } catch (error) {
        // console.error('get 요청 실패:', error);
      }
    };

    fetchWeather();

  }, [token, navigate, dispatch]);

  return (
    <div className="Main">
      <div className="MainRectangle">
        <Sidebar />
        <div className='MainRight'>
          <SearchBar />
          <div className='MainRightContents'>
            <CreatureInfo />
            <Calendar />
          </div>
        </div>
      </div>
    </div>
  );
};

export default Main;
