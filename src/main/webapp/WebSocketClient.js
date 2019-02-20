var controlSocket, streamSocket;
var paused = false;

window.onload = function () {
    draw(0);
    openControlConnection();
}

function openControlConnection() {
    controlSocket = new WebSocket("ws://localhost:8080/WebsocketDemo/control");
    controlSocket.onopen = function(event) { console.log("Control connection established."); };
    controlSocket.onclose = function(event) { console.log("Control connection closed."); };
    controlSocket.onmessage = function(event) { onControlMessage(event); };
}

function onControlMessage(event) {
    var message = JSON.parse(event.data);
    switch (message.type) {
        case "pairing":
            openStreamConnection(message.spid);
            break;
        default:
            break;
    }
}

function openStreamConnection(spid) {
    streamSocket = new WebSocket("ws://localhost:8080/WebsocketDemo/stream?spid=" + spid);
    streamSocket.onopen = function(event) { console.log("Stream connection established."); };
    streamSocket.onclose = function(event) { console.log("Stream connection closed."); };
    streamSocket.onmessage = function(event) { processStream(event.data); };
}

function processStream(data) {
    draw(data);
}

function toggleStream() {
    if (paused) {
        resumeStream();
        document.getElementById("toggleButton").value = "Pause";
        paused = false;
    }
    else {
        pauseStream();
        document.getElementById("toggleButton").value = "Resume";
        paused = true;
    }
}

function pauseStream() {
    controlSocket.send(JSON.stringify({
        type: "control",
        request: "pause"
    }));
}

function resumeStream() {
    controlSocket.send(JSON.stringify({
        type: "control",
        request: "resume"
    }));
}

function draw(fill) {
    var canvas = document.getElementById("progressBar");
    var ctx = canvas.getContext("2d");
    ctx.clearRect(0, 0, canvas.width, canvas.height)
    ctx.lineWidth = 20;
    ctx.strokeStyle = "#BFBFBF";
    ctx.beginPath();
    ctx.arc(75, 75, 50, 0, 2 * Math.PI);
    ctx.stroke();
    ctx.strokeStyle = "#474747"
    ctx.beginPath();
    ctx.arc(75, 75, 50, -Math.PI/2, -Math.PI/2 + (fill * 2) * Math.PI);
    ctx.stroke();
    ctx.font = "25px Verdana";
    ctx.fillText((fill*100).toFixed(0) + "%", 50, 85)
}