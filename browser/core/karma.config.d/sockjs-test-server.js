process.env.CHROME_BIN = require('puppeteer').executablePath();

const http = require('http');
const sockjs = require('sockjs');

const simpleString = 'simple-string-message'

const callConsumer = function (conn, vertxMessage) {
    const msg = {
        type: 'send',
        address: vertxMessage.address,
        body: simpleString
    };
    conn.write(JSON.stringify(msg));
};

const callConsumerComplexType = function (conn, vertxMessage) {
    const msg = {
        type: 'send',
        address: vertxMessage.address,
        body: {'field': simpleString}
    };
    conn.write(JSON.stringify(msg));
};

const callConsumerWithHeaders = function (conn, vertxMessage) {
    const msg = {
        type: 'send',
        address: vertxMessage.address,
        headers: {'header-key': 'header-value'},
        body: simpleString
    };
    conn.write(JSON.stringify(msg));
};

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

const replyRecipientFailure = function (conn, vertxMessage) {
    const msg = {
        type: 'err',
        address: vertxMessage.replyAddress,
        failureCode: 1000,
        failureType: 'RECIPIENT_FAILURE',
        message: 'Server side did fail'
    };
    conn.write(JSON.stringify(msg));
};

const replyNoHandlerFailure = function (conn, vertxMessage) {
    const msg = {
        type: 'err',
        address: vertxMessage.replyAddress,
        failureCode: -1,
        failureType: 'NO_HANDLER',
        message: 'No Handler there'
    };
    conn.write(JSON.stringify(msg));
};

const replyTimeoutFailure = function (conn, vertxMessage) {
    const msg = {
        type: 'err',
        address: vertxMessage.replyAddress,
        failureCode: -1,
        failureType: 'TIMEOUT',
        message: 'Server side did run into timeout'
    };
    conn.write(JSON.stringify(msg));
};

const ackPublish = function (conn, vertxMessage) {
    const msg = {
        type: 'send',
        address: '/publish-ack',
        body: vertxMessage.body
    };
    conn.write(JSON.stringify(msg));
};

const ackSend = function (conn, vertxMessage) {
    const msg = {
        type: 'send',
        address: '/send-ack',
        body: vertxMessage.body
    };
    conn.write(JSON.stringify(msg));
};

const ackUnregister = function (conn, vertxMessage) {
    const msg = {
        type: 'send',
        address: '/unregister-ack',
        body: vertxMessage.address
    };
    conn.write(JSON.stringify(msg));
};

const socketJsServer = sockjs.createServer({sockjs_url: 'http://cdn.jsdelivr.net/sockjs/1.0.1/sockjs.min.js'});
socketJsServer.on('connection', function (conn) {
    conn.on('data', function (message) {
        const vertxMessage = JSON.parse(message);
        const replyOnReplyAddress = '/reply-on-reply-address';

        if (vertxMessage.type === 'register') {

            if (vertxMessage.address === '/consumer') {
                callConsumer(conn, vertxMessage)
            }

            if (vertxMessage.address === '/consumer-complex-type') {
                callConsumerComplexType(conn, vertxMessage)
            }

            if (vertxMessage.address === '/consumer-with-headers') {
                callConsumerWithHeaders(conn, vertxMessage)
            }

        } else if (vertxMessage.type === 'send') {

            if (vertxMessage.address === '/request') {
                replySameData(conn, vertxMessage)
            }
            if (vertxMessage.address === '/request-recipient-failure') {
                replyRecipientFailure(conn, vertxMessage)
            }
            if (vertxMessage.address === '/request-no-handler-failure') {
                replyNoHandlerFailure(conn, vertxMessage)
            }
            if (vertxMessage.address === '/request-timeout-failure') {
                replyTimeoutFailure(conn, vertxMessage)
            }

            if (vertxMessage.address === '/reply-expected') {
                replySameData(conn, vertxMessage, replyOnReplyAddress)
            }

            if (vertxMessage.address === replyOnReplyAddress) {
                replySameData(conn, vertxMessage)
            }

            if (vertxMessage.address === '/send') {
                ackSend(conn, vertxMessage)
            }

            if (vertxMessage.address === '/send') {
                ackSend(conn, vertxMessage)
            }
        } else if (vertxMessage.type === 'publish') {
            ackPublish(conn, vertxMessage)
        } else if (vertxMessage.type === 'unregister') {
            ackUnregister(conn, vertxMessage)
        }
    });
});

const server = http.createServer();
socketJsServer.installHandlers(server, {prefix: '/eventbus'});
server.listen(9999, '0.0.0.0');
