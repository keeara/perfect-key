# Third-party notices

Perfect Key is a modified fork of [HeliBoard](https://github.com/HeliBorg/HeliBoard) (GPL-3.0), itself based on
[OpenBoard](https://github.com/openboard-team/openboard) and the Android Open Source Project (AOSP) LatinIME keyboard (Apache-2.0).
Perfect Key as a whole is distributed under the GNU General Public License v3.0 (see [LICENSE](LICENSE)).
Third-party components keep their own licences, listed below. Original copyright and licence notices in the source files are preserved.

This is an independent project and is not affiliated with or endorsed by the upstream maintainers or by Google.

## Code

| Component | Licence | Notes |
|---|---|---|
| HeliBoard (Helium314 and contributors) | GPL-3.0 | [LICENSE](LICENSE) |
| OpenBoard | GPL-3.0 | upstream of the project above |
| AOSP LatinIME keyboard | Apache-2.0 | [LICENSE-Apache-2.0](LICENSE-Apache-2.0) |
| Native dictionary code (`app/src/main/jni`) | Apache-2.0 | from AOSP |
| FlorisBoard keyboard layout parser (`keyboard_parser/floris`) | Apache-2.0 | Copyright 2021 Patrick Goldinger, file headers kept |
| Material Symbols / Material Icons drawables (Google) | Apache-2.0 | many drawables in `res/drawable`, some modified |
| Pictogrammers Material Design Icons | Apache-2.0 | some upstream drawables |
| Icons8 drawables (`sym_keyboard_*_holo`, `ic_settings_about_github`) | as tagged upstream (Apache-2.0) | carried over from the upstream project |
| Bengali Khipro input method `bn-khipro.mim` | MIT | Copyright 2024 rank_coder, notice in the file |
| AndroidX libraries (core-ktx, recyclerview, autofill, viewpager2, compose, material3, navigation) | Apache-2.0 | Gradle dependencies |
| Android desugar_jdk_libs (core library desugaring) | GPL-2.0 with Classpath Exception | build-time Gradle dependency |
| kotlinx.serialization | Apache-2.0 | Gradle dependency |
| sh.calvin.reorderable | Apache-2.0 | Gradle dependency |
| com.github.skydoves:colorpicker-compose | Apache-2.0 | Gradle dependency |

## Font

- **Inter** (`app/src/main/res/font/inter_regular.ttf`), Copyright (c) 2016 The Inter Project Authors, SIL Open Font License 1.1.
  Full text: [INTER_LICENSE.txt](INTER_LICENSE.txt).

## Emoji search keywords (added in this fork)

`app/src/main/assets/emoji_search/emoji_{en,it,es}.tsv` are generated from [Emojibase](https://github.com/milesj/emojibase) data
(`emojibase-data`, MIT), which in turn is derived from the Unicode CLDR annotations (Unicode License v3).

### Emojibase (MIT)

```
MIT License

Copyright (c) 2017-2019 Miles Johnson

Permission is hereby granted, free of charge, to any person obtaining a
copy of this software and associated documentation files (the "Software"),
to deal in the Software without restriction, including without limitation
the rights to use, copy, modify, merge, publish, distribute, sublicense,
and/or sell copies of the Software, and to permit persons to whom the
Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
DEALINGS IN THE SOFTWARE.
```

### Unicode CLDR data (Unicode License v3)

```
UNICODE LICENSE V3

COPYRIGHT AND PERMISSION NOTICE

Copyright © 1991-2026 Unicode, Inc.

NOTICE TO USER: Carefully read the following legal agreement. BY DOWNLOADING, INSTALLING, COPYING OR OTHERWISE USING DATA FILES, AND/OR SOFTWARE, YOU UNEQUIVOCALLY ACCEPT, AND AGREE TO BE BOUND BY, ALL OF THE TERMS AND CONDITIONS OF THIS AGREEMENT. IF YOU DO NOT AGREE, DO NOT DOWNLOAD, INSTALL, COPY, DISTRIBUTE OR USE THE DATA FILES OR SOFTWARE.

Permission is hereby granted, free of charge, to any person obtaining a copy of data files and any associated documentation (the "Data Files") or software and any associated documentation (the "Software") to deal in the Data Files or Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, and/or sell copies of the Data Files or Software, and to permit persons to whom the Data Files or Software are furnished to do so, provided that either (a) this copyright and permission notice appear with all copies of the Data Files or Software, or (b) this copyright and permission notice appear in associated Documentation.

THE DATA FILES AND SOFTWARE ARE PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT OF THIRD PARTY RIGHTS.

IN NO EVENT SHALL THE COPYRIGHT HOLDER OR HOLDERS INCLUDED IN THIS NOTICE BE LIABLE FOR ANY CLAIM, OR ANY SPECIAL INDIRECT OR CONSEQUENTIAL DAMAGES, OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THE DATA FILES OR SOFTWARE.

Except as contained in this notice, the name of a copyright holder shall not be used in advertising or otherwise to promote the sale, use or other dealings in these Data Files or Software without prior written authorization of the copyright holder.
```

## Dictionaries

- `app/src/main/assets/dicts/main_{en-US,en-GB,it,es}.dict` are built by the [aosp-dictionaries](https://codeberg.org/Helium314/aosp-dictionaries) project
  from the wordlists of OpenBoard 1.4.5, which come from the Android Open Source Project (Apache-2.0, see [LICENSE-Apache-2.0](LICENSE-Apache-2.0)).
  The per-file source information is in that repository (`wordlists/main_*.source`); it was not independently re-verified here.
  Dictionaries for other languages (with other licences) are not included.
- `app/src/main/assets/emoji_dicts/emoji_en.dict` is the English emoji dictionary from the same project. Its source is documented there as
  "Unicode License v3 (CLDR) and AGPL-3.0 (Signal)". The Unicode text is above, and the AGPL-3.0 text is in [LICENSE-AGPL-3.0](LICENSE-AGPL-3.0). The Signal-derived word data is AGPL-3.0
  ([Signal-Android](https://github.com/signalapp/Signal-Android)); GPL-3.0 section 13 explicitly allows combining with AGPL-3.0 works,
  and the corresponding source is this repository.

## Graphics

- The app icon is an original design made for Perfect Key. The upstream icon (CC BY-SA 4.0) and its source files are not included.
