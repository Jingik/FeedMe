import React, { useEffect, useState } from 'react';
import './NotificationModal.css';
import SettingsOutlinedIcon from '@mui/icons-material/SettingsOutlined';
import ArrowBackIosNewOutlinedIcon from '@mui/icons-material/ArrowBackIosNewOutlined';
import Switch from '@mui/material/Switch';
import { TimePicker } from 'antd';
import dayjs from 'dayjs';
import customParseFormat from 'dayjs/plugin/customParseFormat';
import CloseIcon from '@mui/icons-material/Close';
import NotificationsNoneOutlinedIcon from '@mui/icons-material/NotificationsNoneOutlined';
import GroupAddOutlinedIcon from '@mui/icons-material/GroupAddOutlined';
import { addNotifications, setRequests, addRequests, removeRequests, setIsSettingsMode, setIsSwitchOn, setRequestMode, setAlarmTime } from '../../store/alarmSlice';
import { useDispatch, useSelector } from 'react-redux';
import axios from 'axios';
import { EventSourcePolyfill } from 'event-source-polyfill';

dayjs.extend(customParseFormat);

const label = { inputProps: { 'aria-label': 'Color switch demo' } };
const format = 'HH';

const NotificationModal = ({ onClose }) => {
  const dispatch = useDispatch();
  const { notifications, requests, isSettingsMode, isSwitchOn, isRequestMode, alarmTime } = useSelector((state) => state.alarm);
  const { token } = useSelector((state) => state.auth);

  // useEffect(() => {
  
  //   const eventSource = new EventSourcePolyfill('https://i11b104.p.ssafy.io/api/alarms/subscribe/alarm', {
  //     headers: {
  //       'Authorization': sessionStorage.getItem('accessToken')
  //     }
  //   });

  //   console.log('요청 완료!');

  //   eventSource.addEventListener('alarm', (event) => { // 서버에서 설정한 이름과 같아야 함.
  //     // 서버에서 데이터가 전송될 때 호출되는 이벤트 핸들러
  //     console.log(event.data);
  //     console.log('Success!');
  //     const newNotification = JSON.parse(event.data);
  //     dispatch(addNotifications(newNotification));
  //   });

  //   // eventSource.onmessage = (event) => {
  //   //   // 이벤트 데이터 처리
  //   //   console.log('event data', event.data);
    
  //   // };
  
  //   eventSource.addEventListener('error', (error) => {
  //     // SSE 연결 오류 처리
  //     console.error('SSE Error:', error);
  //     eventSource.close(); // 연결을 닫기
  //   });
  
  //   // 컴포넌트가 언마운트되면 SSE 연결을 닫기
  //   return () => {
  //     eventSource.close();
  //   };
  // }, []);

  const requestList = async () => {
    try {
      const res = await axios.get('https://i11b104.p.ssafy.io/api/friends/request', {
        headers: {
          'Authorization': sessionStorage.getItem('accessToken')
        }
      });
      console.log('requests : ', res.data);
      dispatch(setRequests(res.data));
    } catch (err) {
      console.log('Error : ', err);
    }
  };

  useEffect(() => {
    requestList();
  }, []);

  const handleDelete = (index) => {
    const newNotifications = notifications.filter((_, i) => i !== index);
    dispatch(addNotifications(newNotifications));
  };

  const handleReject = async (index, requestId) => {
    try {
      await axios.post(`https://i11b104.p.ssafy.io/api/friends/reject/${requestId}`, {}, {
        headers: {
          'Authorization': sessionStorage.getItem('accessToken'),
          'Content-Type': 'application/json',
        }
      });
      dispatch(removeRequests(index));
      console.log('요청 거절 완료!');
      window.location.reload();
    } catch (error) {
      console.error('Error rejecting friend request:', error);
    }
  };

  const handleAccept = async (index, requestId) => {
    try {
      await axios.post(`https://i11b104.p.ssafy.io/api/friends/accept/${requestId}`, {}, {
        headers: {
          'Authorization': sessionStorage.getItem('accessToken'),
          'Content-Type': 'application/json',
        }
      });
      dispatch(removeRequests(index));
      console.log('요청 수락 완료!');
      window.location.reload();
    } catch (error) {
      console.error('Error accepting friend request:', error);
    }
  };

  const handleTimeChange = async (time) => {
    if (time) {
      const formattedTime = time.format('HH');
      const intAlarmTime = parseInt(formattedTime, 10);

      dispatch(setAlarmTime(time));

      try {
        await axios.post('https://i11b104.p.ssafy.io/api/alarms/time', {
          alarmTime: intAlarmTime
        },
          {
            headers: {
              'Content-Type': 'application/json',
              'Authorization': `${token}`,
            }
          }
        );
        console.log('Time setting updated');
      } catch (error) {
        // console.error('Error setting time:', error);
      }
    } else {
      // console.log('No alarm time');
    }
  };

  const toggleSettingsMode = () => {
    dispatch(setIsSettingsMode(!isSettingsMode));
  };

  const handleSwitchChange = () => {
    dispatch(setIsSwitchOn(!isSwitchOn));
  };

  const toggleRequestMode = () => {
    // const eventSource2 = new EventSourcePolyfill('https://i11b104.p.ssafy.io/api/alarms/subscribe/chat', {
    //   headers: {
    //     'Authorization': sessionStorage.getItem('accessToken')
    //   }
    // });

    // console.log('요청 완료!2');

    // eventSource2.addEventListener('friend', (event) => { // 서버에서 설정한 이름과 같아야 함.
    //   // 서버에서 데이터가 전송될 때 호출되는 이벤트 핸들러
    //   console.log(event.data);
    //   console.log('Success!');
    //   const newRequest = JSON.parse(event.data);
    //   dispatch(addRequests(newRequest));
    // });

    dispatch(setRequestMode(!isRequestMode));

    // // 컴포넌트가 언마운트되면 SSE 연결을 닫기
    // return () => {
    //   eventSource2.close();
    // };
  }

  return (
    <div className="NoticeModal" onClick={onClose}>
      <div className="NoticeContent" onClick={(e) => e.stopPropagation()}>
        <div className='NoticeTitle'>
          <h2>{isSettingsMode ? '알림 설정' : '알림'}</h2>
          {isSettingsMode ? (
            <ArrowBackIosNewOutlinedIcon
              style={{
                color: '#49454F',
                cursor: "pointer"
              }}
              onClick={toggleSettingsMode} />
          ) : (
            <SettingsOutlinedIcon
              style={{
                color: '#49454F',
                cursor: "pointer"
              }}
              onClick={toggleSettingsMode} />
          )}
        </div>
        {isSettingsMode ? (
          <div className="SettingsContent">
            <div className='SettingOnOff'>
              <p>알림 받기</p>
              <Switch {...label} checked={isSwitchOn} onChange={handleSwitchChange} color="secondary" />
            </div>
            <TimePicker
              value={alarmTime} // 현재 선택된 시간
              format={format}
              disabled={!isSwitchOn}
              showMinute={false}
              showSecond={false}
              onChange={handleTimeChange} // 시간 변경 시 호출
            // inputReadOnly
            />
          </div>
        ) : (
          <div>
            {isRequestMode ? (
              <div>
                <div className='AlarmFriends'
                  onClick={toggleRequestMode}>
                  <NotificationsNoneOutlinedIcon />
                  <span>알림 목록 보기</span>
                </div>
                <ul>
                  {requests.map((request, index) => (
                    <li key={index}>
                      <img src={request.creatureImg}
                        style={{
                          width: "35px",
                          marginRight: "20px",
                          borderRadius: "50%",
                        }} alt='creatureImg' />
                      <span style={{
                        fontFamily: "PretendardM"
                      }}>{request.counterpartNickname}</span>
                      <div className='RequestButtons'>
                        <span
                          style={{
                            width: "35px",
                            fontFamily: "PretendardSB",
                            color: "#696969"
                          }}
                          className="NoticeButton" onClick={() => handleReject(index, request.id)}>거절</span>
                        <span
                          style={{
                            width: "35px",
                            marginLeft: "15px",
                            fontFamily: "PretendardSB",
                            color: "#007bff"
                          }}
                          className="NoticeButton" onClick={() => handleAccept(index, request.id)}>수락</span>
                      </div>
                    </li>
                  ))}
                </ul>
              </div>
            ) : (
              <div>
                <div className='AlarmFriends'
                  onClick={toggleRequestMode}>
                  <GroupAddOutlinedIcon />
                  <span>친구 요청 목록 보기</span>
                </div>
                <ul>
                  {notifications.map((notification, index) => (
                    <li key={index}>
                      <NotificationsNoneOutlinedIcon
                        style={{
                          width: "19px",
                          marginRight: "13px"
                        }} />
                      <span>{notification}</span>
                      <CloseIcon
                        style={{
                          width: "19px",
                          marginLeft: "auto"
                        }}
                        className="NoticeButton" onClick={() => handleDelete(index)} />
                    </li>
                  ))}
                </ul>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default NotificationModal;
