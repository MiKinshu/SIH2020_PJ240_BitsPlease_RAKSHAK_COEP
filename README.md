# Rakshak 

This is the Rakshak app. But Rakshak is not only any app, it's a complete line of products including a user app, an officer app, one zonal server and two other websites. This is the user app that is to be used by the general public.

Please test the app in both network availability and no network availability (Airplane Mode).

The app automatically detects the availability of network. That's why we are asking you to open the app in both network and no network. The App uses a minimalist UI and thus you see only what's necessary whereas there is a lot going under the hood.
1. What to expect when you open the app in network condition:
    1. One button in the top left corner :
        * What you see :
            This tells you the probability of a danger not happening in your area.
        * What happens under the hood:
            This takes you to the AI based prediction system that tells you the probability of a disaster happening in your area based on the time of the day, gender of the user and some other properties.
    2. One button on the top right corner :
        * What you see :
            This button open up a dialog where you enter a key to your organisation.
        * What actually happens:
            The idea is that, all people belonging to an organisation (eg COEP, IITD etc) are notified against disasters in the organisation. The administration can also take faster action than the external authorities like Police, Ambulance etc which are notified too. You can register your own org at https://rakshak-local.herokuapp.com. Make sure you do not use the id IIITA, Coep, TMC since they are already registered.
    3. A big central Ask Help button :
        * What you see :
            You press this button and it makes a call to the service mentioned in the drop down mentioned below.
        * What actually happens:
            1. You directly call the concerned authority.
            2. Your location is sent to the central server.
            3. The information then reaches the nearest the relief center corresponding to your requirements(This may be police vehicles, police station, nearest hospital etc)
            4. An officer corresponding to your requirement is assigned to you. He is given your contact details, your previous medical history and fastest path to reach to you (We have made a separate app for officers.)
            5. The organisation is notified of your emergency(check the dashboard of your org, if you registered). The org can then declare an remergency from the dashboard. All the members of the org receive a notification.
            6. The data is sent to our AI model to make better predictions in the future.
            7. All this happens with a single click!
    
    Please use two phones to verify the notification system. Sign in to both the phones.
2. What to expect when you open the app in no network zone:
    1. A connect via radio button :
        * What you see :
            This opens up a Walkie-Talkie that can be used to communicate if there is no cell phone coverage.
        * What actually happens:
            This uses an efficient combination of WiFi-direct and Bluetooth signals. This has a high bandwidth, low latency and uses end to end encryption.
    2. An Emergency message button :
        * What you see :
            Open up a dialogue where you choose your emergency. And see "Sending Ultrasound Message written".
        * What happens under the hood :
            1. This sends an ultrasound signal containing your location.
            2. The nearby devices recieve it. Check if they have access to signal?
                * If yes : It does everything that happens as mentioned in 1.3
                * If No : They forward it.
            3. A chain of devices is formed. Thus increasing your chances of survival even in no network :)
    
    Please use two phones to test the Ultrasound feature and the Walkie-Talkie feature. You will see "received" when an ultrasound message is recieved.

Additionally the app also uses OTP authorization to prevent misuse of the app.

Check out the presentation video of our project on [YouTube](https://www.youtube.com/watch?v=QXC2jZdFRS0)
