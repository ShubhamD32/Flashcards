package flashcards;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

class Card {

    private Map<String,String> map = new LinkedHashMap<>();
    private Map<String,Integer> mistakeMap = new LinkedHashMap<>();
    List<String> loggerList = new ArrayList<>();
    static private int hardestNumber;

    public Card(Map<String,String> map) {
        this.map = map;
    }

    public void addElement () {
        Scanner scanner = new Scanner(System.in);
        System.out.println("The card:");
        addToLogger("The card:");
        String card = scanner.nextLine();
        addToLogger(card);
        if (map.containsKey(card)) {
            System.out.println("The card \"" + card + "\" already exists.");
            addToLogger("The card \"" + card + "\" already exists.");
        }
        System.out.println("The definition of the card:");
        addToLogger("The definition of the card:");
        String definition = scanner.nextLine();
        addToLogger(definition);
        if (map.containsValue(definition)) {
            System.out.println("The definition \"" + definition + "\" already exists.");
            addToLogger("The definition \"" + definition + "\" already exists.");
        }
        map.put(card, definition);
        mistakeMap.put(card, 0);
        System.out.println("The pair (\"" + card + "\":\"" + definition + "\") has been added.");
        addToLogger("The pair (\"" + card + "\":\"" + definition + "\") has been added.");
    }

    public void removeElement () {
        Scanner scanner = new Scanner(System.in);
        System.out.println("The card:");
        addToLogger("The card:");
        String card = scanner.nextLine();
        addToLogger(card);
        if (!map.containsKey(card)) {
            System.out.println("Can't remove \"" + card + "\": there is no such card");
            addToLogger("Can't remove \"" + card + "\": there is no such card");
        } else {
            map.remove(card);
            mistakeMap.remove(card);
            System.out.println("The card has been removed.");
            addToLogger("The card has been removed.");
        }
    }

    public void exportFile () {
        Scanner scanner = new Scanner(System.in);
        System.out.println("File name:");
        addToLogger("File name:");
        String fileName = scanner.nextLine();
        addToLogger(fileName);
        String filePath = "./" + fileName;
        int counter = 0;
        File file = new File(filePath);
        try (FileWriter fileWriter = new FileWriter(file)){
            for (Map.Entry<String,String> entry : map.entrySet()) {
                fileWriter.write(entry.getKey());
                fileWriter.write("\n");
                fileWriter.write(entry.getValue());
                fileWriter.write("\n");
                mistakeWriter(fileWriter, entry.getKey());
                fileWriter.write("\n");
                counter++;
            }
        } catch (IOException e) {
            System.out.println("The file could not be created.");
            addToLogger("The file could not be created.");
        }
        System.out.println(counter + " cards have been saved");
        addToLogger(counter + " cards have been saved");
    }

    public void mistakeWriter(FileWriter fileWriter, String string) {
        try {
            fileWriter.write(Integer.toString(mistakeMap.get(string)));
        } catch (IOException e) {
            System.out.println("The file could not be created.");
            addToLogger("The file could not be created.");
        }
    }


    public void importFile () {
        Scanner scanner = new Scanner(System.in);
        System.out.println("File name:");
        addToLogger("File name:");
        String fileName = scanner.nextLine();
        addToLogger(fileName);
        String filePath = "./" + fileName;
        File file = new File(filePath);
        int fileLineCounter = 0;
        try {
            Scanner fileScanner = new Scanner(file);
            fileScanner.useDelimiter("\\s+!\\n");
            while (fileScanner.hasNext()) {
                String card = fileScanner.nextLine();
                String definition = fileScanner.nextLine();
                int mistakes = Integer.parseInt(fileScanner.nextLine());
                if (map.containsKey(card)) {
                    map.replace(card, definition);
                    fileLineCounter++;
                } else {
                    map.put(card, definition);
                    fileLineCounter++;
                }
                if (!mistakeMap.containsKey(card)) {
                    mistakeMap.put(card, mistakes);
                } else {
                    mistakeMap.replace(card, mistakes);
                }
            }
            if (fileLineCounter == 1) {
                System.out.println(fileLineCounter + " card has been loaded.");
                addToLogger(fileLineCounter + " card has been loaded.");
            } else {
                System.out.println(fileLineCounter + " cards have been loaded.");
                addToLogger(fileLineCounter + " cards have been loaded.");
            }
        } catch (FileNotFoundException e ) {
            System.out.println("File not found.");
            addToLogger("File not found.");
        }
    }

    public Map<String,Integer> getMistakeMap() {
        return mistakeMap;
    }

