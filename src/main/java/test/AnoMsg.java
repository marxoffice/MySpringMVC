package test;

import annotation.Autowired;
import annotation.Component;

@Component
public class AnoMsg {
    @Autowired
    private MsgService service;
    public void say() {
        System.out.println("another message: " + service.getMessage());
    }
}
