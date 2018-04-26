# Simulation Final Project -- Agent-based Simulation with Disease

## Running

The project uses the Streams API, so Java 8 or greater is required to build and run the project.

To run the simulation, first build the project using `javac -d ./build/production/Sim_Final_Proj -classpath ./squintV2.19.jar *.java`
Then, run the project using `java -classpath "./build/production/Sim_Final_Proj;./squintV2.19.jar" SimulationManager`

## Our Experiments

There are many options that we are able to tweak to get different results, these include the length of an agent's immune system, length of disease genome,
metabolic penalty of a disease, how often an agent can have an immune response to a disease, and how often the agent's immune system randomly mutates.

While holding the length of the disease genome constant as well as the other variables, the length of the agent's immune system can impact how likely the agent will already be immune to the disease. The longer the agent's immune system, the greater the chance that the agent is already immune to the disease. There is also a greater chance of having a closer match to a disease genome, which could lead to becoming immune to diseases faster than with a shorter agent's immune system. With a greater length of the agents' immune systems, the initial grid of agents will contain more healthy, blue agents than when setting the length of the agents' immune systems to being a smaller length. 

The shorter the length of a disease genome, the higher the chance that an agent might already be immune to the disease. This will also allow agents to become immune to the disease faster. When the length of the disease genome is shorter, the grid of agents will start with more agents being blue and healthy and more quickly turn into a grid of blue, healthy agents instead of red, infected agents. Oppositely, the longer the length of a disease genome, the less chance that an agent will be already immune to the disease.  Longer disease genomes correspond to agents being infected for a longer period of time, because the immune response will take longer to appropriately flip each digit to have developed an immunity to the disease. When the grid is initially populated, the majority of agents will be red and infected. However, as time progresses, the grid will begin to reflect a combination of blue, healthy agents on the outsides with more red, infected agents near the cells with the most resources. There will be some mixed in blues with the red agents from the replacement of dead agents with new, healthy agents. However, the majority of healthy agents will be found on the outside, which is because while there are less reousrces, infected agents with higher metabolic rates cannot survive in this environment for very long. 

Increasing the metabolic penalty of a disease results in agents dying off faster. Since every agent that dies is replaced by a new healthy agent, the number of blue agents quickly overtakes the number of sick agents. And if left to run long enough, all the agents would eventually be blue as the sick agents are dying and being replaced at a higher rate than they are moving and infecting their healthy neighboring agents.

When experimenting with the agent's immune response to a disease, we noticed how an increase in how often an agent can respond to a disease corresponds to an increase in how quickly agents are able to fight off the diseases and thus become healthy more quickly. If an agent's immune system could not respond to a disease frequently, the agent would remain infected for longer and die more quickly because of the increased metabolic rate associated with being infected, especially if the agent is not near a high resource cell. 

[Still need to talk about experimenting with how often the agent's immune system randomly mutates - forget what entirely happened with this]

