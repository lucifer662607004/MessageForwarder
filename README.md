# Message Forwarder

This Android app can transfer local SMS to your iOS device automatically via Bark, or any other platforms that Bark supports. [What is Bark?](https://github.com/Finb/Bark)

## Does it support my phone?

1. Tested with pure Google One Android 10 ROM on a Nokia 7 plus, it works perfectly. 
2. It should work on higher Android version (Test required).
3. To work on lower Android version, modify the 'minSdk' statement on gradle file and compile it again.
4. If you're using third-party/modified ROM, you're on your own now. Good luck with it.

## Compiling

Import the project with IDEA or Android Studio and click 'run'

## Tips

If you intent to use self-deploy Bark server or other http server, remove the verification of "https://api.day.app/" in the code and run again.