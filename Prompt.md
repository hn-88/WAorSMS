Private URL for my reference - https://claude.ai/chat/1c1b5a36-f1db-411e-bf59-d615ba1c24d2

Initially, I asked Claude.ai (Sonnet 5 medium model, free web-based)
```
Please write code for an Android app which will

1. auto-answer phone calls with a pre-recorded 
audio message which asks the user to press 1, 2 or 3 or 9
2. ring the phone if 9 is chosen, so that 
the phone can be picked up and the call answered by a human 
3. play separate pre-recorded audio for 1, 2 and 3,
following which send separate SMS messages to the caller
based on whether 1, 2 or 3 was chosen.
```
Claude explained that Android apps don't get access to voice, so we could choose either

_Option A — Cloud telephony (Twilio Voice, etc.)_

_Option B — Native on-device Android app_\
_Can auto-answer calls (ANSWER_PHONE_CALLS permission, Android 8+) and can auto-reject/screen calls_

Then, I asked, 
```
How about a semi automated system - I can copy a phone number 
to the Android phone clipboard. 
Is there a home screen shortcut or some app which can

1. take the phone number from the clipboard
2. check if that number has an associated whatsapp account
3. If whatsapp account exists, send a preformatted
whatsapp message to that whatsapp account
4. if no whatsapp account, send as sms instead.
```
and Claude immediately wrote out the code for such an app! Then, I prompted,
```
1. Please modify it so that it has three buttons instead - 

"Send Telugu" "Send HIndi" and "Send English" - 
which will have 3 separate strings - I will edit 
the separate strings manually for each.

2. Instead of separate buttons for send whatsapp and send sms,
can it do both with one button press, send whatsapp and SMS, one after the other?
3. I would like to build this on github actions instead of locally.
Please write the workflow needed to build this.
```
And Claude generated the contents of this repo. 

I asked Gemini via aistudio.google.com to fix the error for the first build,
```
I have an android app to be built like this on github actions,
(copy-pasted the workflow yml here)
I get this error,

/home/runner/work/_temp/c49dead5-7c48-4584-9dc5-634857b1468c.sh: 
line 1: sdkmanager: command not found
yes: standard output: Broken pipe
/home/runner/work/_temp/c49dead5-7c48-4584-9dc5-634857b1468c.sh: 
line 2: sdkmanager: command not found
Error: Process completed with exit code 127.

I have been able to build other android apps using just
npx ionic cordova build android --release -- -- --packageType=apk

So I guess the sdk etc are already installed on the ubuntu github runner.
Please guide on how I should edit the workflow which gives the error.
```
and Gemini correctly asked me to add `setup-android`

_The error happens because while ubuntu-latest has parts of the Android SDK preinstalled, it does not include the sdkmanager binary in the system's PATH by default[1]. When Ionic/Cordova builds apps, it handles the SDK setup and environment variables under the hood, which is why your Ionic workflows worked smoothly without manually calling sdkmanager.
To fix this, you can use a community-maintained GitHub Action called android-actions/setup-android[2]. It automatically locates the preinstalled SDK, configures the environmental variables (ANDROID_HOME, ANDROID_SDK_ROOT), adds tools like sdkmanager to your PATH, and installs any required packages[2]._

And this resulted in the working app.

**Note on usage** - WhatsApp comes up first. If sent via WhatsApp, we can go to the default SMS messaging app and delete the draft which would have been created there. Or send the draft.
