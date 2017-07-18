function VideoBridge() {
    var context = window.context;
    if (typeof context === "undefined" && typeof FakeVideoContext !== "undefined") {
        context = new FakeVideoContext();
    }

    return {
        play: function (videoName) {
            context.play(videoName);
        }
    };
}

function FakeVideoContext() {
    return {
        play: function (videoName) {
            alert("Video to play '" + videoName + "'");
        }
    }
}
