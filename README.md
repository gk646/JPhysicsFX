### JPhysicsFX
#### A small physics/particle simulation built in JavaFX.



JAR built with Java 19, only basic runtime required.


How it works:

Download the most recent release and run it, preferably start it with the command prompt:

~~~
java -jar PhysicsFX.jar --x=1280 --y=960 --n=25000 --T=6
~~~
Arguments:

~~~
    --x= window width (required),(0-infinity)  
        in pixels 
~~~
        --y= window height (required), (0-infinity) 
            in pixels 
~~~
    --n= amount of particles (required),(0-infinity)
        Recommended: 25,000-50,000
~~~
       --T= amount of threads (required), (0-50)
            Recommended: 5-10
~~~
    --M= run mode  (optional), release (default), debug
~~~
        --style (optionl) , particle (default), grid-based
            simulation style

