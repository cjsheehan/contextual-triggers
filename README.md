# Contextual Triggers

### Creating a Trigger Thread
To have a Trigger run in the background service, it needs to be defined in a **TriggerThread**. The main work of the **TriggerThread** is handled by the **BaseThread**, leaving only the **start**, **stop**, and **run** actions left to be implemented. These actions must be overriden and defined for every **TriggerThread**:

* void doStartAction()
* void doStopAction()
* void doRunAction()

The most important step is the **run** triggerType. This will define the triggerType that should occur for every step of the thread. The individual Trigger code should go here. This is an example of a Trigger that will increment a number until it reaches 20, then send a notification to the user:
```
int number = 0;
@Override
public void doRunAction() {
	number++;
	if (number == 20) {
		sendNotification();
	}
}
```
The other actions are mainly used for setting up and tearing down the Trigger, and can even be left empty if they are not needed.

### Sending a Notification
To send a notification once a Trigger has been hit, you can use the *sendNotification()* method. Each Trigger that extends **TriggerThread** will also have to implement the following two methods in order for *sendNotification()* to work properly:

* String getTitle()
  * Returns the title for the notification
* String getMessage()
  * Returns the message for the notification

You may also use an **Extension** method for sending notifications. This method needs the **Context** for the activity, as well as a title and a message for the notification. You can also specify a resource integer for the icon. This is an example of sending a notification using the **Extension** method, and can be used in any section of the app:

```Extension.sendNotifiction(context, "Notification Title!", "Notification message will go here!");```

### Add a new Trigger to the Application
Once you have created a new **TriggerThread**, you can add it as a setting in the app. In the **MainActivity**, there is a method called *initializeTriggerSettings()*. Inside of this method, all of the **TriggerThread**s are being added to the **TriggerService**. Making sure to call it inside the **if** statement, simply call this method:

```TriggerService.addThread(new NameOfYourTriggerThreadHere(this));```

A new setting will be added for your Trigger automatically, with the thread being wired to the background **TriggerService**.

### Querying a Trigger From the TriggerService
After a trigger has been hit and the **TriggerService** has been notified of this and is ready to handle the trigger, the **TriggerService** can communicate with other **TriggerThread**s in order to receive their data at the current time. Each **TriggerThread** must define the type of their trigger object as a generic type when extending **TriggerThread**, as well as implementing a method called *getTriggerObject()* that returns the data for that thread. With this complete, you can query the **TriggerThread** in the **TriggerService**. You must first get the thread that you want to query through the *getTrigger(TriggerType)* method. You will have to cast the result of this method to the type of **TriggerThread** that you want, and once you have that, you can call the *getTriggerObject()* method to get the data from the **TriggerThread**. Here is an example of querying the data from the **StepCounterThread**:

```
StepCounterThread thread = (StepCounterThread) getTrigger(TriggerType.STEP_COUNTER);
int stepCount = thread.getTriggerObject();
```