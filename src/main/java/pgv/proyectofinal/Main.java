package pgv.proyectofinal;

public class Main {
    public static void main(String[] args) throws Exception {
        if(args.length < 6){
            System.out.println("No ha sido ejecutado de la forma correcta.\nLa sintaxis del programa:\napp.exe --mode <cliente|servidor> --ip <ip> --port <port>");
        }
        switch (args[1]){
            case "server":
                App.main(args);
                break;
            case "client":
                AppClient.main(args);
                break;
        }
    }
}
