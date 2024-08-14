import React, { useState, useEffect, useRef } from 'react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

const ChatRoom = ({ roomId }) => { // roomId를 props로 받도록 수정
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
    connect();
    return () => {
      disconnect();
    };
  }, [roomId]); // roomId가 변경될 때마다 새로 연결되도록 설정

  const connect = () => {
    if (stompClient.current) return; // 이미 연결되어 있는지 확인

    const socket = new SockJS('https://i11b104.p.ssafy.io/api/ws/friendChat');
    stompClient.current = new Client({
        webSocketFactory: () => socket,
        debug: (str) => {
            console.log(str);
        },
        onConnect: (frame) => {
            console.log('Connected: ' + frame);

            // 현재 채팅방의 메시지 수신
            if (stompClient.current && !isSubscribed.current) {
                stompClient.current.subscribe(`/chatRoom/messages/${roomId}`, (message) => {
                    const receivedMessage = JSON.parse(message.body);
                    showMessage(receivedMessage);
                });

                // 과거 메시지 로드
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
            }

            fetchMoreData(); // 초기 데이터 로드
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
        reconnectDelay: 1000 // 재연결 시도 간격 설정
    });

    stompClient.current.activate();
};

  const disconnect = () => {
    if (stompClient.current) {
      stompClient.current.deactivate();
      stompClient.current = null;
    }
    console.log("Disconnected");
  };

  const sendMessage = () => {
    if (stompClient.current && messageContent.trim() !== '') {
      stompClient.current.publish({
        destination: `/chat/messages/${roomId}`,
        body: JSON.stringify({
          'sendId': username,
          'content': messageContent
        })
      });
      setMessageContent(''); // 메시지 전송 후 입력 필드 비우기
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
    console.log("Received message:", message);  // 메시지가 올바르게 도착하는지 확인
    
    // message의 내용이 이중으로 직렬화된 JSON이므로 다시 파싱
    const parsedMessage = JSON.parse(message.message);
    
    setMessages((prevMessages) => [...prevMessages, parsedMessage]);
    
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
        <button onClick={connect}>Connect</button>
        <button onClick={disconnect}>Disconnect</button>
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
            {message.content}
          </div>
        ))}
      </div>
    </div>
  );
};

export default ChatRoom;
