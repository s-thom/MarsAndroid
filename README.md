# Mars (Android)

Mars is an Android app for sending URLs to any Windows 10 device using Microsoft's "Project Rome".

*This was a weekend project, and is given as-is with no guarantee of any updates. I will not be providing support for this app.*

Sign in with your Microsoft account, and see active devices you have signed in on. In another app, your borwser for example, share a URL to Mars and it will appear on the device you choose.

Mars also supports the "Direct Share" capability on Android Nougat and higher. This allows you to share directly to the device from the share menu, without having to bring up the device selection dialog.

**I wrote a little bit on [my website](https://sthom.kiwi/projects/mars/) about the creation of this app. If you're interested, give it a read.**

If you are going to build this app yourself, you will need to add a `secrets.properties` into the root directory of the project.

```
MS_CLIENT_ID=<Client ID given by Microsoft>
# If you don't need to sign your build, leave out these entries
# and comment out the signing in `app/build.gradle`
SIGN_KEY_PASSWORD=<Key for signing>
SIGN_STORE_PASSWORD=<Key for keystore>
SIGN_STORE_PATH=<Path to the keystore>
SIGN_KEY_ALIAS=<Alias for the key>
```
