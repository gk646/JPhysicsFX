### PhysicsFX
#### A small physics/particle simulation built in JavaFX.



JAR built with Java 19, only basic runtime required.


How it works:

Download the most recent release and run it, preferably start it with the command prompt:

~~~
java -jar PhysicsFX.jar --x=1280 --y=960 --n=25000 --T=6
~~~
Arguments:

~~~
    -x (required)
        Window width in pixels (0-infinity)  
~~~
        -y (required)
            Window height in pixels (0-infinity)  
~~~
    -n (required)
        Total amount of particles to spawn (0-infinity)
        Recommended: 25,000-50,000

~~~
       -T (required)
            number of threads (0-50)
            Recommended: 5-10

~~~
    -M (optional)
        Run mode (release (default), debug)
~~~
        -style (optionl)
            simulation style (particle (default), grid-based)


