process.env.CHROME_BIN = require('puppeteer').executablePath();

const http = require('http');
const sockjs = require('sockjs');

const replySameData = function (conn, vertxMessage, replyAddress) {
    const msg = {
        type: 'send',
        address: vertxMessage.replyAddress,
        replyAddress: replyAddress,
        headers: vertxMessage.headers,
        body: vertxMessage.body
    };
    conn.write(JSON.stringify(msg));
};

const socketJsServer = sockjs.createServer({sockjs_url: 'http://cdn.jsdelivr.net/sockjs/1.0.1/sockjs.min.js'});
socketJsServer.on('connection', function (conn) {
    conn.on('data', function (message) {
        const vertxMessage = JSON.parse(message);

        if (vertxMessage.type === 'send') {
            if (vertxMessage.address === '/request') {
                replySameData(conn, vertxMessage)
            }
        }
    });
});

const server = http.createServer();
socketJsServer.installHandlers(server, {prefix: '/eventbus'});
server.listen(9999, '0.0.0.0');
