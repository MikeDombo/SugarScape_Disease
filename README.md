# Simulation Final Project -- Agent-based Simulation with Disease

## Running

The project uses the Streams API, so Java 8 or greater is required to build and 
run the project.

To run the simulation, first build the project using `javac -d build/production/Sim_Final_Proj -classpath "squintV2.19.jar" *.java` Then, run the project using

Windows: `java -classpath "build/production/Sim_Final_Proj;squintV2.19.jar" SimulationManager`

Linux/Mac: `java -classpath "build/production/Sim_Final_Proj:squintV2.19.jar" SimulationManager`

## Changing Parameters

- Number of Diseases - `SimulationManager:18` - Currently set to 12
- Disease Genome Length - `SimulationManager:20` - A lambda function which can return integers.
Currently returns a random uniform integer 1-11.
- Disease Metabolic Penalty - `SimulationManager:23` - A lambda function which returns doubles.
Currently returns a random uniform double 1-2.
- Display the Current Cell Resource Level or Max Resource Level - `SimulationManager:28` - Initially true,
shows the current level. Set to false to show the maximum level.
- Frequency of Random Immune Mutation - `SimulationManager:31` - Currently random uniform double from 3-7. 
- Frequency of Immune Disease Response - `SimulationManager:34` - Currently a random normal with
mean of 1. `Math.abs` is used to make all times in the future.

## Our Experiments

There are many options that we are able to tweak to get different results,
these include the length of an agent's immune system, length of disease genome,
metabolic penalty of a disease, how often an agent can have an immune response
to a disease, and how often the agent's immune system randomly mutates. The sample
size for all of the average number of healthy and infected agents found and printed
below was 50 experiments with a maximum time of 100 seconds for each experiment.

While holding the length of the disease genome constant as well as the other variables,
the length of the agent's immune system can impact how likely the agent will already
be immune to the disease. The longer the agent's immune system, the greater the
chance that the agent is already immune to the disease. There is also a greater
chance of having a closer match to a disease genome, which could lead to becoming
immune to diseases faster than with a shorter agent's immune system. With a greater
length of the agents' immune systems, the initial grid of agents will contain more
healthy, blue agents than when setting the length of the agents' immune systems to
being a smaller length.

In testing these assumptions, we held the genome length constant at 10, the metabolic
penalty at 0.5, the mutation rate at 5, the immune response rate at 1, the number of
diseases at 12. The following numbers are some of the output we found
for the number of healthy and infected agents on average when changing the length of
the agent's immune system:
Immune Length	Healthy		Infected
10				141			259
30				142			258
50				145			255
100				149			251
150				152			248

This output confirms the assumptions that were made above that as the immune system
length increased, the number of healthy agents in the system also increased. While
the numbers were not as drastic as we initially anticipated, the numbers still support
our theory.

The shorter the length of a disease genome, the higher the chance that an agent might
already be immune to the disease. This will also allow agents to become immune to
the disease faster. When the length of the disease genome is shorter, the grid of
agents will start with more agents being blue and healthy and more quickly turn
into a grid of blue, healthy agents instead of red, infected agents. Oppositely,
the longer the length of a disease genome, the less chance that an agent will be
already immune to the disease.  Longer disease genomes correspond to agents being
infected for a longer period of time, because the immune response will take longer
to appropriately flip each digit to have developed an immunity to the disease.
When the grid is initially populated, the majority of agents will be red and
infected. However, as time progresses, the grid will begin to reflect a combination
of blue, healthy agents on the outsides with more red, infected agents near the
cells with the most resources. There will be some mixed in blues with the red
agents from the replacement of dead agents with new, healthy agents. However, the
majority of healthy agents will be found on the outside, which is because while
there are less resources, infected agents with higher metabolic rates cannot
survive in this environment for very long.

In testing these assumptions, we held the immune system length constant at 50, the
metabolic penalty at 0.5, the mutation rate at 5, the immune response rate at 1, the
number of diseases at 12. The following numbers are some of the output we found
for the number of healthy and infected agents on average when changing the length of
the disease's genome length:
Genome Length	Healthy		Infected
5				305			95
10				145			255
20				139			261
40				135			265

![genome_length](https://user-images.githubusercontent.com/14288345/39609901-7b6ecfac-4f1a-11e8-93ed-9831f0d22bac.png)

This output confirms the assumptions that were made above that as the disease's genome
length increases, the number of healthy agents in the system decreases because the
chance that an agent is already immune to a disease decreases.


Increasing the metabolic penalty of a disease results in agents dying off faster.
Since every agent that dies is replaced by a new healthy agent, the number of
blue agents quickly overtakes the number of sick agents. And if left to run long
enough, all the agents would eventually be blue as the sick agents are dying and
being replaced at a higher rate than they are moving and infecting their healthy
neighboring agents.

In testing the claims made above, we held the immune system length constant at 50, the
disease genome length at 10, the mutation rate at 5, the immune response rate at 1, the
number of diseases at 12. The following numbers are some of the output we found
for the number of healthy and infected agents on average when changing the metabolic
penalty for an agent being infected:
Metabolic Penalty	Healthy		Infected
0.1					87			313
0.5					145			255
1.5					216			184
3					273			127

This output confirms the statements that were made above that as the metabolic penalty
increases, the number of healthy agents in the system will increase. Initially, this
seemed strange. However, when considering that when an agent dies in our landscape,
they are replaced with a healthy agent, this makes sense. Having a higher number of
healthy agents as the metabolic penalty increases means that agents are in fact dying
more quickly and thus more frequently being replaced with healthy agents.

When experimenting with the agent's immune response to a disease, we noticed how
an increase in how often an agent can respond to a disease corresponds to an
increase in how quickly agents are able to fight off the diseases and thus become
healthy more quickly. If an agent's immune system could not respond to a disease
frequently, the agent would remain infected for longer and die more quickly
because of the increased metabolic rate associated with being infected,
especially if the agent is not near a high resource cell.

In testing these assumptions, we held the immune system length constant at 50, the
disease genome length at 10, the metabolic penalty at 0.5, the mutation rate at 5, and
the number of diseases at 12. The following numbers are some of the output we found
for the number of healthy and infected agents on average when changing the agent's
immune response rate:
Immune Response Rate	Healthy		Infected
0.2						152			248
1						145			255
5						140			260
15						137			263

This output shows that as the immune response rate is increased, the number of healthy
agents in the landscape decreases. This is probably because the agents are able to cure
themselves and stick around longer. Whereas, others die and are reborn as healthy.

In addition to agents attacking their diseases, their immune systems also randomly
mutate, flip one bit in their immune system, with an even drawn from a uniform 3, 7.
We decided that random mutations should be truly random, so the time is drawn from
a uniform distribution. We then selected the bounds of the mutation such that over
the average agent lifetime there would be, on average, 20 such mutations.
Because the mutations are completely random they can aid the agent in attacking a
disease, or they may hinder the progress. Increasing the event rate causes
noticeably more healthy agents, especially around the high sugar areas which
previously had even more disease.

Given that this is how we decided our mutate function to run, the numbers of healthy
and infected agents did not change dramatically as the mutation rate increased, which
can be seen in the numbers below:
Mutation Rate	Healthy		Infected
0.5				143			257
2				146			254
5				145			255
15				146			254
