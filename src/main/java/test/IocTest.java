package test;

public class IocTest {
    public static void main(String[] args) {
        AppContext context = new AppContext(App.class);
        MsgService service = context.getBean(MsgService.class);
        MsgPrinter printer = context.getBean(MsgPrinter.class);
        System.out.println("service.getMessage(): " + service.getMessage());
        printer.printMessage();
        AnoMsg am = context.getBean(AnoMsg.class);
        am.say();
        String buffer = context.getBean(String.class);
        System.out.println(buffer);
        Integer integer = context.getBean(Integer.class);
        System.out.println(integer);
    }
}
