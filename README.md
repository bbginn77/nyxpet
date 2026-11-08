# NyxPet

A pocket Siamese for your Android tablet or phone, from the makers of NyxVision and NyxCorp Terminal.
It looks like a 1997 egg-shaped virtual pet ("Seal Point Edition"), with a pixel LCD screen and three buttons.

| Icon | What it does |
|---|---|
| **Feed** | A meal (fills her up; turns into litter clumps later) or a treat (makes her happy, max 3 an hour). She refuses food when full. |
| **Play** | The yarn game: guess left or right, 5 rounds. 3 or more right is a win. |
| **Litter** | Scoops the clumps. Leave them and she gets grumpy. |
| **Pet** | She purrs and hearts float up. Too many pets in a row and she hisses. |
| **Light** | Bedtime is 9 pm to 7 am. Turn the light off so she sleeps well. |
| **Stats** | Age, weight, and heart meters for hunger, happiness, energy and litter. |
| **Call** (bell) | Lights up and beeps when she needs you. |

Nyx keeps living while the app is closed, and she never dies: neglect only makes her sad.
When she is happy, Rex the Frenchie sometimes drops by. (Hint: the logo.)

## Controls
- **A** moves the cursor along the icons, **B** chooses the lit icon, **C** backs out.
- You can also tap an icon directly. In the yarn game, A = left, C = right, or tap that side of the screen.
- Android's Back button or gesture works like C. Back on the main screen leaves the app.
- The **?** button in the corner has the care guide, sound, reminders, and Start over.

## Reminders
When you leave the app, NyxPet works out when Nyx will next need you and schedules one notification
for that moment: hungry, lonely, litter box full, or asleep with the light still on.
Opening the app cancels it. Android asks once whether NyxPet may send notifications.
Turn them off any time with **? → Reminders**.

## Step 1: Build the APK on GitHub (no computer needed)
1. On github.com, create a new **public** repository named `nyxpet`.
2. Upload every file and folder in this project to it ("Add file → Upload files", then drag
   the whole folder in). Keep the hidden `.github` folder.
3. Open the **Actions** tab, enable workflows if asked, and run **Build NyxPet APK**
   (it also runs on every upload). It takes about 4 minutes.
4. When it shows a green check, your download link is:
   `https://github.com/BBGINN77/nyxpet/releases/latest/download/nyxpet.apk`

## Step 2: Install on the tablet
1. On the tablet, open Chrome and go to the link above.
2. Open the download, allow Chrome to install apps if Android asks, and choose **Install**.
3. **NyxPet** appears with your other apps (Nyx on a little green screen).
4. Allow notifications when it asks, if you want reminders.

Updating later: download the newest APK from the same link and install it. It installs over the
old version and keeps Nyx, her age and her stats.

## Backup and restore plan
Nothing on the tablet is changed except installing one app:
- **Remove it:** Settings → Apps → NyxPet → Uninstall. (This also deletes Nyx's save.)
- **Stop reminders only:** ? → Reminders: off, or Settings → Apps → NyxPet → Notifications.
- **Undo the install permission:** Settings → Apps → Chrome → Install unknown apps → off.
- Your home screen, other apps and logins are never touched. Nothing is rooted, no developer
  settings change, and the launcher is not replaced.

## Project layout
- `app/src/main/assets/www/index.html`: the whole toy (HTML, CSS, JavaScript).
- `app/src/main/java/com/nyxcorp/nyxpet/MainActivity.kt`: full-screen web view, Back button, haptics.
- `app/src/main/java/com/nyxcorp/nyxpet/Reminders.kt`: the "Nyx needs you" notifications.
- `app/nyxpet.keystore`: fixed signing key so updates install over each other. Personal use only.

All art, sounds and text are original NyxCorp material.
