const timers = [];

// every page, after loaded, creates a websocket connection to the server under the ws protocol
let ws = new WebSocket('ws://localhost:8765')

// no matter who sent the message, just display it
ws.onmessage = async (msg) => {
    const dm = createDanmaku(msg.data);
    addInterval(dm);
}

$(".send").on("click", async () => {
    const danmaku = document.getElementById('danmakutext').value;
    if (danmaku === '') {
        alert('Blank danmaku is invalid')
        return;
    }
    document.getElementById('danmakutext').value = '';  // clean the textfield
    // we do not need to display the danmaku immediately, since the delay of websocket's broadcast is small enough that we can just display the message on receive
    await ws.send(danmaku);
});

// create a Dom object corresponding to a danmaku
function createDanmaku(text) {
    const jqueryDom = $("<div class='bullet'>" + text + "</div>");
    const fontColor = "rgb(255,255,255)";
    const fontSize = "20px";
    let top = Math.floor(Math.random() * 400) + "px";
    const left = $(".screen_container").width() + "px";
    jqueryDom.css({
        "position": 'absolute',
        "color": fontColor,
        "font-size": fontSize,
        "left": left,
        "top": top,
    });
    $(".screen_container").append(jqueryDom);
    return jqueryDom;
}

// add timer task to let the danmaku fly from right to left
function addInterval(jqueryDom) {
    let left = jqueryDom.offset().left - $(".screen_container").offset().left;
    const timer = setInterval(function () {
        left--;
        jqueryDom.css("left", left + "px");
        if (jqueryDom.offset().left + jqueryDom.width() < $(".screen_container").offset().left) {
            jqueryDom.remove();
            clearInterval(timer);
        }
    }, 5); // set delay as 5ms,which means the danmaku changes its position every 5ms
    timers.push(timer);
}
