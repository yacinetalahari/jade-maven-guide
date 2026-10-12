package agents;

import jade.core.Agent;

public class HelloAgent extends Agent {
    protected void setup() {
        System.out.println("Hello from Maven! I am agent " + getLocalName());
    }
}