import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner scan = new Scanner(System.in);
        BufferPool buffer = new BufferPool();
        if(args.length > 0){
            try{
            buffer.initalize(Integer.parseInt(args[0]));
            }
            catch(NumberFormatException e){
                System.out.println("Invalid argument. Need integer.");
                System.exit(1);
            }
        }
        else{
            System.out.println("No buffer size provided.");
            System.exit(1);
        }
        while(true){
            String[] input = scan.nextLine().split(" ", 3);
            String command = input[0].toUpperCase();
            int number;
            if(input.length > 1){
                number = Integer.parseInt(input[1]);
                byte[] record;
                if(command.equals("GET")){
                    buffer.GET(number);
                }
                else if(command.equals("SET")){
                    record = input[2].substring(1, input[2].length() - 1).getBytes();
                    buffer.SET(number, record);
                }
                else if (command.equals("PIN")) {
                    buffer.PIN(number);
                }
                else if(command.equals("UNPIN")){
                    buffer.UNPIN(number);
                }
                else{
                    System.out.println("Invalid command");
                }
            }
            else if (command.equals("QUIT")) {
                System.out.println("Thank you, come again!");
                System.exit(0);
            }
            else{
                System.out.println("Invalid command");
            }

        }
    }
}