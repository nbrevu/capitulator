# capitulator
Yet another simple UI for youtube-dl, specialised in multi-chapter videos.

First of all, a disclaimer: this is a **personal** project, and I'm only making the code public because someone might find it useful.

I'm creating this project so that I can download multi-chapter music compilations from Youtube effortessly and mostly automatically, in such a way that each song goes in its own mp3 file and I can easily add to my winamp/AIMP playlists. Yeah, as sad or as funny as it sounds. This means:

- The features are designed to be useful to me, in particular to a niche use case.
- It's very lacking in features compared to the full youtube-dl. This is expected. Youtube-dl is a far more complex project than this.
- This is intended to be a one week (or less) project, and therefore I'm not going to bother with even unit tests.
- There is a chance that someone might find this useful, but lacking some feature. I encourage you to fork the code and add whatever you like, but I'm not likely to accept pull requests (sorry if I'm sounding like a jerk. It's just that I'm likely to not even notice that someone opened the pull request).

This project is based on [youtube-dl](https://github.com/ytdl-org/youtube-dl) and [FFmpeg](https://ffmpeg.org/). These pieces of software are not included in this repository. I'm also, additionally, planning to use it along with the [Open With](https://addons.mozilla.org/en-US/firefox/addon/open-with/) Firefox extension, so that I can easily invoke it on any youtube video I happen to be watching; but having this extension is not strictly needed to run this software, whereas youtube-dl and ffmpeg are mandatory.

Yes, the title is a very stupid bilingual joke. I'm sorry.

Usage:
- Install youtube-dl and ffmpeg somewhere on your PC and note the installation folders. I think that youtube-dl already brings an ffmpeg, but if it doesn't, you will need to install it separately.
- Compile the application with Java 12 or later, and Maven.
- Run the SetupApp class in order to configure the application. Chances are that the paths will not be suited to your machine. I suggest that as the temporary folder you just use a folder named "tmp" inside of the folder where you downloaded the code.
- I used the Motif look and feel by default because I like my software to look older than the Sun, but you probably want to use a different one. Motif has the crippling problem of not supporting copying, pasting and undoing. I encourage you to use Windows Classic for a still reasonably pleasant experience, free from the evils of flat design and other PC-unfriendly trends.
- Run the CapitulatorApp class passing the youtube URL as argument. If the video already includes chapters, the main GUI will appear and you will be able to choose which chapters do you want to keep, where to store the files, which file names to use and so on. If the video doesn't support chapters, but someone in the comments has added them, you will be able to add them in the GUI that will appears when no chapters are detected.
- Enjoy having the face of Anote Tong in your taskbar while the download happens.

Known shortcomings:
- Despite the name, youtube-dl supports many more video services; however, this application is 100% tailored to youtube. I'm assuming that there is no way this will work with other video locations. Maybe some json tags are common enough that it works, but I wouldn't bet on it.
- My goal was to be able to use Firefox's Open With extension with this application. It worked, but I had to do some additional operations not included in the code itself. Namely, after compiling I copied the jar and all the dependencies (plus the config.properties file) into a folder, and created two bat files calling my Java 12 virtual machine including all the jars in the classpath. One of the bat files runs the CapitulatorApp class, which is the main one, and another one runs the SetupApp class. The bat file for CapitulatorApp takes one argument, which is the youtube URL.
- The output of ffmpeg seems to be kind of weird. As far as audio files go, they work... but Winamp can't read the tags (VLC and AIMP do, though), and the song length is sometimes read as twice the original (like there are two streams instead of one? Not sure). The tags are definitely an ffmpeg issue (I tried ID3v1 tags as well, but they didn't work either), but the length might be related to some oddity in the youtube video format.
- In general, YMMV. It worked for me, but the total amount of computers I've run this code so far is 1.