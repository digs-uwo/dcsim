DCSim
=====
### A Data Centre Simulation Tool for Evaluating Dynamic Virtualized Resource Management


#### Simulator Code
- the core simulation code is located in the src/ directory

#### Traces
- a set of trace files to use with the simulator are located in the traces/ directory
- trace files are csv files, with each line containing the pair [time, value]
  - time is simulation time in ms
  - value is a normalized load value in the range [0, 1], where 0 represents 0 load, and 1 represents the largest load in the trace

#### Configuration
- the config/simulation.config file contains a set of configuration parameters to configure host and VM state transition delays (i.e. for powering on and off, starting a VM), migration penalty and overhead values, and simulation output. You can also choose to use an approximate MVA calculation for VM queuing model calculation, to improve simulation performance (recommended).

#### Examples
- the examples/ directory contains a set of basic examples of how to use the simulator
- SimpleExample is a good starting point to using the simulator. You can run this file directly, and read the code for a basic introduction

#### Configuring and Using the Simulator
- A particular simulation configuration and setup is defined by extending SimulationTask, and overriding its setup() method. Within this method, configure your simulation (see examples in the example/ directory)
- Run your simulation by creating a new instance of your SimulationTask, and calling its run() method.
- You must call Simulation.initializeLogging() prior to creating and running your SimulationTask
- You can get the results of your simulation from SimulationTask.getMetrics()


#### For more detailed information on the inner workings of the simulator and how to use it, visit the [wiki](http://github.com/digs-uwo/dcsim/wiki)