    public void ask () {
        Map<String,String> reverseMap = new LinkedHashMap<>();
        for (Map.Entry<String,String> entry : map.entrySet()) {
            reverseMap.put(entry.getValue(), entry.getKey());
        }
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();
        System.out.println("How many times to ask?");
        addToLogger("How many times to ask?");
        int numberOfTries = Integer.parseInt(scanner.nextLine());
        addToLogger(Integer.toString(numberOfTries));
        ArrayList<String> arrayList = new ArrayList<>(map.keySet());
        int arrayListSize = arrayList.size();
        for (int i = 0; i < numberOfTries; i++) {
            int randomSelector = random.nextInt(arrayListSize);
            System.out.println("Print the definition \"" + arrayList.get(randomSelector) + "\":");
            addToLogger("Print the definition \"" + arrayList.get(randomSelector) + "\":");
            String askedString = arrayList.get(randomSelector);
            //System.out.println("Asked string is : " + askedString);
            String checkString = scanner.nextLine();
            addToLogger(checkString);
            String answerString = map.get(arrayList.get(randomSelector));
            if (checkString.equalsIgnoreCase(answerString)) {
                System.out.println("Correct answer.");
                addToLogger("Correct answer.");
            } else if (reverseMap.containsKey(checkString)) {
                if (!mistakeMap.containsKey(askedString)) {
                    mistakeMap.put(askedString, 1);
                } else {
                    mistakeMap.replace(askedString, mistakeMap.get(askedString) + 1);
                }
                System.out.println("Wrong answer. The correct one is \"" + map.get(askedString) + "\", you've just written the definition of \"" + reverseMap.get(checkString) + "\"");
                addToLogger("Wrong answer. The correct one is \"" + map.get(askedString) + "\", you've just written the definition of \"" + reverseMap.get(checkString) + "\"");
            } else {
                if (!mistakeMap.containsKey(askedString)) {
                    mistakeMap.put(askedString, 1);
                } else {
                    mistakeMap.replace(askedString, mistakeMap.get(askedString) + 1);
                }
                System.out.println("Wrong answer. The correct one is \"" + map.get(reverseMap.get(answerString)) + "\"");
                addToLogger("Wrong answer. The correct one is \"" + map.get(reverseMap.get(answerString)) + "\"");
            }
        }
    }

    public void getHardest() {
        List<String> hardestCards = new ArrayList<>();
        if (mistakeMap.isEmpty()) {
            System.out.println("There are no cards with errors");
            addToLogger("There are no cards with errors");
        } else {
            hardestCards = getLargestStrings();
            if ((mistakeMap.get(hardestCards.get(0)) == 0)) {
                System.out.println("There are no cards with errors");
                addToLogger("There are no cards with errors");
            } else if (hardestCards.size() == 1) {
                System.out.println("The hardest card is \"" + hardestCards.get(0) + "\"." + " You have " + mistakeMap.get(hardestCards.get(0))  + " errors answering it.");
                addToLogger("The hardest card is \"" + hardestCards.get(0) + "\"." + " You have " + mistakeMap.get(hardestCards.get(0))  + " errors answering it.");
            } else {
                System.out.print("\"The hardest cards are\"" );
                addToLogger("\"The hardest cards are" );
                for (int i = 0; i < hardestCards.size(); i++) {
                    System.out.print(" \"" + hardestCards.get(i) + "\",");
                    System.out.print("\n");
                    addToLogger(" \"" + hardestCards.get(i) + "\",");
                    addToLogger("\n");
                }
                System.out.print(" \"" + hardestCards.get(hardestCards.size()) + "\". You have " + mistakeMap.get(hardestCards.get(0))  + " errors answering it.");
                //addToLogger(" \"" + hardestCards.get(hardestCards.size() - 1) + "\". You have " + mistakeMap.get(hardestCards.get(0))  + " errors answering it.");
            }
            /*System.out.println(hardestCards);
            System.out.println("Count -> " + mistakeMap.get(hardestCards.get(0)));*/
        }
        //System.out.println("SHUBHAM ->" + mistakeMap.get(hardestCards.get(0)));
    }

    private List<String> getLargestStrings() {
        List<String> list = new ArrayList<String>();
        int max = Collections.max(mistakeMap.values());
        if (mistakeMap.size() == 0) {
            list.clear();
            return list;
        } else {
            for (Map.Entry<String, Integer> entry : mistakeMap.entrySet()) {
                if (entry.getValue() == max) {
                    list.add(entry.getKey());
                    max = entry.getValue();
                }
            }
            hardestNumber = max;
            return list;
        }
    }

    /* public int returnHardestNumber() {
     *//*return mistakeMap.get();*//*
    }*/

