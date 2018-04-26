# Simulation Final Project -- Agent-based Simulation with Disease

## Running

The project uses the Streams API, so Java 8 or greater is required to build and run the project.

To run the simulation, first build the project using `javac -d ./build/production/Sim_Final_Proj -classpath ./squintV2.19.jar *.java`
Then, run the project using `java -classpath "./build/production/Sim_Final_Proj;./squintV2.19.jar" SimulationManager`

## Our Experiments

There are many options that we are able to tweak to get different results, these include the length of an agent's immune system, length of disease genome,
metabolic penalty of a disease, how often an agent can have an immune response to a disease, and how often the agent's immune system randomly mutates.

Increasing the metabolic penalty of a disease results in agents dying off faster. Since every agent that dies is replaced by a new healthy agent, the number of blue agents quickly overtake the number of sick agents. And if left to run long enough, all the agents would eventually be blue as the sick ones are replaced at a higher than they are infecting healthy agents.

The shorter the length of a disease genome, the higher the chance that an agent might already be immune to the disease. This will also allow them to become immune to the disease faster.

Increasing how often an agent can have an immune response to a disease  would increase how quickly agents are able to fight of diseases. 

