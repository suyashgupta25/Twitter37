# Twitter37

This project consumes twitter APIs and displays latest tweets for screen name tretton37.

## Architecture
Project is written in Kotlin and uses MVVM design pattern with android architectural components. The latest android arch paging library is used to achieve the desired tweets list on home page. Android studio v3.1.4 used for development.

## Libraries
### app:
1) Support Libs: AppCompat, recyclerview, design, support-annotations are used to provide backward support.
2) Kotlin : I used Kotlin as a language.
3) Arch Lifecycle: lifecycle extensions and paging library used for tweets page list like structure.
4) Dependency Injection: Dagger2 for managing dependencies.
5) Image downloading: Glide
6) Twitter native SDK used which has Retrofit in-built in it
7) Crashlytics is used for Crash tracking purpose.

### test:
1) Junit & Robolectric: Used to write unit test cases.
2) Mocking: used mockito-kotlin

### androidTest
1) Espresso: core, rules and runner are used for the UITests.


## Information:
When You launch Twitter37 app it will show you the latest tweets of the tretton37 in a page-list pattern and if tweet contains an image then on click of it you can see the full screen image with zoom support(draggable). Also the home screen has tweets search support which stores the history in the local sqlite db.

I tried initially the raw twitter API (Postman) and got success using auth-1 process. later I came across the official SDK of twitter which has good support of all available APIs and it has retrofit in-built. So I integrated it here.

Android arch paging support used for app making, by default it fetches 3 page in the start with page size being 10. Later on scroll it manages all the later calls by itself in parallel updating the UI.

For displaying the image on next screen I used a customized ImageView which on the bases of gesture scales the image.

I tried not to over-engineer the project and used a limited set of suitable libraries. If in-case we need to execute some external APIs I would suggest to use RxJava/RxAndroid framework to manage asynchronous tasks.

Happy Coding!
