# Zoomy Library [![API](https://img.shields.io/badge/API-23%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=23) [![JitPack](https://jitpack.io/v/moisoni97/Zoomy.svg)](https://jitpack.io/#moisoni97/Zoomy)
Zoomy is an easy to use pinch-to-zoom library for Android.

![alt tag](art/zoomy-sample.gif)


# Getting Started

* You project should build against Android 6.0 (minSdk 23).

* Add the JitPack repository to your project's build.gradle file:

```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

* Add the dependency in your app's build.gradle file:

```gradle
dependencies {
    implementation 'com.github.moisoni97:Zoomy:3.0.0'
}
```

# Usage

Register the `view` you want to be zoomable:

```java

Zoomy.Builder builder = new Zoomy.Builder(this).target(imageView);
builder.register();
```

Unregister the `view`:

```java
Zoomy.unregister(imageView);
```

That's all. Now your views can be pinch-zoomed!

# Customization

* Use ZoomyConfig to change default configuration flags:

```java
ZoomyConfig config = new ZoomyConfig();
config.setZoomAnimationEnabled(false); //enables zoom out animation when view is released
config.setImmersiveModeEnabled(false); //enables immersive mode when zooming a view

Zoomy.setDefaultConfig(config); //set the configuration across all `Zoomy` registered views
```

* Zoomy Builder also allows some customization:

```java
Zoomy.Builder builder = new Zoomy.Builder(this)
                    .target(imageView)
                    .enableImmersiveMode(true)
                    .enableShadow(true)
                    .animateZooming(false);
```

* Add callbacks to listen for specific events:

```java
Zoomy.Builder builder = new Zoomy.Builder(this)
                    .target(imageView)
                    .tapListener(new TapListener() {
                        @Override
                        public void onTap(View v) {
                            //view tapped, do stuff
                        }
                    })
                     .longPressListener(new LongPressListener() {
                        @Override
                        public void onLongPress(View v) {
                            //view long pressed, do stuff
                        }
                    }).doubleTapListener(new DoubleTapListener() {
                        @Override
                        public void onDoubleTap(View v) {
                            //view double tapped, do stuff
                        }
                    })
                    .zoomListener(new ZoomListener() {
                        @Override
                        public void onViewStartedZooming(View view) {
                            //view started zooming
                        }

                        @Override
                        public void onViewEndedZooming(View view) {
                            //view ended zooming
                        }
                    });
```

* Change the interpolator used to animate ending zoom event:

```java
Zoomy.Builder builder = new Zoomy.Builder(this)
                    .target(imageView)
                    .interpolator(new OvershootInterpolator());
```

License
=======

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.