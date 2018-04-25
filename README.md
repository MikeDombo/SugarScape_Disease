# Simulation Final Project -- Agent-based Simulation with Disease

## Running

The project uses the Streams API, so Java 8 or greater is required to build and run the project.

To run the simulation, first build the project using `javac -d ./build/production/Sim_Final_Proj -classpath ./squintV2.19.jar *.java`
Then, run the project using `java -classpath "./build/production/Sim_Final_Proj;./squintV2.19.jar" SimulationManager`

## Our Experiments

There are many options that we are able to tweak to get different results, these include the length of an agent's immune system, length of disease genome,
metabolic penalty of a disease, how often an agent can have an immune response to a disease, and how often the agent's immune system randomly mutates.

