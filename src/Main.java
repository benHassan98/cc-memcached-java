import server.Server;


import java.util.Arrays;
import java.util.Optional;

public class Main {


    public static boolean validateArgs(String[] args) {
        var argList = Arrays.asList(args);

        int validCnt = 0;
        int portIndex = argList.indexOf("-p");
        int sizeIndex = argList.indexOf("-m");

        if(portIndex != -1 && portIndex + 1 < argList.size()){
            validCnt += 2;
        }

        if(sizeIndex != -1 && sizeIndex + 1 < argList.size()){
            validCnt += 2;
        }


        return argList.size() == validCnt;
    }

    public static Optional<Integer> getPort(String[] args){


        int portIndex = Arrays.asList(args).indexOf("-p");
        if(portIndex != -1 ){

            try{
                Integer port = Integer.parseInt(args[portIndex + 1]);
                return Optional.of(port);
            }
            catch (NumberFormatException e){
                return Optional.empty();
            }
        }

        return Optional.of(11211);


    }
    public static Optional<Long> getCacheSizeInBytes(String[] args){

        int sizeIndex = Arrays.asList(args).indexOf("-m");
        if(sizeIndex != -1 ){

            try{
                Long size = Long.parseLong(args[sizeIndex + 1]) * 1024L * 1024L;
                return Optional.of(size);
            }
            catch (NumberFormatException e){
                return Optional.empty();
            }

        }

        return Optional.of(1024L * 1024L);

    }

    public static void main(String[] args){

        if(!validateArgs(args)){
            System.out.println("Unspecified Arguments");
            return;
        }

        var port = getPort(args);
        var cacheSize = getCacheSizeInBytes(args);

        if(port.isEmpty()){
            System.out.println("Please Enter a valid Port");
            return;
        }
        if(cacheSize.isEmpty()){
            System.out.println("Please Enter a valid Size");
            return;
        }

        System.out.println("CC-Memcached Works on Port: "+port.get());
        System.out.println("With "+cacheSize.get()+" byte Cache Size");

        Server server = new Server(cacheSize.get());
        server.start(port.get());

    }
}