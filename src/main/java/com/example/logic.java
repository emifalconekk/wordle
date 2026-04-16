package com.example;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.io.InputStream;

public class logic {

    public static final String RESET  = "\u001B[0m";
    public static final String VERDE  = "\u001B[32m";
    public static final String AMARILLO = "\u001B[33m";
    public static final String GRIS   = "\u001B[90m";

    public List<String> lawordslist = new ArrayList<String>();
    static protected String lawords= "/wordle-La.txt";
    public List<String> tawordslist = new ArrayList<String>();
    static protected String tawords= "/wordle-Ta.txt";

    public void loadWords(String path, List<String> list)
    {
        InputStream is = getClass().getResourceAsStream(path);
        if (is == null) {
        System.out.println("No se encontró el archivo de palabras");
        return;
        }
        try (BufferedReader reader =  new BufferedReader(new InputStreamReader(is)))
            {
                String line;
                while((line = reader.readLine()) != null)
                {
                    list.add(line.trim().toLowerCase());
                }
            }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    public List<String> selectWord(List<String> list){
        Random rand = new Random();
        int index = rand.nextInt(lawordslist.size());
        List<String> correct = new ArrayList<>(Arrays.asList(list.get(index).split("")));
        return correct;
    }

    public String gameLogic(List<String> correct, List<String> letters)
    {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < letters.size(); i++) {
            if(letters.get(i).equals(correct.get(i))){
                result.append(VERDE + letters.get(i) + RESET);
            }
            else if(correct.contains(letters.get(i)))
            {
                result.append(AMARILLO + letters.get(i) + RESET);
            }
            else{
                result.append(GRIS + letters.get(i) + RESET);
            }
        }
        return result.toString();
    }

    public boolean verifyWord(String word)
    {
        return lawordslist.contains(word) || tawordslist.contains(word);
    }

    public void gameOrchestrator() throws IOException
    {
        int cont = 0;
        loadWords(lawords, lawordslist);
        loadWords(tawords, tawordslist);
        List<String> word = selectWord(lawordslist);
        boolean flag = true;
        boolean notfinished = true;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (notfinished)
        {
            flag = true;
            System.out.println("Introduce a 5-letter word: ");
        List<String> letters = new ArrayList<>();
        while (flag)
        {
            String input = reader.readLine().toLowerCase();
            letters = new ArrayList<>(Arrays.asList(input.split("")));
            if (letters.size() != 5)
            {
                System.out.println("The word has to have 5 letters");
            }
            else if (!verifyWord(input))
            {
                System.out.println("The word is not on the list");
            }
            else{
                flag = false;
                cont++;
            }
        }
        System.out.println(gameLogic(word, letters));
        if (word.equals(letters))
        {
            System.out.println("Correct!");
            notfinished = false;
        }
        if (cont == 6)
        {
            System.out.println("You have no more tries");
            notfinished = false;
        }
        }
        return;
    }

}
