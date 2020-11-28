package test;

import annotation.Autowired;
import annotation.Component;

@Component
public class MsgPrinter {
    @Autowired
    private MsgService service;
    public void printMessage() {
        System.out.println("printer.printMessage(): " + this.service.getMessage());
    }
}
