# IntelliQ.me
[IntelliQ.me](https://intelliq.me/) is a **smart system to manage waiting queues** and offers estimations about remaining waiting time. We think **no one should waste lifetime while waiting**. It doesn't matter where you are - you should be able to use the time until it's your turn effectively.

## Contribute
We need your help, feel free to [get in touch](https://intelliq.me/imprint/)! Please check out our [project backlog](https://github.com/IntelliQ/Android/projects/) to find out what we're currently working on. Reporting [issues](https://github.com/IntelliQ/Android/issues) also helps a lot, even if they are [easy to fix](https://github.com/IntelliQ/Android/issues?q=is%3Aissue+is%3Aopen+label%3Aeasy). 

## Android App
The [mobile](https://github.com/IntelliQ/Android/tree/master/IntelliQ/mobile/src/main/java/com/steppschuh/intelliq) module contains the sources of the app for Android phones & tablets. The [wear](https://github.com/IntelliQ/Android/tree/master/IntelliQ/wear/src/main/java/com/steppschuh/intelliq) module contains the sources of the app for Android wear devices (not available yet).

## Backend
The [backend](https://github.com/IntelliQ/Android/tree/master/IntelliQ/backend/src/main/java/com/intelliq/appengine) module contains the sources for the Google App Engine **Java** project, available through the GCP at [intelliq-me.appspot.com](https://intelliq-me.appspot.com/).

### API
The sources for the API endpoints which the Android, iOS and Web apps use can be found [here](https://github.com/IntelliQ/Android/tree/master/IntelliQ/backend/src/main/java/com/intelliq/appengine/api/endpoint). The documentation for these endpoints is still work in progress.

### Websites
The sources for all the websites that the backend serves can be found [here](https://github.com/IntelliQ/Android/tree/master/IntelliQ/backend/src/main/webapp). This includes the [landing page](https://intelliq.me/), the [web app](https://intelliq.me/apps/web/) and the [management console](https://intelliq.me/manage/). All these use **vanilla HTML5, CSS3 and JS**.
