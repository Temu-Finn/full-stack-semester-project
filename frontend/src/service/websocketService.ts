import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

class WebsocketService {
  private stompClient: Client;

  constructor(token: string) {
    this.stompClient = new Client({
      webSocketFactory: () => new SockJS(`http://localhost:8080/ws?token=${token}`),
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      debug: (str) => console.log(str),
      onConnect: (frame) => {
        console.log('Connected: ' + frame);
      }
    });

    this.stompClient.onStompError = (frame) => {
      console.error('STOMP error:', frame.headers['message']);
      console.error('Details:', frame.body);
    };
  }

  public activate () {
    this.stompClient.activate();
  }


  public subscribe(destination: string, callback: (message: any) => void) {
    this.stompClient.subscribe(destination, (message) => {
      callback(JSON.parse(message.body));
    });
  }

  public send(destination: string, payload: any) {
    this.stompClient.publish({
      destination,
      body: JSON.stringify(payload),
    });
  }
}

export default WebsocketService;
