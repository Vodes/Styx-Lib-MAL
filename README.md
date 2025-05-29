## Styx-Lib-MAL

A multiplatform MyAnimeList API Wrapper/Toolkit to use in both the styx clients and serverside applications.

This approach is necessary due to MAL requiring the client-id and client-secret to be sent when refreshing the access tokens.<br>
And from what I know these should never be accessible to the user.

### Installation

Like most of my other libraries, this is only available on my own repo.

- Add the repos to wherever you want
    ```kt
    repositories {
        // ...
        maven("https://repo.styx.moe/releases")
        maven("https://repo.styx.moe/snapshots")
    }
    ```
- Grab the artifacts or dependency declarations [from here](https://repo.styx.moe/#/releases/moe/styx/styx-lib-mal).
