import React, { useState, useEffect, useRef } from 'react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import { useDispatch, useSelector } from 'react-redux';
import { fetchUserData } from '../../store/userSlice';

const ChatWindow = ({ roomId }) => {
  const dispatch = useDispatch();
  const token = useSelector((state) => state.auth.token);
  const user = useSelector((state) => state.user);
  const { sendId } = user;

  const [username, setUsername] = useState('');
  const [messageContent, setMessageContent] = useState('');
  const [messages, setMessages] = useState([]);
  const [hasMore, setHasMore] = useState(true);
  const [skip, setSkip] = useState(0);
  const messageContainerRef = useRef(null);
  const stompClient = useRef(null);
  const isSubscribed = useRef(false);

  const limit = 10;

  useEffect(() => {
    if (roomId) {
      console.log('Room ID:', roomId);
      disconnect(); // 이전 연결을 명확히 해제
      connect(); // 새로운 연결 설정
    }
    return () => {
      disconnect(); // 컴포넌트 언마운트 시 연결 해제
    };
  }, [roomId]);

  useEffect(() => {
    if (token) {
      dispatch(fetchUserData(token));
    }
  }, [dispatch, token]);

  const connect = () => {
    if (stompClient.current) {
      disconnect(); // 기존 연결이 있을 경우 해제
    }

    const socket = new SockJS('https://i11b104.p.ssafy.io/api/ws/friendChat');
    const newClient = new Client({
        webSocketFactory: () => socket,
        debug: (str) => {
            console.log(str);
        },
        onConnect: (frame) => {
            console.log('Connected: ' + frame);
            
            stompClient.current = newClient; // 새로운 클라이언트 인스턴스를 할당

            if (stompClient.current) { // stompClient.current가 null이 아닌 경우에만 subscribe 시도
                stompClient.current.subscribe(`/chatRoom/messages/${roomId}`, (message) => {
                    const receivedMessage = JSON.parse(message.body);
                    showMessage(receivedMessage);
                });

                stompClient.current.subscribe(`/chatRoom/loadMessages/${roomId}`, (message) => {
                    const slice = JSON.parse(message.body);
                    const newMessages = slice.content || [];
                    if (newMessages.length === 0) {
                        setHasMore(false);
                    } else {
                        setMessages((prevMessages) => [...newMessages.reverse(), ...prevMessages]);
                        setSkip((prevSkip) => prevSkip + limit);
                    }
                    setHasMore(!slice.last);
                });

                isSubscribed.current = true;
                fetchMoreData(); // 초기 데이터 로드
            } else {
                console.warn('stompClient.current is null after connection');
            }
        },
        onStompError: (frame) => {
            console.error('Broker reported error: ' + frame.headers['message']);
            console.error('Additional details: ' + frame.body);
        },
        onDisconnect: () => {
            console.log("Disconnected");
            isSubscribed.current = false;
            stompClient.current = null;
        },
        reconnectDelay: 1000
    });

    newClient.activate();
  };

  const disconnect = () => {
    if (stompClient.current) {
      stompClient.current.deactivate(); // 기존 연결 해제
      stompClient.current = null;
      isSubscribed.current = false;
      setMessages([]); // 메시지 초기화
      setSkip(0); // 스킵 초기화
      setHasMore(true); // 페이징 관련 상태 초기화
      console.log("Disconnected from the WebSocket");
    }
  };

  const sendMessage = () => {
    if (stompClient.current && messageContent.trim() !== '') {
      stompClient.current.publish({
        destination: `/chat/messages/${roomId}`,
        headers:{
          Authorization: `${token}`,
        },
        body: JSON.stringify({
          'sendId': sendId,
          'content': messageContent
        })
      });
      setMessageContent('');
    }
  };

  const fetchMoreData = () => {
    if (stompClient.current && hasMore) {
      stompClient.current.publish({
        destination: `/chat/loadMessages/${roomId}`,
        body: JSON.stringify({
          'skip': skip,
          'limit': limit
        })
      });
    }
  };

  const handleScroll = () => {
    if (messageContainerRef.current.scrollTop === 0 && hasMore) {
      fetchMoreData();
    }
  };

  const showMessage = (message) => {
    console.log("Received message:", message);

    try {
        if (message && typeof message === 'object') {
            setMessages((prevMessages) => [...prevMessages, message]);
        } else {
            console.warn("Unexpected message format:", message);
        }
    } catch (error) {
        console.error("Failed to process message:", error);
        console.error("Original message:", message);
    }

    if (messageContainerRef.current) {
        messageContainerRef.current.scrollTop = messageContainerRef.current.scrollHeight;
    }
  };

  return (
    <div>
      <div>
        <input
          type="text"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          placeholder="Enter your username"
        />
        <br />
        <input
          type="text"
          value={messageContent}
          onChange={(e) => setMessageContent(e.target.value)}
          placeholder="Enter your message"
        />
        <button onClick={sendMessage}>Send</button>
      </div>
      <div
        id="messageContainer"
        style={{ height: '40vh', overflow: 'auto', border: '1px solid black', padding: '10px' }}
        ref={messageContainerRef}
        onScroll={handleScroll}
      >
        {messages.map((message, index) => (
          <div key={index} className="message" style={{ padding: '5px', borderBottom: '1px solid #ddd' }}>
            {message.message}
          </div>
        ))}
      </div>
    </div>
  );
};

export default ChatWindow;
