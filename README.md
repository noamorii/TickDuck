# TickDuck

The TickDuck app is a convenient place to save and schedule various tasks. 

Prioritization helps the user to prioritize daily tasks correctly, and the calendar helps to plan properly and not forget important things. You can also divide tasks into different sections, so-called categories, thanks to which you can clearly separate tasks for work and school.

Also, the app supports multiple users, so if you share a mobile device with someone else, you can both take notes! In the future, we would like to expand this functionality with external resources and therefore add the possibility of being logged in on two devices at the same time.

## Implementation

Camera interfaces, disk read capabilities and vibration were used. Furthermore, we tried to keep the support for android sdk 21+, where the compiler is in version 32. Some features had to be limited and some changed - for example, the vibration should be a little different on the version 31+ range.

<h3>Libraries</h3>

<b>Room library</b> - provides an abstraction layer on top of SQLite that allows more robust access to the database while using the full power of SQLite.

<b>Data Binding</b> - a support library that allows you to bind UI components in your layouts to data sources in your application using a declarative format rather than programmatically.

<b>Desugar bundler</b> - some functionalities, for example “LocalDate”, are not supported by versions as old as sdk 21. Bundler bundles this functionality with the application, thanks to which it is available even on such old versions.

<h3>UI Prototype</h3>

https://www.figma.com/file/VVaRGK3du7zseM5Cp3cmso/PDA-concept?node-id=0%3A1
