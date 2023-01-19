import asyncio
import websockets


class DanmakuServer:
    def __init__(self):
        """Set up a list of the connecting clients, which is required by websockets.broadcast"""
        self.clients = []

    async def reply(self, websocket):
        """Broadcasts any message from any connected client to all clients.

        When a new client is connected, websockets.serve will call a new reply, where the parameter 'websocket'
        is the socket to the new connection. This function continuous running (until exception),
        if the client doesn't send a message, the 'async for' keyword for will keep blocking

        which is similar to:
            while True:
                msg = await websocket.recv()
                      ~~~~~ blocking

        once a client sends a message, the corresponding 'reply' method's will be unblocked for 1+ iterations
        that each iteration it takes one message out from the sent messages.
        then it broadcasts the message to the list of connecting clients.
        """
        self.clients.append(websocket)
        try:
            async for msg in websocket:
                websockets.broadcast(self.clients, msg)
        # such as webpage no resp or timeout, the websocket client will close the connection
        # we no longer need to run this function, since no more message is possible to be sent by this websocket
        # we can also remove it from the broadcast targets' list, since it can no longer receive messages
        except websockets.ConnectionClosed:
            self.clients.remove(websocket)


if __name__ == "__main__":
    server = DanmakuServer()
    asyncio.get_event_loop().run_until_complete(websockets.serve(server.reply, 'localhost', 8765))
    asyncio.get_event_loop().run_forever()
