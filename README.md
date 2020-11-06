# UPDATE AVAILABLE
| Download | Vulnerabilities | Hits |
|-------| --------------- | ---------|
| [![latest release](https://img.shields.io/badge/download-v2.3.1-blue.svg)](https://github.com/omegas82128/Aikino/releases/download/v2.3.1/Aikino.Setup.v2.3.1.exe) | [![Known Vulnerabilities](https://snyk.io/test/github/omegas82128/Aikino/badge.svg?targetFile=pom.xml)](https://snyk.io/test/github/omegas82128/Aikino?targetFile=pom.xml) | ![Hits](https://hitcounter.pythonanywhere.com/count/tag.svg?url=https%3A%2F%2Fgithub.com%2Fomegas82128%2FAikino) |
# ![app icon](./.github/readme-images/app-icon.png) Aikino
Aikino is an open source windows application that downloads movie, anime and tv show posters, converts them to icons and applies them.

![app-demo](./.github/readme-images/demo.gif)
## Features

Features include:
* Browsing posters of Movies, Tv Shows and Anime.
* Downloading those posters.
* Creating icons from posters and applying created icons.
* Icons can be of different types ([With Template](https://www.deviantart.com/musacakir/art/Movie-And-TV-Show-DVD-Folder-Icon-Template-469935243) or Simple)
* 'Open with Aikino' and 'Refresh Folder Icon' are added to windows *right-click menu* for folders on install.
 * Clicking 'Open with Aikino' will open Aikino for the selected folder.
 * Create icons from posters you have downloaded. (Posters need to be in the folder)

## FAQ
#### What folders will the app work on?
* For **movie folders** that have the correct year of release in name. 

*For example:*
```
Doctor.Sleep.2019.DC.1080p.BluRay.x265-RARBG
Magnolia (1999)
```
* For **Tv Series folders** that either mention season number or have name followed by a Mini-Series tag.

*For example:*
```
Twin Peaks Season 2
Devs (Mini-Series)
```
* For **anime folders** that either have season number or have an anime, sub, dub or dual-audio tag.

*For example:*
```
Monster (anime)
Legend of Galactic Heroes (Sub)
Tatami Galaxy Season 1
Death Note (Dual-Audio)
```

#### I got the notification that icon was applied. Why isn't the icon displayed on the folder yet?
Windows File Explorer updates on its own pace. It can apply the icon in a few seconds to about a minute. 

If it does not update till then close the application. Icon should be applied in 30 seconds. 

If the icon is still not applied, use 'Refresh Folder Icon' in the windows right-click menu on the folder in question.

#### I changed poster size from settings. Why is the downloaded poster of previous size?
The change takes effect on application restart. So when you restart the application, Aikino will download the poster in the new size. 

#### Why doesn't my movie/tv show/anime have more/any posters?
All posters are provided by [TMDb](https://www.themoviedb.org/). If you have a poster that you want to add, and it follows TMDb guidelines, you can upload on TMDb and access it through Aikino.

#### Can I use Aikino to create and apply an icon from a poster that I have?
Yes you can. Just turn on Local Posters feature from settings. After that posters in the folder you opened will display, along with posters provided by the app, for icon creation. (It is turned on by default)

## Credits
Aikino uses the following APIs:
* **imcdonagh** - [image4j](https://github.com/imcdonagh/image4j)
* **Ivan Szkiba**- [ini4j](http://ini4j.sourceforge.net/)
* **divijbindlish** - [parse-torrent-name](https://github.com/divijbindlish/parse-torrent-name)
* **G00fY2** - [version-compare](https://github.com/G00fY2/version-compare)
* **aoudiamoncef** [apollo-client-maven-plugin](https://github.com/aoudiamoncef/apollo-client-maven-plugin
* **jfoenixadmin** [JFoenix](https://github.com/jfoenixadmin/JFoenix)
* <img src="https://www.themoviedb.org/assets/2/v4/logos/v2/blue_short-8e7b30f73a4020692ccca9c88bafe5dcb6f8a62a4c6bc55cd9ba82bb2cd95f6c.svg" width="80"> - **holgerbrandl** - [TheMovieDB API](https://github.com/holgerbrandl/themoviedbapi) 

## License

    Copyright 2020 Muhammad Haris

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


## Disclaimer
**Aikino uses the TMDb API, but it is not endorsed or certified by [TMDb](https://www.themoviedb.org/).**
