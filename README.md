## ⚠ Deprecated
#### Cause the main website does not exist, So you may know the role. This app does not work properly! Thank you for your under-standing.
##

<!-- PROJECT LOGO -->
<div align="center">
	<img src="https://github.com/MRKaZ/mDisk-Downloader/blob/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" width="120">
</div>

<!-- TITLE --> 
<h1 align="center">mDisk Downloader</h1>
<h4 align="center">An app to download video formatted files from <sup>[mdisk.me]</sup> with beautiful ui.</h4>
<div align="center">
  
  [![App](https://img.shields.io/badge/Download-APK-blue.svg?style=for-the-badge&logo=android)](https://github.com/MRKaZ/mDisk-Downloader/releases/latest)
  [![App](https://img.shields.io/github/v/release/MRKaZ/Crash-Reporter?style=for-the-badge)](https://github.com/MRKaZ/mDisk-Downloader/releases/latest)
  [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg?style=for-the-badge)](https://opensource.org/licenses/Apache-2.0)
</div>

## Features
- Clean, Simple & Beautiful UI.
- Bypassed **DRM** Protection. (Digital Rights Management)
- Please kindly read the **Note** under **Changelog** field.
- Built with **KOTLIN** also Obedient to *(MVVM)* architecture.
- Displaying file **Info** before download
  - *File size*
  - *File duration*
  - *File Resolution*
  - *File Author*
- Check downloaded files list.
- Also can copy direct link to stream without download.

#### Tested with
- Android **S** 12 **(_SDK 31_)**
- Android **R** 11 **(_SDK 30_)**
- Android **Pie** 9 **(_SDK 28_)**

You can test it with other SDK's. Also Bug reports always accept **_Report an issue or Pull me a Request to update the codes snippet_**

<h2 align="center">Preview</h2>
<div align="center">
	<img src="https://github.com/MRKaZ/mDisk-Downloader/blob/master/assets/Preview.gif">
</div>

<h2 align="center">Snapshots</h2>
<div align="center">
	<img src="https://github.com/MRKaZ/mDisk-Downloader/blob/master/assets/snap_1.png" width="220"> <img src="https://github.com/MRKaZ/mDisk-Downloader/blob/master/assets/snap_2.png" width="220"> <img src="https://github.com/MRKaZ/mDisk-Downloader/blob/master/assets/snap_3.png" width="220"> 
</div>
  
## Built with
- [Kotlin](https://kotlinlang.org/) - A modern programming language that makes developers happier.
- [Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html) - For asynchronous, background threads and more...
- [Android Architecture Components](https://developer.android.com/topic/libraries/architecture)
  - [LiveData](https://developer.android.com/topic/libraries/architecture/livedata) - Observable data holder class.
  - [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) - Store and manage UI-related data in a lifecycle conscious way.
- [Retrofit](https://square.github.io/retrofit/) - To manage the process of receiving, sending, and creating HTTP requests and responses.
- [Gson](https://github.com/google/gson) - A Java serialization/deserialization
- [Material Components for Android](https://github.com/material-components/material-components-android) - Material Design UI components for Android.

## Credits
- [Animated Bottom Bar](https://github.com/Droppers/AnimatedBottomBar) - A customizable and easy to use BottomBar navigation view with sleek animations
- [PermissionX](https://github.com/guolindev/PermissionX) - For request android runtime permission
- [Youtube-DL](https://github.com/yausername/youtubedl-android) - Youtube downloader.
- [Rocket .GIF](https://dribbble.com/shots/2200411-Rocket) - Rocket animation

**All libraries' credits go to their respective owners (developers).**

## Changelog
```
[v1.1] [03/10/2022]
+ Bypassed DRM Protection. (Digital Rights Management)
+ Integrated Kotlin DSL. (Groovy to Kotlin DSL)
+ Added foreground service to perform downloads background.
- Removed some functions and features.
• [Dependencies]
+ Added youtubedl-android (youtube-dl) dependency. To bypass the DRM protection. (https://github.com/yausername/youtubedl-android)

[MRKaZ] [Taprobana (LK)]

[v1.0] [06/07/2022]

+ First release!.
+ Simple video downloader from mdisk.me

[MRKaZ] [Taprobana (LK)]
```

## Note
* Some of video files cannot bypass the DRM (Digital Rights Management).
* Some of files are not downloadable. (Download disabled by the file author)
* Also some files are sharing disabled by the file author.
* About the download speed (Bandwidth) its depend on your internet connection and data traffic.

**[DON't ASK OR PULL ISSUE's ABOUT THESE ERROR's]**


## License
##### Distributed under the Apache License. See [`LICENSE`](https://github.com/MRKaZ/mDisk-Downloader/blob/master/LICENSE) for more information.
```
Copyright 2022 Kasun Gamage

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

<!-- ## Disclaimer
```
This repository is for research purposes only, the use of this code is your responsibility.
I take NO responsibility and/or liability for how you choose to use any of the source code
available here. By using any of the files available in this repository,
you understand that you are AGREEING TO USE AT YOUR OWN RISK. Once again,
ALL files available here are for EDUCATION and/or RESEARCH purposes ONLY.
Kasun Gamage (MRKaZ)
``` -->
