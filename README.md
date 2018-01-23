# AppAnalyzer
[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)

An Android library for listening logs, exceptions and OkHttp's requests.

<p align="center">
<img src="https://github.com/alexlytvynenko/appAnalyzer/blob/master/assets/screenshots.png"/>
</p>

## Getting started

In your `build.gradle`:

```groovy
dependencies {
    debugImplementation 'com.alexlytvynenko.appanalyzer:appanalyzer-core:1.0.1'
    releaseImplementation 'com.alexlytvynenko.appanalyzer:appanalyzer-core-no-op:1.0.1'
}
```

In your `Application` class:

```kotlin
class ExampleApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        AppAnalyzer.install(this)
    }
}
```

**Now you are able to listen a logs and exceptions!** For listening HTTP request and response data go to `OkHttpClient` initialization:

```kotlin
val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(HttpNetworkAnalyzerInterceptor())
            .build()
```

**You're good to go!** AppAnalyzer will automatically create a launcher icon once you run your application.

To disable some of the options, add the following to your `Application` class:

```kotlin
class ExampleApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        AppAnalyzer.disabledLogs(true) // To disable logs listening
                .disabledExceptions(true) // To disable exceptions listening
                .disabledRequests(true) // To disable requests listening
                .install(this)
    }
}
```

Or to disable AppAnalyzer completely: 

```kotlin
class ExampleApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        AppAnalyzer.disabled(true)
                .install(this)
    }
}
```

**Warning**: The intercepted data of requests has the potential to leak sensitive information such as "Authorization" or "Cookie" headers and the contents of request and response bodies. This data should only be listened in a controlled way or in a non-production environment. For release build make sure to use `appanalyzer-core-no-op` dependency:

```groovy
dependencies {
    releaseImplementation 'com.alexlytvynenko.appanalyzer:appanalyzer-core-no-op:1.0.1'
}
```

## Customizing AppAnalyzer

`DisplayAnalyzerActivity` comes with a default icon and label, which you can change by providing `R.drawable.app_analyzer_icon`, `R.drawable.app_analyzer_icon_round` and `R.string.app_analyzer_display_activity_label` in your app:

```
res/
  drawable-hdpi/
    app_analyzer_icon.png
    app_analyzer_icon_round.png
  drawable-mdpi/
    app_analyzer_icon.png
    app_analyzer_icon_round.png
  drawable-xhdpi/
    app_analyzer_icon.png
    app_analyzer_icon_round.png
  drawable-xxhdpi/
    app_analyzer_icon.png
    app_analyzer_icon_round.png
  drawable-xxxhdpi/
    app_analyzer_icon.png
    app_analyzer_icon_round.png
```

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
  <string name="app_analyzer_display_activity_label">MyAppAnalyzer</string>
</resources>
```

## Sample
* Clone the repository and check out the `app-analyzer-sample` module.
* Download a [sample apk](https://raw.githubusercontent.com/alexlytvynenko/appAnalyzer/master/app-analyzer-sample.apk) to check it.


<p align="center">
<img src="https://github.com/alexlytvynenko/appAnalyzer/blob/master/assets/app_analyzer_icon.png" width="250"/>
</p>

## Licence
Copyright 2018 Alexander Lytvynenko

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
