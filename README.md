# AIperLife

Augmented reality social game


## Installation

- manually copy: model.net categories.txt files in /sdcard/neural-nets

- Install Android Studio from: http://developer.android.com/sdk/index.html
- Install latest API through Android Studio. Tutorial: http://developer.android.com/sdk/installing/adding-packages.html
- Open the app folder into Android Studio.
- Compile app.
- Android Studio may or may not give errors. If you see an error, click the "Fix now" link, and then compile again. This may happen a few times.
- Once all the errors have been fixed, then app can be run.



## compile linked libs:

cd ~/FWDNXT/git/AIPerLife/app/src/main

export NDK_TOOLCHAIN_VERSION=4.9

~/Library/Android/sdk/ndk-bundle/ndk-build V=1