    void resetStatistics() {
        for (Map.Entry<String,Integer> entry : mistakeMap.entrySet()) {
            mistakeMap.replace(entry.getKey(), 0);
        }
        System.out.println("Card statistics has been reset.");
        addToLogger("Card statistics has been reset.");
    }

    void addToLogger(String string) {
        loggerList.add(string);
    }

    /*void displayLoggerList() {
        System.out.println(loggerList);
    }*/

    void writeLog() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("File name:");
        addToLogger("File name:");
        String filename = scanner.nextLine();
        addToLogger(filename);
        String filePath = "./" + filename;
        File file = new File(filePath);
        try (FileWriter fileWriter = new FileWriter(file)) {
            for (String string : loggerList) {
                fileWriter.write(string);
                fileWriter.write("\n");
            }
        } catch (IOException e) {
            System.out.println("Error writing to file");
            addToLogger("Error writing to file");
        }
        System.out.println("The log has been saved.");
        addToLogger("The log has been saved.");
    }

    void fileReadArgs (String fileNameArgs) {
        String fileName = fileNameArgs;
        addToLogger(fileName);
        String filePath = "./" + fileName;
        File file = new File(filePath);
        int fileLineCounter = 0;
        try {
            Scanner fileScanner = new Scanner(file);
            fileScanner.useDelimiter("\\s+!\\n");
            while (fileScanner.hasNext()) {
                String card = fileScanner.nextLine();
                String definition = fileScanner.nextLine();
                int mistakes = Integer.parseInt(fileScanner.nextLine());
                if (map.containsKey(card)) {
                    map.replace(card, definition);
                    fileLineCounter++;
                } else {
                    map.put(card, definition);
                    fileLineCounter++;
                }
                if (!mistakeMap.containsKey(card)) {
                    mistakeMap.put(card, mistakes);
                } else {
                    mistakeMap.replace(card, mistakes);
                }
            }
            if (fileLineCounter == 1) {
                System.out.println(fileLineCounter + " card has been loaded.");
                addToLogger(fileLineCounter + " card has been loaded.");
            } else {
                System.out.println(fileLineCounter + " cards have been loaded.");
                addToLogger(fileLineCounter + " cards have been loaded.");
            }
        } catch (FileNotFoundException e ) {
            System.out.println("File not found.");
            addToLogger("File not found.");
        }
    }

    void fileWriteArgs(String fileNameArgs) {
        String fileName = fileNameArgs;
        addToLogger(fileName);
        String filePath = "./" + fileName;
        int counter = 0;
        File file = new File(filePath);
        try (FileWriter fileWriter = new FileWriter(file)){
            if (map.size() == 0) {
                System.out.println("0 cards have been saved.");
            }
            for (Map.Entry<String,String> entry : map.entrySet()) {
                fileWriter.write(entry.getKey());
                fileWriter.write("\n");
                fileWriter.write(entry.getValue());
                fileWriter.write("\n");
                mistakeWriter(fileWriter, entry.getKey());
                fileWriter.write("\n");
                counter++;
            }
        } catch (IOException e) {
            System.out.println("The file could not be created.");
            addToLogger("The file could not be created.");
        }
        System.out.println(counter + " cards have been saved");
        addToLogger(counter + " cards have been saved");
    }

}

public class Main {


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Map<String,String> map = new LinkedHashMap<>();
        Card card = new Card(map);
        for (int i = 0; i < args.length-1; i++) {
            if ("-import".equals(args[i])) {
                card.fileReadArgs(args[++i]);
            }
        }
        System.out.println("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
        card.addToLogger("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
        String inputString = scanner.nextLine();
        card.addToLogger(inputString);
        while (!"exit".equalsIgnoreCase(inputString)) {
            switch (inputString.toLowerCase()) {
                case "add" :
                    card.addElement();
                    break;
                case "remove" :
                    card.removeElement();
                    break;
                case "import" :
                    card.importFile();
                    break;
                case "export" :
                    card.exportFile();
                    break;
                case "ask" :
                    card.ask();
                    break;
                case "hardest card" :
                    card.getHardest();
                    break;
                case "reset stats" :
                    card.resetStatistics();
                    break;
                case "log" :
                    card.writeLog();
                    break;
            }
            System.out.println("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
            card.addToLogger("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
            inputString = scanner.nextLine();
            card.addToLogger(inputString);
        }
        System.out.println("Bye bye!");
        for (int i = 0; i < args.length-1; i++) {
            if ("-export".equals(args[i])) {
                card.fileWriteArgs(args[++i]);
            }
        }
        card.addToLogger("Bye bye!");
    }
}
