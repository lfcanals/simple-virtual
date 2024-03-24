# Simple Server Virtualization
Simple virtualizer implementation for TCP/UDP based on Netty.

Originally created to virtualise messages to Reuters to test a FX
electronic realtime system.

It's not a tool ready to use out-of-the-box by anyone without no knowledge
on what's doing: it's just a piece of code used in a test environment
and an example of how to create tests a little bit bigger.

### Some curious things
For exmaple in the folder src/resource/patterns ou can find an initial
subset of the message interchanged with a Reuters server:
 1. The identifier of the operation
 2. The heartbeat that maintains the line up

The rest of messages are not public since it's part of your job: adapt to
your case!!

### Config
Change in pom.xml the port where virtualizer will be listening.

### Run
```
mvn exec:exec
```

### WORK IN PROGRESS
  1. Simplify how to get the patterns
  2. Add examples of patterns request/answer
  3. Add script processing to control answer flow
